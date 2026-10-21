---
name: change-database
description: Use when changing DevPulse Room entities, indexes, FTS, triggers, migrations, SQL queries, transactions, paging or persisted article/user state.
---

# Change DevPulse persistence

Read [plan-change](../plan-change/SKILL.md), AGENTS.md's schema policy and the current Room declarations before editing. Distinguish a schema change, a data write and a query-only change.

## Inspect the current storage path

- [DevPulseDatabase](../../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/DevPulseDatabase.kt) implements database transactions and paging-source providers; [DevPulseDatabaseTransactionAccessor](../../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/transaction/DevPulseDatabaseTransactionAccessor.kt) exposes readTransaction/writeTransaction to repositories.
- [LocalFeedPostSqlQueryBuilder](../../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/LocalFeedPostSqlQueryBuilder.kt) binds values through RoomRawQuery and typed SqlBinding values, and adds stable ID ordering. Inspect the relevant DAO, query model, cursor and paging provider as well as the builder. Discovery uses explicit DAO queries; do not force all query shapes into one builder.
- [FeedDiscoveryDatabaseTest](../../../core/data/db/src/jvmTest/kotlin/dev/shounakmulay/devpulse/core/data/db/FeedDiscoveryDatabaseTest.kt) exercises real Room paging, invalidation and reopening. Use its current paging/reopen assertions; inspect the test before copying any version-specific expectation.

## Schema decision before implementation

1. Read [SchemaVersions](../../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/SchemaVersions.kt) and the actual @Database declaration, exports and custom triggers. The current working tree declares only BASE = 1; inspect its matching export, registered schema and tests. Do not infer release verification from that declaration or reintroduce the retired lineage.
2. For the one-time entity/user-state redesign, follow AGENTS.md: finish its new BASE = 1 schema/export and retire that development lineage, without a legacy migration/backfill. Do not perform a reset as incidental work on another feature.
3. Once that baseline is established, every schema change requires a new version, a new immutable export and a registered, tested migration. Indexes, FKs, FTS and triggers count even when annotations miss them. Never reset again or add destructive migration fallback.
4. Query-only changes and ordinary writes in an unchanged schema do not require a version increment. Explain which category applies with the actual affected tables/columns/triggers.

## Implement

Use the existing DAO -> repository -> use-case -> observation path. Preserve publisher fields, user-controlled state and stable local IDs according to CONTEXT.md. Name the columns each write owns. Prefer an explicit column update over an upsert that overwrites unrelated ownership.

Keep related writes in writeTransaction; use readTransaction when a consistent multi-read view is required. Keep network calls outside database transactions. Preserve foreign keys, deletion effects, FTS synchronization and invalidation of all observed tables. Bind user values; do not interpolate search text, URLs or IDs into SQL.

For pagination, keep filter/sort semantics consistent across initial, forward, backward and anchor refresh queries. Include a stable tie-breaker, verify the anchor still belongs to the active scope, and avoid silently restarting pages when cursor types disagree. Preserve the established empty-search behavior and FTS sanitization.

## Persistence failure and state ownership

DAO, transaction and repository operations return plain T/Unit/Flow<T>. Database failures throw unchanged to the use case; do not catch, return Result, log-and-swallow, or return empty rows after a failed query. The use case invokes the I/O dispatcher-catching helper for one-shot work, or applies flowCachingOnDefault to the plain upstream flow. Use finally/use for owned resources; do not swallow a transaction exception and accidentally commit a partial write.

[UserStateDatabaseTest](../../../core/data/db/src/jvmTest/kotlin/dev/shounakmulay/devpulse/core/data/db/UserStateDatabaseTest.kt) establishes the current narrow-write convention: content sync writes publisher-owned rows; bookmarks/read state and feed pin/last-opened state have separate ownership. A read-state update changes only isRead so concurrent bookmark writes survive. Read/unread setters have no revision token; the last committed write determines the value. Missing state is observed as defined defaults. Test repeated setters, defaults, preserved unrelated fields, paging invalidation and reopen through actual Room operations.

## Validate behavior

Use [test-change](../test-change/SKILL.md). Seed tied sort values, duplicate memberships and empty scopes when relevant. Assert literal row IDs/order, no duplicates, append/prepend/refresh, updates/deletes and observer invalidation through real Room. SQL-string assertions are supplementary.

After the baseline exists, migration tests start from a populated supported old schema, open the new database, verify retained values/IDs, reopen it and check integrity/foreign keys plus relevant FTS results. For the authorized reset, test the new schema and reopen behavior instead of preserving retired version assumptions.

Preview `bash scripts/verify.sh --test :core:data:db dev.shounakmulay.devpulse.core.data.db.FeedDiscoveryDatabaseTest --plan`. Execution may be refused because the graph contains copyRoomSchemas. For unchanged-schema verification, use a reviewed isolated checkout. For an intentional schema change, inspect the configured Room generation task, run it only within the authorized schema work, and review the exported SQL/version diff before testing. Do not weaken the runner or refresh a baseline to hide a mismatch.

Shared Room/KSP changes also need enabled Android/iOS compilation where available. JVM database evidence does not prove Android upgrade/UI behavior or iOS compilation. Report each platform and any generated-artifact blocker separately.
