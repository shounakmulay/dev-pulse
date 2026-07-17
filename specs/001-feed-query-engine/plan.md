# Implementation Plan: Feed Query Engine

**Branch**: `feat/feed-details` | **Date**: 2026-06-11 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `specs/001-feed-query-engine/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Replace the current feed post query builder with a typed, whitelisted query engine that supports the named filter and sort catalog from the spec, preserves cursor-based pagination for every sort, and makes common performance paths explicit through a named coverage matrix. The implementation will keep feed post retrieval owned by `core:data:db`, expose only a typed query surface through `core:data:feed`, preserve the current post-with-feed metadata projection, and add stored sort/filter support values where expression-based ordering would block predictable performance.

## Technical Context

**Language/Version**: Kotlin 2.3.21, Kotlin Multiplatform shared code in `commonMain`

**Primary Dependencies**: Room3 3.0.0-alpha06, AndroidX Paging 3.5.0, SQLite bundled driver 2.6.2, Koin 4.2.1, kotlinx.coroutines 1.11.0, Kotlin test

**Storage**: Room3 KMP over SQLite with schema export in `core/data/db/schemas`

**Testing**: `kotlin.test` in `commonTest`, focused Gradle compile/test tasks, SQL string/argument tests for query building, cursor paging tests for no duplicates/no gaps

**Target Platform**: Shared KMP targets for Android, iOS, and Desktop JVM

**Project Type**: Kotlin Multiplatform mobile and desktop app with modular clean architecture

**Performance Goals**: Common query combinations show first page under 1 second and next page under 500 ms on a representative dataset of 10,000 posts across 200 feeds

**Constraints**: No offset/page-number feed post pagination; all dynamic query pieces must come from named catalogs; every sort must end with unique post identity; common combinations get explicit index coverage; rare combinations remain correct without indexing every possible combination; current post-with-feed metadata projection must remain usable by existing feed screens

**Scale/Scope**: One feed post query engine spanning `core:data:db`, query models, cursor paging, schema/index support, repository pass-through, and focused tests. New end-user filter UI is out of scope unless later tasks explicitly add it.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

The project constitution file still contains placeholder principles only, so there are no enforceable constitution gates to evaluate for this feature.

Repository-level checks applied instead:

- Clean Architecture direction remains Presentation -> Domain -> Data. Query storage details stay in `core:data:db`; domain/features receive only mapped feed models or typed query inputs.
- Shared KMP behavior belongs in `commonMain`.
- Dynamic SQL must be generated from typed whitelists, not arbitrary caller-provided SQL fragments.
- Cursor pagination remains the required paging strategy for feed posts.
- Schema/index changes must account for exported Room schema JSON.

Pre-Phase 0 status: PASS.

Post-Phase 1 status: PASS. The design artifacts keep storage/query concerns in `core:data:db`, keep repository/domain boundaries explicit, and add no cross-layer dependency reversal.

## Project Structure

### Documentation (this feature)

```text
specs/001-feed-query-engine/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── feed-post-paging-contract.md
│   ├── feed-post-query-contract.md
│   └── performance-coverage-contract.md
└── tasks.md
```

### Source Code (repository root)

```text
core/data/db/
├── src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/
│   ├── dao/FeedContentDao.kt
│   ├── model/feed/LocalRssContentFeedPost.kt
│   ├── model/feed/LocalRssFeed.kt
│   ├── model/feed/LocalRssPostCategory.kt
│   ├── paging/LocalCursorPagingSource.kt
│   ├── paging/LocalRssPostWithFeedMetadataPagingDataProvider.kt
│   └── query/
│       ├── FeedPostQuery.kt
│       ├── FeedPostSqlQueryBuilder.kt
│       ├── FeedPostQueryCoverage.kt
│       └── FeedPostSqlBinding.kt
└── src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/
    ├── paging/
    └── query/

core/data/feed/
├── src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/feed/repository/
└── src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/feed/repository/

core/domain/feed/
└── src/commonMain/kotlin/dev/shounakmulay/devpulse/core/domain/feed/feed/

feature/feed/
└── src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/
```

**Structure Decision**: Implement the query engine in `core:data:db` because it owns Room entities, raw query construction, exported schema, and cursor paging. Use `core:data:feed` only to pass typed query intent into the DB provider and map projections to domain models. Touch `core:domain:feed` and `feature:feed` only when exposing the expanded query options to current callers.

## Complexity Tracking

No constitution violations or justified complexity exceptions.
