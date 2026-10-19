package dev.shounakmulay.devpulse.core.remoteconfig

class ConfigView internal constructor(
    private val catalog: ConfigCatalog,
    private val active: Map<String, CachedConfigEntry>,
    private val cacheKeys: Set<String>,
    private val overrides: Map<String, ConfigValue>,
    val context: ConfigContext,
    val contextGeneration: Long,
    val evaluatedAtMillis: Long,
    val pendingKeys: Set<String>,
    val localOnlyNamespaces: Set<String>,
) {
    fun <T> evaluate(key: ConfigKey<T>): ConfigEvaluation<T> {
        if (!catalog.contains(key)) return default(key, "unregistered_key")
        overrides[key.qualifiedName]?.let { override ->
            key.decode(override)?.let { value ->
                return ConfigEvaluation(
                    value = value,
                    source = ConfigSource.DEBUG_OVERRIDE,
                    reason = "debug_override",
                    revision = null,
                    ageMillis = null,
                    contextGeneration = contextGeneration,
                )
            }
        }
        val entry = active[key.qualifiedName] ?: return default(key, "compiled_default")
        val value = key.decode(entry.value) ?: return default(key, "invalid_active_value")
        val age = (evaluatedAtMillis - entry.fetchedAtMillis).coerceAtLeast(0)
        val stale = evaluatedAtMillis >= entry.expiresAtMillis
        val staleAllowed = when (val policy = key.stalePolicy) {
            ConfigStalePolicy.UseDefault -> false
            is ConfigStalePolicy.AllowStale -> evaluatedAtMillis - entry.expiresAtMillis < policy.maxStaleMillis
        }
        if (stale && !staleAllowed) return default(key, "expired")
        val source = when {
            stale -> ConfigSource.STALE
            key.qualifiedName in cacheKeys -> ConfigSource.CACHE
            else -> ConfigSource.PROVIDER
        }
        return ConfigEvaluation(
            value = value,
            source = source,
            reason = if (stale) "bounded_stale" else "activated",
            revision = entry.revision,
            ageMillis = age,
            contextGeneration = contextGeneration,
        )
    }

    private fun <T> default(key: ConfigKey<T>, reason: String) = ConfigEvaluation(
        value = key.defaultValue,
        source = ConfigSource.DEFAULT,
        reason = reason,
        revision = null,
        ageMillis = null,
        contextGeneration = contextGeneration,
    )
}
