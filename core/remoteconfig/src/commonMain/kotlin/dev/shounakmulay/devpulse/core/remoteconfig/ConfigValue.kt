package dev.shounakmulay.devpulse.core.remoteconfig

import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
sealed interface ConfigValue {
    @Serializable
    data class BooleanValue(val value: Boolean) : ConfigValue

    @Serializable
    data class IntValue(val value: Int) : ConfigValue

    @Serializable
    data class DoubleValue(val value: Double) : ConfigValue

    @Serializable
    data class StringValue(val value: String) : ConfigValue
}

enum class ConfigActivation {
    IMMEDIATE,
    NEXT_SCREEN,
    NEXT_OPERATION,
    NEXT_SESSION,
    APP_START,
}

sealed interface ConfigStalePolicy {
    data object UseDefault : ConfigStalePolicy
    data class AllowStale(val maxStaleMillis: Long) : ConfigStalePolicy {
        init {
            require(maxStaleMillis >= 0)
        }
    }
}

@Serializable
data class ConfigContext(
    val environment: String,
    val contextId: String = "global",
    val attributes: Map<String, String> = emptyMap(),
) {
    init {
        require(environment.isNotBlank())
        require(contextId.isNotBlank())
    }

    internal fun frozen() = copy(attributes = attributes.toMap())
}

@Serializable
data class ConfigSnapshot(
    val namespace: String,
    val environment: String,
    val contextId: String,
    val schemaVersion: Int,
    val revision: String,
    val fetchedAtMillis: Long,
    val expiresAtMillis: Long,
    val values: Map<String, ConfigValue>,
)

interface ConfigProvider {
    val namespace: String
    suspend fun fetch(context: ConfigContext): Result<ConfigSnapshot>
}

class LocalConfigProvider(
    override val namespace: String,
    private val snapshot: (ConfigContext) -> ConfigSnapshot,
) : ConfigProvider {
    override suspend fun fetch(context: ConfigContext): Result<ConfigSnapshot> =
        configResult { snapshot(context) }
}

interface ConfigStore {
    suspend fun read(): Result<String?>
    suspend fun write(value: String): Result<Unit>
    suspend fun clear(): Result<Unit>
}

fun interface ConfigClock {
    fun nowMillis(): Long
}

object SystemConfigClock : ConfigClock {
    override fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()
}

enum class ConfigSource {
    DEFAULT,
    CACHE,
    PROVIDER,
    STALE,
    DEBUG_OVERRIDE,
}

data class ConfigEvaluation<T>(
    val value: T,
    val source: ConfigSource,
    val reason: String,
    val revision: String?,
    val ageMillis: Long?,
    val contextGeneration: Long,
)

data class ConfigIssue(val key: String?, val reason: String)

data class ConfigFetchOutcome(
    val stagedKeys: Set<String>,
    val issues: List<ConfigIssue>,
    val discarded: Boolean = false,
    val localOnly: Boolean = false,
)

internal suspend inline fun <T> configResult(crossinline block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (cancelled: kotlinx.coroutines.CancellationException) {
        throw cancelled
    } catch (failure: Exception) {
        Result.failure(failure)
    }
