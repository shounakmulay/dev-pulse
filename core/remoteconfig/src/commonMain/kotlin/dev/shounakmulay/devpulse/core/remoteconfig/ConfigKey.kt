package dev.shounakmulay.devpulse.core.remoteconfig

sealed class ConfigKey<T>(
    val namespace: String,
    val name: String,
    val defaultValue: T,
    val activation: ConfigActivation,
    val stalePolicy: ConfigStalePolicy,
) {
    val qualifiedName: String = "$namespace.$name"

    init {
        require(namespace.matches(Regex("[a-z][a-z0-9_]*")))
        require(name.matches(Regex("[a-z][a-z0-9_.]*")))
    }

    abstract fun decode(value: ConfigValue): T?
    abstract fun encode(value: T): ConfigValue

    class BooleanKey(
        namespace: String,
        name: String,
        defaultValue: Boolean,
        activation: ConfigActivation = ConfigActivation.IMMEDIATE,
        stalePolicy: ConfigStalePolicy = ConfigStalePolicy.UseDefault,
    ) : ConfigKey<Boolean>(namespace, name, defaultValue, activation, stalePolicy) {
        override fun decode(value: ConfigValue): Boolean? = (value as? ConfigValue.BooleanValue)?.value
        override fun encode(value: Boolean): ConfigValue = ConfigValue.BooleanValue(value)
    }

    class IntKey(
        namespace: String,
        name: String,
        defaultValue: Int,
        val range: IntRange,
        activation: ConfigActivation = ConfigActivation.IMMEDIATE,
        stalePolicy: ConfigStalePolicy = ConfigStalePolicy.UseDefault,
    ) : ConfigKey<Int>(namespace, name, defaultValue, activation, stalePolicy) {
        init {
            require(defaultValue in range)
        }
        override fun decode(value: ConfigValue): Int? =
            (value as? ConfigValue.IntValue)?.value?.takeIf { it in range }
        override fun encode(value: Int): ConfigValue = ConfigValue.IntValue(value)
    }

    class DoubleKey(
        namespace: String,
        name: String,
        defaultValue: Double,
        val range: ClosedFloatingPointRange<Double>,
        activation: ConfigActivation = ConfigActivation.IMMEDIATE,
        stalePolicy: ConfigStalePolicy = ConfigStalePolicy.UseDefault,
    ) : ConfigKey<Double>(namespace, name, defaultValue, activation, stalePolicy) {
        init {
            require(defaultValue.isFinite() && defaultValue in range)
        }
        override fun decode(value: ConfigValue): Double? =
            (value as? ConfigValue.DoubleValue)?.value?.takeIf { it.isFinite() && it in range }
        override fun encode(value: Double): ConfigValue = ConfigValue.DoubleValue(value)
    }

    class StringKey(
        namespace: String,
        name: String,
        defaultValue: String,
        allowedValues: Set<String>? = null,
        val maxLength: Int = 256,
        activation: ConfigActivation = ConfigActivation.IMMEDIATE,
        stalePolicy: ConfigStalePolicy = ConfigStalePolicy.UseDefault,
    ) : ConfigKey<String>(namespace, name, defaultValue, activation, stalePolicy) {
        val allowedValues: Set<String>? = allowedValues?.toSet()

        init {
            require(maxLength >= 0)
            require(defaultValue.length <= maxLength)
            require(this.allowedValues == null || defaultValue in this.allowedValues)
        }
        override fun decode(value: ConfigValue): String? =
            (value as? ConfigValue.StringValue)?.value?.takeIf {
                it.length <= maxLength && (allowedValues == null || it in allowedValues)
            }
        override fun encode(value: String): ConfigValue = ConfigValue.StringValue(value)
    }
}

data class ConfigAtomicGroup(val name: String, val keys: Set<String>)

class ConfigCatalog(
    keys: List<ConfigKey<*>>,
    val schemaVersion: Int = 1,
    atomicGroups: List<ConfigAtomicGroup> = emptyList(),
) {
    val keys: Map<String, ConfigKey<*>> = keys.associateBy { it.qualifiedName }
    val atomicGroups: List<ConfigAtomicGroup> = atomicGroups.map { it.copy(keys = it.keys.toSet()) }
    val namespaces: Set<String> = keys.map { it.namespace }.toSet()

    init {
        require(schemaVersion > 0)
        require(this.keys.size == keys.size)
        require(this.atomicGroups.map { it.name }.distinct().size == this.atomicGroups.size)
        require(this.atomicGroups.flatMap { it.keys }.distinct().size == this.atomicGroups.sumOf { it.keys.size })
        this.atomicGroups.forEach { group ->
            require(group.name.isNotBlank() && group.keys.size >= 2)
            require(group.keys.all { it in this.keys })
            val members = group.keys.map { this.keys.getValue(it) }
            require(members.map { it.namespace }.distinct().size == 1)
            require(members.map { it.activation }.distinct().size == 1)
            require(members.map { it.stalePolicy }.distinct().size == 1)
        }
    }

    internal fun contains(key: ConfigKey<*>): Boolean = keys[key.qualifiedName] === key
}
