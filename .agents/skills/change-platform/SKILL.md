---
name: change-platform
description: Use when changing DevPulse Koin wiring or startup, expect/actual implementations, notifications, permissions, native services, background workers or app entry points.
---

# Change startup and platform integration

Use [plan-change](../plan-change/SKILL.md). Identify the shared contract, every configured actual implementation, DI owner, native registration and lifetime before editing. Separate common compilation from native behavior.

## Startup and DI

- [DevPulseStartup.initialize](../../../composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/startup/DevPulseStartup.kt) configures logging, calls startKoin with platformConfiguration() before includes(koinConfiguration), then resolves services from the returned Koin application. A platform configuration block must not resolve shared definitions before the graph exists.
- [DevPulseKoinApplication](../../../composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/di/DevPulseKoinApplication.kt) owns app module composition. Reuse annotated @Module/@ComponentScan, @Factory/@Single and @KoinViewModel patterns; inspect explicit bindings and generated configuration instead of adding a parallel global container.
- StartupAsyncInitialiser implementations are resolved with getAll<StartupAsyncInitialiser>() and run in ApplicationScope after startKoin returns. Declare the owner and failure/retry semantics of new initialization. Do not start app services from recomposition.

## Shared contracts and lifetimes

Read all actual implementations when a shared signature changes, including appleMain intermediates and JVM implementations. Keep Android/UIKit/Java-native APIs out of commonMain. Use the existing bridge/interface and source-set pattern rather than adding a runtime or native dependency incidentally.

Screen work ends with the ViewModel/subscription. Services and workers own longer work; application-scoped work needs an application owner. Preserve cancellation and cleanup on success, ordinary failure and cancellation. State whether repeated starts/retries are idempotent and which actor serializes shared processing.

## Native exceptions and framework results

Platform services/clients/repositories below use cases return plain values and let native failures throw; no local catching, Result wrapping or success fallback. Keep any deliberate best-effort decision in a use case, with DispatcherProvider-based exception conversion. Move background work that needs error policy behind a use case instead of invoking a catching helper in a lower service/repository.

A worker or other entry-point adapter can consume a use-case Result and translate it into the framework-required completion/retry type. That adapter is above the use case; it does not justify returning Kotlin Result from repositories or moving business recovery into a native implementation. Test cancellation separately from ordinary retryable failure. Preserve post-startKoin resolution and getAll<StartupAsyncInitialiser>() ordering while changing startup behavior.

## Background feed work

[FeedSyncWorker.doWork](../../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/sync/FeedSyncWorker.kt) first reads the background-sync preference, enqueues work, awaits queueExecutor.process(), and currently catches notification errors outside a dedicated use case. This entry-point adapter is above use cases; its local handling does not establish the required use-case boundary for new business recovery and must not be copied into repositories or lower services. Preserve the distinction between enqueued and completed work; do not report success while work merely starts in another scope. Read the actual worker-result mapper and native attempt-count mapping before changing retry behavior. Existing worker maps use Any at a legacy boundary; do not spread that type into new domain APIs.

[FeedImportForegroundService.onStartCommand](../../../feature/feed/src/androidMain/kotlin/dev/shounakmulay/devpulse/feature/feed/screens/addfeed/ui/service/FeedImportBackgroundProcessLauncher.android.kt) shows the current service/queue/notification integration. It uses serviceScope and processingMutex; it is not a fully proven service-lifecycle blueprint. Review start legality, repeated starts, failure cleanup and stop behavior rather than copying the block wholesale.

## Notifications and permissions

Trace the exact notification passed to startForeground and every channel initialization/ID. [AndroidNotificationBuilder.createBaseBuilder](../../../core/notifications/src/androidMain/kotlin/dev/shounakmulay/devpulse/core/notifications/AndroidNotificationBuilder.kt) chooses the small icon; verify the referenced drawable is packaged and suitable. Check manifest service declarations/types and permissions against the affected platform/version. Do not infer foreground-service acceptance from an ordinary notification succeeding.

Inspect existing permission states, including denied/permanently denied paths, and native TODO branches before presenting a flow as complete. Keep platform limitations visible; do not silently turn an unsupported operation into success.

## Validate

Use [test-change](../test-change/SKILL.md) for use-case Result conversion, framework-adapter mapping, preference gating, cancellation and duplicate-start behavior, and [verify-change](../verify-change/SKILL.md) for task selection. Preview and then enable platform checks explicitly:

```bash
DEVPULSE_VERIFY_ANDROID=1 DEVPULSE_VERIFY_IOS=1 bash scripts/verify.sh --base <reviewed-integration-ref> --plan
```

Inspect the planned task graph before running it. Native/link/signing tasks may write tracked configuration and need reviewed isolation. Do not install signing credentials or access production services as a side effect of verification.

For native behavior, exercise the relevant operation on an available authorized device/simulator: fresh start, repeated start, denied permission, background/foreground, cancellation and cold reopen as applicable. Capture the specific failure and owner. If that environment is unavailable, finish independent code/tests and report native runtime validation as missing.
