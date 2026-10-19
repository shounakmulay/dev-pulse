package dev.shounakmulay.devpulse.core.remoteconfig

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RemoteConfigTest {
    private val enabled = ConfigKey.BooleanKey("app", "enabled", false)
    private val count = ConfigKey.IntKey("app", "count", 2, 1..10)
    private val ratio = ConfigKey.DoubleKey("app", "ratio", 0.1, 0.0..0.1)
    private val context = ConfigContext(environment = "test")

    @Test
    fun `Given no provider When initialized and fetched Then compiled defaults remain usable`() = runTest {
        val config = config(store = MemoryStore())
        assertEquals(false, config.evaluate(enabled).value)
        config.initialize().getOrThrow()
        assertTrue(config.fetch("app").getOrThrow().localOnly)
        assertEquals(ConfigSource.DEFAULT, config.evaluate(enabled).source)
        assertEquals(setOf("app"), config.snapshot().localOnlyNamespaces)
        assertEquals(2, config.evaluate(count).value)
    }

    @Test
    fun `Given fetched config When no activation occurs Then existing values remain active`() = runTest {
        val store = MemoryStore()
        val config = config(store, provider { snapshot(mapOf("enabled" to ConfigValue.BooleanValue(true))) })
        config.fetch("app").getOrThrow()
        assertFalse(config.evaluate(enabled).value)
        assertEquals(setOf("app.enabled"), config.snapshot().pendingKeys)
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        assertTrue(config.evaluate(enabled).value)
        assertEquals("r1", config.evaluate(enabled).revision)
        assertTrue(config.snapshot().pendingKeys.isEmpty())
        assertNotNull(store.value)
    }

    @Test
    fun `Given unknown keys and invalid typed values When activated Then defaults and safe limits remain`() = runTest {
        val mode = ConfigKey.StringKey("app", "mode", "safe", setOf("safe", "fast"))
        val catalog = ConfigCatalog(listOf(enabled, count, ratio, mode))
        val config = RemoteConfig(
            catalog = catalog,
            store = MemoryStore(),
            initialContext = context,
            providers = listOf(provider {
                snapshot(mapOf(
                    "enabled" to ConfigValue.StringValue("true"),
                    "count" to ConfigValue.IntValue(11),
                    "ratio" to ConfigValue.DoubleValue(0.9),
                    "mode" to ConfigValue.StringValue("unsafe"),
                    "unknown" to ConfigValue.BooleanValue(true),
                ))
            }),
            clock = ConfigClock { 1000 },
        )
        val result = config.fetch("app").getOrThrow()
        assertEquals(5, result.issues.size)
        assertTrue(result.stagedKeys.isEmpty())
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        assertFalse(config.evaluate(enabled).value)
        assertEquals(2, config.evaluate(count).value)
        assertEquals(0.1, config.evaluate(ratio).value)
        assertEquals("safe", config.evaluate(mode).value)
    }

    @Test
    fun `Given malformed schema or context When fetched Then the whole snapshot is rejected`() = runTest {
        val invalid = listOf(
            snapshot(emptyMap()).copy(namespace = "other"),
            snapshot(emptyMap()).copy(environment = "production"),
            snapshot(emptyMap()).copy(contextId = "old-user"),
            snapshot(emptyMap()).copy(schemaVersion = 2),
            snapshot(emptyMap()).copy(expiresAtMillis = 500),
            snapshot(emptyMap()).copy(fetchedAtMillis = 2000),
        )
        invalid.forEach { incoming ->
            val config = config(MemoryStore(), provider { incoming })
            assertTrue(config.fetch("app").isFailure)
            assertFalse(config.evaluate(enabled).value)
            assertTrue(config.snapshot().pendingKeys.isEmpty())
        }
    }

    @Test
    fun `Given an invalid member of an atomic group When fetched Then both previous values remain`() = runTest {
        var values = mapOf("enabled" to ConfigValue.BooleanValue(true), "count" to ConfigValue.IntValue(4))
        val config = config(
            store = MemoryStore(),
            provider = provider { snapshot(values) },
            catalog = groupedCatalog(),
        )
        config.fetch("app").getOrThrow()
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        val before = config.snapshot()
        values = mapOf("enabled" to ConfigValue.BooleanValue(false), "count" to ConfigValue.IntValue(100))
        assertEquals("invalid_atomic_group", config.fetch("app").getOrThrow().issues.last().reason)
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        assertTrue(config.evaluate(enabled).value)
        assertEquals(4, config.evaluate(count).value)
        assertTrue(before.evaluate(enabled).value)
        assertEquals(4, before.evaluate(count).value)
    }

    @Test
    fun `Given a partial atomic group When fetched Then none of that group is staged`() = runTest {
        val config = config(
            store = MemoryStore(),
            provider = provider { snapshot(mapOf("enabled" to ConfigValue.BooleanValue(true))) },
            catalog = groupedCatalog(),
        )
        assertTrue(config.fetch("app").getOrThrow().stagedKeys.isEmpty())
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        assertFalse(config.evaluate(enabled).value)
        assertEquals(2, config.evaluate(count).value)
    }

    @Test
    fun `Given activated cache When restarted offline Then validated activated values are restored`() = runTest {
        val store = MemoryStore()
        val first = config(store, provider { snapshot(mapOf("enabled" to ConfigValue.BooleanValue(true))) })
        first.fetch("app").getOrThrow()
        first.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        val restarted = config(store)
        restarted.initialize().getOrThrow()
        assertTrue(restarted.evaluate(enabled).value)
        assertEquals(ConfigSource.CACHE, restarted.evaluate(enabled).source)
    }

    @Test
    fun `Given pending restart config When restarted Then it waits for the declared activation`() = runTest {
        val restartKey = ConfigKey.BooleanKey("app", "restart", false, ConfigActivation.APP_START)
        val catalog = ConfigCatalog(listOf(restartKey))
        val store = MemoryStore()
        val first = config(store, provider { snapshot(mapOf("restart" to ConfigValue.BooleanValue(true))) }, catalog)
        first.fetch("app").getOrThrow()
        val restarted = config(store = store, catalog = catalog)
        restarted.initialize().getOrThrow()
        assertFalse(restarted.evaluate(restartKey).value)
        restarted.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        assertFalse(restarted.evaluate(restartKey).value)
        restarted.activate(ConfigActivation.APP_START).getOrThrow()
        assertTrue(restarted.evaluate(restartKey).value)
        assertEquals(ConfigSource.CACHE, restarted.evaluate(restartKey).source)
    }

    @Test
    fun `Given cached pending values When replaced by a new fetch Then activation reports provider provenance`() = runTest {
        val store = MemoryStore()
        val first = config(store, provider { snapshot(mapOf("enabled" to ConfigValue.BooleanValue(true))) })
        first.fetch("app").getOrThrow()
        val restarted = config(store, provider { snapshot(mapOf("enabled" to ConfigValue.BooleanValue(false))) })
        restarted.initialize().getOrThrow()
        restarted.fetch("app").getOrThrow()
        restarted.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        assertFalse(restarted.evaluate(enabled).value)
        assertEquals(ConfigSource.PROVIDER, restarted.evaluate(enabled).source)
    }

    @Test
    fun `Given corrupt incompatible or invalid cache When restored Then it is discarded`() = runTest {
        val entry = CachedConfigEntry(ConfigValue.BooleanValue(true), "r1", 500, 2000)
        val valid = CachedConfig(
            schemaVersion = 1,
            context = context,
            active = mapOf("app.enabled" to entry),
            previous = emptyMap(),
            pending = emptyMap(),
        )
        val rawCaches = listOf(
            "{not-json}",
            ConfigCacheCodec.encode(valid.copy(schemaVersion = 2)),
            ConfigCacheCodec.encode(valid.copy(formatVersion = 2)),
            ConfigCacheCodec.encode(valid.copy(active = mapOf("app.enabled" to entry.copy(value = ConfigValue.IntValue(1))))),
            ConfigCacheCodec.encode(valid.copy(active = mapOf("app.enabled" to entry.copy(expiresAtMillis = 100)))),
            ConfigCacheCodec.encode(valid.copy(active = mapOf("app.unknown" to entry))),
        )
        rawCaches.forEach { raw ->
            val store = MemoryStore(raw)
            val config = config(store)
            config.initialize().getOrThrow()
            assertFalse(config.evaluate(enabled).value)
            assertNull(store.value)
        }
    }

    @Test
    fun `Given cached partial atomic group When restored Then the cache is rejected`() = runTest {
        val cache = CachedConfig(
            schemaVersion = 1,
            context = context,
            active = mapOf("app.enabled" to CachedConfigEntry(ConfigValue.BooleanValue(true), "r1", 500, 2000)),
            previous = emptyMap(),
            pending = emptyMap(),
        )
        val store = MemoryStore(ConfigCacheCodec.encode(cache))
        val config = config(store = store, catalog = groupedCatalog())
        config.initialize().getOrThrow()
        assertFalse(config.evaluate(enabled).value)
        assertNull(store.value)
    }

    @Test
    fun `Given cache for another environment When restored Then it cannot influence current values`() = runTest {
        val store = MemoryStore()
        val first = config(store, provider { snapshot(mapOf("enabled" to ConfigValue.BooleanValue(true))) })
        first.fetch("app").getOrThrow()
        first.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        val other = RemoteConfig(ConfigCatalog(listOf(enabled)), store, ConfigContext("production"), clock = ConfigClock { 1000 })
        other.initialize().getOrThrow()
        assertFalse(other.evaluate(enabled).value)
    }

    @Test
    fun `Given expired values When evaluated Then strict defaults and bounded stale policies apply`() = runTest {
        val cosmetic = ConfigKey.IntKey(
            namespace = "app",
            name = "cosmetic",
            defaultValue = 2,
            range = 1..10,
            stalePolicy = ConfigStalePolicy.AllowStale(500),
        )
        var now = 1000L
        val config = RemoteConfig(
            catalog = ConfigCatalog(listOf(enabled, cosmetic)),
            store = MemoryStore(),
            initialContext = context,
            providers = listOf(provider { snapshot(mapOf("enabled" to ConfigValue.BooleanValue(true), "cosmetic" to ConfigValue.IntValue(7))) }),
            clock = ConfigClock { now },
        )
        config.fetch("app").getOrThrow()
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        now = 2000
        assertFalse(config.evaluate(enabled).value)
        assertEquals("expired", config.evaluate(enabled).reason)
        assertEquals(7, config.evaluate(cosmetic).value)
        assertEquals(ConfigSource.STALE, config.evaluate(cosmetic).source)
        now = 2500
        assertEquals(2, config.evaluate(cosmetic).value)
    }

    @Test
    fun `Given an observed expiring value When time passes Then the flow publishes its safe default`() = runTest {
        val config = RemoteConfig(
            catalog = ConfigCatalog(listOf(enabled)),
            store = MemoryStore(),
            initialContext = context,
            providers = listOf(provider { snapshot(mapOf("enabled" to ConfigValue.BooleanValue(true)), fetchedAt = 0, expiresAt = 100) }),
            clock = ConfigClock { testScheduler.currentTime },
        )
        config.fetch("app").getOrThrow()
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        val seen = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            config.observe(enabled).collect { seen.add(it.value) }
        }
        advanceTimeBy(100)
        runCurrent()
        assertEquals(listOf(true, false), seen)
    }

    @Test
    fun `Given a fetch failure When refreshed Then active values remain available`() = runTest {
        var fail = false
        val provider = object : ConfigProvider {
            override val namespace = "app"
            override suspend fun fetch(context: ConfigContext): Result<ConfigSnapshot> =
                if (fail) Result.failure(IllegalStateException("offline"))
                else Result.success(snapshot(mapOf("enabled" to ConfigValue.BooleanValue(true))))
        }
        val config = config(MemoryStore(), provider)
        config.fetch("app").getOrThrow()
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        fail = true
        assertTrue(config.fetch("app").isFailure)
        assertTrue(config.evaluate(enabled).value)
    }

    @Test
    fun `Given a failed activation write When activated Then observers retain the previous snapshot`() = runTest {
        val store = MemoryStore()
        val config = config(store, provider { snapshot(mapOf("enabled" to ConfigValue.BooleanValue(true))) })
        config.fetch("app").getOrThrow()
        store.failWrites = true
        assertTrue(config.activate(ConfigActivation.IMMEDIATE).isFailure)
        assertFalse(config.evaluate(enabled).value)
        assertEquals(setOf("app.enabled"), config.snapshot().pendingKeys)
        store.failWrites = false
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        assertTrue(config.evaluate(enabled).value)
    }

    @Test
    fun `Given two activated revisions When rolled back Then one coherent previous snapshot is restored`() = runTest {
        var revision = "newer"
        var value = true
        val config = config(MemoryStore(), provider {
            snapshot(mapOf("enabled" to ConfigValue.BooleanValue(value))).copy(revision = revision)
        })
        config.fetch("app").getOrThrow()
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        revision = "older-opaque-revision"
        value = false
        config.fetch("app").getOrThrow()
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        assertFalse(config.evaluate(enabled).value)
        assertEquals("older-opaque-revision", config.evaluate(enabled).revision)
        config.rollback().getOrThrow()
        assertTrue(config.evaluate(enabled).value)
        assertEquals("newer", config.evaluate(enabled).revision)
    }

    @Test
    fun `Given a screen-bound value When immediate activation runs Then it waits for screen entry`() = runTest {
        val layout = ConfigKey.StringKey("app", "layout", "list", setOf("list", "grid"), activation = ConfigActivation.NEXT_SCREEN)
        val config = config(MemoryStore(), provider { snapshot(mapOf("layout" to ConfigValue.StringValue("grid"))) }, ConfigCatalog(listOf(layout)))
        config.fetch("app").getOrThrow()
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        assertEquals("list", config.evaluate(layout).value)
        config.activate(ConfigActivation.NEXT_SCREEN).getOrThrow()
        assertEquals("grid", config.evaluate(layout).value)
    }

    @Test
    fun `Given two fetches When the older response arrives last Then it cannot overwrite the newer response`() = runTest {
        val firstStarted = CompletableDeferred<Unit>()
        val releaseFirst = CompletableDeferred<Unit>()
        var calls = 0
        val provider = object : ConfigProvider {
            override val namespace = "app"
            override suspend fun fetch(context: ConfigContext): Result<ConfigSnapshot> {
                calls += 1
                return if (calls == 1) {
                    firstStarted.complete(Unit)
                    releaseFirst.await()
                    Result.success(snapshot(mapOf("enabled" to ConfigValue.BooleanValue(false))))
                } else {
                    Result.success(snapshot(mapOf("enabled" to ConfigValue.BooleanValue(true))))
                }
            }
        }
        val config = config(MemoryStore(), provider)
        val old = async { config.fetch("app").getOrThrow() }
        firstStarted.await()
        config.fetch("app").getOrThrow()
        releaseFirst.complete(Unit)
        assertTrue(old.await().discarded)
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        assertTrue(config.evaluate(enabled).value)
    }

    @Test
    fun `Given a context change during fetch When the old response arrives Then old values are discarded`() = runTest {
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val provider = object : ConfigProvider {
            override val namespace = "app"
            override suspend fun fetch(context: ConfigContext): Result<ConfigSnapshot> {
                started.complete(Unit)
                release.await()
                return Result.success(snapshot(mapOf("enabled" to ConfigValue.BooleanValue(true))))
            }
        }
        val config = config(MemoryStore(), provider)
        val old = async { config.fetch("app").getOrThrow() }
        started.await()
        config.changeContext(context.copy(attributes = mapOf("channel" to "other"))).getOrThrow()
        release.complete(Unit)
        assertTrue(old.await().discarded)
        assertFalse(config.evaluate(enabled).value)
        assertEquals(1, config.evaluate(enabled).contextGeneration)
    }

    @Test
    fun `Given active context values and debug overrides When context changes Then both reset immediately`() = runTest {
        val config = config(MemoryStore(), provider { snapshot(mapOf("enabled" to ConfigValue.BooleanValue(true))) }, debug = true)
        config.fetch("app").getOrThrow()
        config.activate(ConfigActivation.IMMEDIATE).getOrThrow()
        config.setOverride(count, 8).getOrThrow()
        config.changeContext(context.copy(environment = "other")).getOrThrow()
        assertFalse(config.evaluate(enabled).value)
        assertEquals(2, config.evaluate(count).value)
    }

    @Test
    fun `Given production mode When overrides are attempted Then values remain unchanged`() = runTest {
        val config = config(MemoryStore())
        assertTrue(config.setOverride(enabled, true).isFailure)
        assertFalse(config.evaluate(enabled).value)
    }

    @Test
    fun `Given debug overrides When a value exceeds its safety range Then it is rejected`() = runTest {
        val config = config(store = MemoryStore(), debug = true)
        assertTrue(config.setOverride(ratio, 1.0).isFailure)
        config.setOverride(count, 9).getOrThrow()
        assertEquals(9, config.evaluate(count).value)
        assertEquals(ConfigSource.DEBUG_OVERRIDE, config.evaluate(count).source)
        config.clearOverrides().getOrThrow()
        assertEquals(2, config.evaluate(count).value)
    }

    @Test
    fun `Given a foreign key with the same qualified name When overridden Then the canonical value is unchanged`() = runTest {
        val foreignKey = ConfigKey.IntKey("app", "count", 1, 0..100)
        val config = config(store = MemoryStore(), debug = true)
        assertTrue(config.setOverride(foreignKey, 9).isFailure)
        assertEquals(2, config.evaluate(count).value)
        assertEquals(ConfigSource.DEFAULT, config.evaluate(count).source)
        config.setOverride(count, 9).getOrThrow()
        assertEquals(9, config.evaluate(count).value)
    }

    @Test
    fun `Given grouped debug keys When a partial override is attempted Then the group remains unchanged`() = runTest {
        val config = config(store = MemoryStore(), catalog = groupedCatalog(), debug = true)
        assertTrue(config.setOverride(enabled, true).isFailure)
        assertFalse(config.evaluate(enabled).value)
        config.setOverrides(mapOf("app.enabled" to ConfigValue.BooleanValue(true), "app.count" to ConfigValue.IntValue(8))).getOrThrow()
        assertTrue(config.evaluate(enabled).value)
        assertEquals(8, config.evaluate(count).value)
    }

    @Test
    fun `Given provider cancellation When fetched Then cancellation propagates`() = runTest {
        val provider = object : ConfigProvider {
            override val namespace = "app"
            override suspend fun fetch(context: ConfigContext): Result<ConfigSnapshot> = throw CancellationException("cancelled")
        }
        val config = config(MemoryStore(), provider)
        assertFailsWith<CancellationException> { config.fetch("app") }
    }

    @Test
    fun `Given duplicate providers or incompatible group policy When composed Then configuration is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            RemoteConfig(ConfigCatalog(listOf(enabled)), MemoryStore(), context, listOf(provider { snapshot(emptyMap()) }, provider { snapshot(emptyMap()) }))
        }
        val deferred = ConfigKey.IntKey("app", "deferred", 1, 1..10, ConfigActivation.NEXT_SCREEN)
        assertFailsWith<IllegalArgumentException> {
            ConfigCatalog(listOf(enabled, deferred), atomicGroups = listOf(ConfigAtomicGroup("group", setOf("app.enabled", "app.deferred"))))
        }
    }

    private fun config(
        store: MemoryStore,
        provider: ConfigProvider? = null,
        catalog: ConfigCatalog = ConfigCatalog(listOf(enabled, count, ratio)),
        debug: Boolean = false,
    ) = RemoteConfig(
        catalog = catalog,
        store = store,
        initialContext = context,
        providers = listOfNotNull(provider),
        clock = ConfigClock { 1000 },
        debugOverridesEnabled = debug,
    )

    private fun groupedCatalog() = ConfigCatalog(
        keys = listOf(enabled, count),
        atomicGroups = listOf(ConfigAtomicGroup("pair", setOf("app.enabled", "app.count"))),
    )

    private fun provider(block: (ConfigContext) -> ConfigSnapshot): ConfigProvider = LocalConfigProvider("app", block)

    private fun snapshot(
        values: Map<String, ConfigValue>,
        fetchedAt: Long = 500,
        expiresAt: Long = 2000,
    ) = ConfigSnapshot(
        namespace = "app",
        environment = "test",
        contextId = "global",
        schemaVersion = 1,
        revision = "r1",
        fetchedAtMillis = fetchedAt,
        expiresAtMillis = expiresAt,
        values = values,
    )

    private class MemoryStore(initial: String? = null) : ConfigStore {
        var value = initial
        var failWrites = false
        override suspend fun read(): Result<String?> = Result.success(value)
        override suspend fun write(value: String): Result<Unit> =
            if (failWrites) Result.failure(IllegalStateException("disk full"))
            else Result.success(Unit).also { this.value = value }
        override suspend fun clear(): Result<Unit> = Result.success(Unit).also { value = null }
    }
}
