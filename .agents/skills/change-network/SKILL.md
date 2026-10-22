---
name: change-network
description: Use when changing DevPulse HTTP clients, transport responses, serialization, remote feed parsing or discovery catalog download and reconciliation.
---

# Change remote data behavior

Use [plan-change](../plan-change/SKILL.md) and inspect the caller's status/error contract, configured engine, parser/mapper, storage write and tests before editing. Fetching data successfully is not evidence that reconciliation committed it correctly.

## Existing boundary

- [DevPulseNetworkClient](../../../core/network/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/network/DevPulseNetworkClient.kt) exposes status/headers/text and typed decoding through its response contract. [KtorDevPulseNetworkResponse.body](../../../core/network/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/network/KtorDevPulseNetworkResponse.kt) preserves KClass plus KType through Ktor TypeInfo, including generic types.
- [createHttpClient](../../../core/network/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/network/DevPulseHttpClient.kt) owns content negotiation, redirect policy, content encoding and transport logging. Current logging is LogLevel.ALL with Authorization/Cookie sanitization; do not treat that as permission to log bodies, credentials or user URLs in new code.
- core/network/build.gradle.kts selects OkHttp for Android/JVM and Darwin for iOS. Reuse the injected client; do not create a client per call or expose Ktor HttpResponse/HttpClient types through new domain/presentation APIs. Existing unused transport imports are not an API design example.

## Implement at the owning boundary

Keep parsing and transport details in the network/data implementation, domain decisions in use cases and UI mapping in feature interactors. Reuse kotlinx.serialization and the existing configured serializer instead of constructing a separate serializer that changes generic decoding or unknown-key behavior.

Define accepted HTTP statuses and empty/malformed responses explicitly. Treat header names case-insensitively and preserve header values such as weak ETags exactly. Let transport/data exceptions and cancellation propagate to the use case, bound concurrent fetching, and avoid network calls inside write transactions. Close resources owned by the new code; do not close an injected application client per request.

For parser changes, test supported formats and malformed/partial input through the actual parser/mapper path. Express any deliberate recoverable fallback in the use case while preserving its requested behavior and reader-owned fields during sync; do not implement fallback by catching exceptions below that boundary. Avoid building a second processing or paging path.

## Transport failures belong to the use-case boundary

Clients, response wrappers, parsers, mappers and repositories return plain decoded data/Unit/Flow<T> and throw on technical failure. Do not add Result, runCatching, try/catch, Flow.catch or log-and-return-empty handling there, even if an older parser/repository does so. A use case chooses runCachingOnIO/runCatchingOnDefault or flowCachingOnDefault and exposes Result<T>/Flow<Result<T>> to its caller. Test the transport/repository exception separately from the use-case failure result, including malformed bodies and upstream Flow failures. An expected empty payload or nullable missing resource is a domain value only when the contract explicitly allows it.

## Discovery reconciliation

Read [FeedDiscoveryRepositoryImpl.refresh](../../../core/data/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/feed/repository/FeedDiscoveryRepositoryImpl.kt) and its fetchMasterJson, getCollectionsToProcessForRefresh and persistDiscoveryRefreshData helpers. They compare collection hashes, fetch changed collections, update collection metadata and replace membership inside writeTransaction.

Exercise unchanged catalog, added/changed/deleted collection, reorder-only and partial fetch failure cases. A changed collection must update its own SHA/title/count/path as well as feed rows and mappings. Persist the new master/ETag with the completed transaction so a failed refresh cannot advertise an uncommitted catalog. Preserve subscribed user feeds independently of catalog membership.

For conditional requests, verify both the HTTP outcome and the repository outcome. A bare 304 log does not prove a successful unchanged-catalog return. Inspect the current client cache configuration and test restart behavior when touching cache/ETag logic; MockEngine decoding tests alone do not establish it.

## Validate and finish

[DevPulseNetworkClientTest](../../../core/network/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/network/DevPulseNetworkClientTest.kt) covers mixed-case header lookup, configured generic decoding and text bodies through MockEngine. Keep the real response wrapper and serializer under test; mock only transport/external boundaries. Add negative status/body cases relevant to the requested change.

```bash
bash scripts/verify.sh --test :core:network dev.shounakmulay.devpulse.core.network.DevPulseNetworkClientTest
```

Use [test-change](../test-change/SKILL.md) for repository/transaction failure cases and [verify-change](../verify-change/SKILL.md) for affected consumers/platforms. Tests are offline fixtures unless live network verification is explicitly within scope. Do not claim live endpoint, restart-cache or commit behavior from a transport unit test. Review logs and the final diff for newly exposed user data.
