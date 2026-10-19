package dev.shounakmulay.devpulse.core.remoteconfig

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RemoteConfig(
    private val catalog: ConfigCatalog,
    private val store: ConfigStore,
    initialContext: ConfigContext,
    providers: List<ConfigProvider> = emptyList(),
    private val clock: ConfigClock = SystemConfigClock,
    private val debugOverridesEnabled: Boolean = false,
) {
    private val providers = providers.associateBy { it.namespace }
    private val mutex = Mutex()
    private val state = MutableStateFlow(RuntimeState(context = initialContext.frozen()))
    private val requests = mutableMapOf<String, Long>()
    private var nextRequest = 0L
    private var initialized = false

    init {
        require(this.providers.size == providers.size) { "A namespace must have one provider" }
        require(this.providers.keys.all { it in catalog.namespaces }) { "Unknown provider namespace" }
    }

    suspend fun initialize(): Result<Unit> = configResult {
        mutex.withLock {
            if (!initialized) {
                initialized = true
                restoreCache()
            }
        }
    }

    fun snapshot(): ConfigView = view(state.value, clock.nowMillis())

    fun <T> evaluate(key: ConfigKey<T>): ConfigEvaluation<T> = snapshot().evaluate(key)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> observe(key: ConfigKey<T>): Flow<ConfigEvaluation<T>> = state.flatMapLatest { current ->
        flow {
            val now = clock.nowMillis()
            emit(view(current, now).evaluate(key))
            val expiryTimes = current.active[key.qualifiedName]?.let { entry ->
                buildList {
                    add(entry.expiresAtMillis)
                    val stale = key.stalePolicy
                    if (stale is ConfigStalePolicy.AllowStale) {
                        add(safeExpiry(entry.expiresAtMillis, stale.maxStaleMillis))
                    }
                }
            }.orEmpty()
            expiryTimes.filter { it > now }.distinct().sorted().forEach { expiry ->
                delay((expiry - clock.nowMillis()).coerceAtLeast(1))
                emit(view(current, clock.nowMillis()).evaluate(key))
            }
        }
    }.distinctUntilChanged()

    suspend fun fetch(namespace: String): Result<ConfigFetchOutcome> = configResult {
        initialize().getOrThrow()
        require(namespace in catalog.namespaces) { "Unknown config namespace" }
        val provider = providers[namespace]
        if (provider == null) {
            ConfigFetchOutcome(emptySet(), emptyList(), localOnly = true)
        } else {
            val request = mutex.withLock {
                nextRequest += 1
                requests[namespace] = nextRequest
                FetchRequest(state.value.context, state.value.generation, nextRequest)
            }
            val incoming = provider.fetch(request.context).getOrThrow()
            mutex.withLock {
                val current = state.value
                if (current.generation != request.generation || requests[namespace] != request.sequence) {
                    ConfigFetchOutcome(emptySet(), emptyList(), discarded = true)
                } else {
                    val validated = validateSnapshot(namespace, request.context, incoming)
                    val retained = current.pending.filterKeys { catalog.keys.getValue(it).namespace != namespace }
                    val pending = retained + validated.entries
                    val next = current.copy(
                        pending = pending,
                        pendingCacheKeys = current.pendingCacheKeys.intersect(retained.keys),
                    )
                    persist(next)
                    state.value = next
                    ConfigFetchOutcome(validated.entries.keys, validated.issues)
                }
            }
        }
    }

    suspend fun activate(boundary: ConfigActivation): Result<Unit> = configResult {
        initialize().getOrThrow()
        mutex.withLock {
            val current = state.value
            val selected = current.pending.filterKeys { catalog.keys.getValue(it).activation == boundary }
            if (selected.isNotEmpty()) {
                val next = current.copy(
                    active = current.active + selected,
                    previous = current.active,
                    cacheKeys = (current.cacheKeys - selected.keys) + current.pendingCacheKeys.intersect(selected.keys),
                    pending = current.pending - selected.keys,
                    pendingCacheKeys = current.pendingCacheKeys - selected.keys,
                )
                persist(next)
                state.value = next
            }
        }
    }

    suspend fun changeContext(context: ConfigContext): Result<Unit> = configResult {
        mutex.withLock {
            val frozen = context.frozen()
            if (frozen != state.value.context) {
                state.value = RuntimeState(context = frozen, generation = state.value.generation + 1)
                requests.clear()
                initialized = true
                restoreCache()
            }
        }
    }

    suspend fun rollback(): Result<Unit> = configResult {
        initialize().getOrThrow()
        mutex.withLock {
            val current = state.value
            val next = current.copy(
                active = current.previous,
                previous = current.active,
                cacheKeys = current.previous.keys,
                pending = emptyMap(),
                pendingCacheKeys = emptySet(),
            )
            persist(next)
            requests.clear()
            state.value = next
        }
    }

    suspend fun <T> setOverride(key: ConfigKey<T>, value: T): Result<Unit> = configResult {
        require(catalog.contains(key)) { "Unknown config key" }
        setOverrides(mapOf(key.qualifiedName to key.encode(value))).getOrThrow()
    }

    suspend fun setOverrides(values: Map<String, ConfigValue>): Result<Unit> = configResult {
        require(debugOverridesEnabled) { "Debug config overrides are disabled" }
        mutex.withLock {
            val frozen = values.toMap()
            require(frozen.all { (name, value) -> catalog.keys[name]?.decode(value) != null }) {
                "Unknown or invalid override"
            }
            catalog.atomicGroups.forEach { group ->
                require(frozen.keys.intersect(group.keys).isEmpty() || frozen.keys.containsAll(group.keys)) {
                    "Override must include an entire atomic group"
                }
            }
            state.value = state.value.copy(overrides = state.value.overrides + frozen)
        }
    }

    suspend fun clearOverrides(): Result<Unit> = configResult {
        mutex.withLock {
            state.value = state.value.copy(overrides = emptyMap())
        }
    }

    private suspend fun restoreCache() {
        val raw = store.read().getOrThrow() ?: return
        val decoded = configResult { ConfigCacheCodec.decode(raw) }
        val cache = decoded.getOrNull()
        if (cache == null || !validCache(cache)) {
            store.clear().getOrThrow()
            return
        }
        val current = state.value
        if (cache.context != current.context) return
        state.value = current.copy(
            active = cache.active.toMap(),
            previous = cache.previous.toMap(),
            pending = cache.pending.toMap(),
            cacheKeys = cache.active.keys.toSet(),
            pendingCacheKeys = cache.pending.keys.toSet(),
        )
    }

    private fun validCache(cache: CachedConfig): Boolean =
        cache.formatVersion == 1 && cache.schemaVersion == catalog.schemaVersion &&
            listOf(cache.active, cache.previous, cache.pending).all { entries ->
                entries.all { (name, entry) ->
                    val key = catalog.keys[name]
                    key != null && key.decode(entry.value) != null && validTime(entry)
                } && catalog.atomicGroups.all { group ->
                    val members = entries.filterKeys { it in group.keys }
                    members.isEmpty() || members.keys == group.keys &&
                        members.values.map { Triple(it.revision, it.fetchedAtMillis, it.expiresAtMillis) }.distinct().size == 1
                }
            }

    private fun validateSnapshot(
        namespace: String,
        context: ConfigContext,
        incoming: ConfigSnapshot,
    ): ValidatedSnapshot {
        require(incoming.namespace == namespace && incoming.environment == context.environment &&
            incoming.contextId == context.contextId && incoming.schemaVersion == catalog.schemaVersion) {
            "Snapshot context or schema does not match"
        }
        require(incoming.values.size <= 1024 && incoming.revision.length in 1..256)
        require(incoming.fetchedAtMillis >= 0 && incoming.fetchedAtMillis <= clock.nowMillis() &&
            incoming.expiresAtMillis > incoming.fetchedAtMillis) { "Invalid snapshot time" }
        val issues = mutableListOf<ConfigIssue>()
        val entries = buildMap {
            incoming.values.toMap().forEach { (name, value) ->
                val qualified = "$namespace.$name"
                val key = catalog.keys[qualified]
                when {
                    key == null -> issues.add(ConfigIssue(qualified, "unknown_key"))
                    key.decode(value) == null -> issues.add(ConfigIssue(qualified, "invalid_value"))
                    else -> put(
                        qualified,
                        CachedConfigEntry(value, incoming.revision, incoming.fetchedAtMillis, incoming.expiresAtMillis),
                    )
                }
            }
        }.toMutableMap()
        catalog.atomicGroups.filter { catalog.keys.getValue(it.keys.first()).namespace == namespace }.forEach { group ->
            if (!entries.keys.containsAll(group.keys)) {
                group.keys.forEach(entries::remove)
                issues.add(ConfigIssue(group.name, "invalid_atomic_group"))
            }
        }
        return ValidatedSnapshot(entries.toMap(), issues.toList())
    }

    private fun validTime(entry: CachedConfigEntry): Boolean =
        entry.revision.length in 1..256 && entry.fetchedAtMillis >= 0 &&
            entry.fetchedAtMillis <= clock.nowMillis() && entry.expiresAtMillis > entry.fetchedAtMillis

    private suspend fun persist(current: RuntimeState) {
        val cache = CachedConfig(
            schemaVersion = catalog.schemaVersion,
            context = current.context,
            active = current.active,
            previous = current.previous,
            pending = current.pending,
        )
        store.write(ConfigCacheCodec.encode(cache)).getOrThrow()
    }

    private fun view(current: RuntimeState, now: Long) = ConfigView(
        catalog = catalog,
        active = current.active,
        cacheKeys = current.cacheKeys,
        overrides = current.overrides,
        context = current.context,
        contextGeneration = current.generation,
        evaluatedAtMillis = now,
        pendingKeys = current.pending.keys,
        localOnlyNamespaces = catalog.namespaces - providers.keys,
    )

    private fun safeExpiry(expiry: Long, allowance: Long): Long =
        if (Long.MAX_VALUE - expiry < allowance) Long.MAX_VALUE else expiry + allowance

    private data class FetchRequest(val context: ConfigContext, val generation: Long, val sequence: Long)
    private data class ValidatedSnapshot(val entries: Map<String, CachedConfigEntry>, val issues: List<ConfigIssue>)
    private data class RuntimeState(
        val context: ConfigContext,
        val generation: Long = 0,
        val active: Map<String, CachedConfigEntry> = emptyMap(),
        val previous: Map<String, CachedConfigEntry> = emptyMap(),
        val pending: Map<String, CachedConfigEntry> = emptyMap(),
        val cacheKeys: Set<String> = emptySet(),
        val pendingCacheKeys: Set<String> = emptySet(),
        val overrides: Map<String, ConfigValue> = emptyMap(),
    )
}
