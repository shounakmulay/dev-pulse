package dev.shounakmulay.devpulse.core.telemetry.api

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.collections.immutable.toPersistentSet

sealed interface AttributeValue {
    data class Text(val value: String) : AttributeValue
    data class Integer(val value: Long) : AttributeValue
    data class Decimal(val value: Double) : AttributeValue
    data class Flag(val value: Boolean) : AttributeValue
}

sealed interface AttributeRule {
    class Choice(values: Set<String>) : AttributeRule {
        val values: PersistentSet<String> = values.toPersistentSet()
        init {
            require(values.isNotEmpty() && values.size <= 64)
            require(values.all { it.length in 1..80 && it.matches(Regex("[a-zA-Z0-9_.-]+")) })
        }
    }
    data class Integer(val minimum: Long = 0, val maximum: Long = Long.MAX_VALUE) : AttributeRule {
        init { require(minimum <= maximum) }
    }
    data class Decimal(val minimum: Double = 0.0, val maximum: Double = Double.MAX_VALUE) : AttributeRule {
        init { require(minimum.isFinite() && maximum.isFinite() && minimum <= maximum) }
    }
    data object Flag : AttributeRule
}

class AttributeSchema(rules: Map<String, AttributeRule> = emptyMap()) {
    val rules: PersistentMap<String, AttributeRule> = rules.toPersistentMap()
    init {
        require(rules.size <= 16)
        require(rules.keys.all { it.matches(Regex("[a-z][a-z0-9_.]{0,63}")) })
    }

    fun accepts(attributes: SafeAttributes): Boolean = attributes.values.all { (key, value) ->
        when (val rule = rules[key]) {
            is AttributeRule.Choice -> value is AttributeValue.Text && value.value in rule.values
            is AttributeRule.Integer -> value is AttributeValue.Integer && value.value in rule.minimum..rule.maximum
            is AttributeRule.Decimal -> value is AttributeValue.Decimal && value.value.isFinite() && value.value in rule.minimum..rule.maximum
            AttributeRule.Flag -> value is AttributeValue.Flag
            null -> false
        }
    }

    companion object { val Empty = AttributeSchema() }
}

class SafeAttributes private constructor(val values: PersistentMap<String, AttributeValue>) {
    companion object {
        val Empty = SafeAttributes(persistentMapOf())
        fun of(vararg values: Pair<String, AttributeValue>): SafeAttributes =
            SafeAttributes(values.toMap().toPersistentMap())
    }
}

class MetricAttributes private constructor(val values: PersistentMap<String, String>) {
    fun asSafeAttributes(): SafeAttributes = SafeAttributes.of(
        *values.map { (key, value) -> key to AttributeValue.Text(value) }.toTypedArray()
    )
    companion object {
        val Empty = MetricAttributes(persistentMapOf())
        fun of(vararg values: Pair<String, String>): MetricAttributes =
            MetricAttributes(values.toMap().toPersistentMap())
    }
}

class MetricDimensions(choices: Map<String, Set<String>> = emptyMap()) {
    val choices: PersistentMap<String, PersistentSet<String>> =
        choices.mapValues { (_, values) -> AttributeRule.Choice(values).values }.toPersistentMap()
    init {
        require(choices.size <= 4)
        require(choices.keys.all { it.matches(Regex("[a-z][a-z0-9_.]{0,63}")) })
        require(choices.values.fold(1L) { count, values -> count * values.size } <= 256)
    }
    fun accepts(attributes: MetricAttributes): Boolean =
        attributes.values.all { (key, value) -> value in (choices[key] ?: return@all false) }
    companion object { val Empty = MetricDimensions() }
}

internal fun requireSignalName(name: String) {
    require(name.matches(Regex("[a-z][a-z0-9_.]{0,95}")))
}
