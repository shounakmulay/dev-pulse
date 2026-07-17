# Tasks: Feed Query Engine

**Input**: Design documents from `/specs/001-feed-query-engine/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md, contracts/

**Tests**: Required by FR-021 and the query, paging, and performance contracts. Write story tests before implementation and verify they fail for the current behavior before making the related code changes.

**Organization**: Tasks are grouped by user story so each story can be implemented and tested as an independently useful increment.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel with other marked tasks in the same phase because it touches different files or only adds an independent test file
- **[Story]**: Maps to the user story in spec.md
- **Path requirement**: Every task names the exact file path it creates, edits, or validates

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Prepare shared test fixtures and confirm current implementation seams before schema and query changes.

- [X] T001 Record the current DB schema version and schema export path before entity edits in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/DevPulseDatabase.kt`
- [X] T002 [P] Create reusable query assertion fixtures for SQL/order/binding checks in `core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostQueryFixtures.kt`
- [X] T003 [P] Create reusable paging dataset fixtures for duplicate/gap cursor checks in `core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/paging/FeedPostPagingFixtures.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establish the typed catalog, stored values, category model, and schema support required by all user stories.

**Critical**: No user story work should begin until the foundational catalog and storage shape are in place.

- [X] T004 Replace the current broad sort/direction model with named filter, sort, cursor, and page-size request types in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostQuery.kt`
- [X] T005 Add sort term metadata for each named sort, including SQL expression, direction, missing-value policy, and cursor value type in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostSortSpec.kt`
- [X] T006 Add query validation for page-size caps, explicit empty multi-value filters, cursor sort identity, cursor value count, and cursor value types in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostQueryValidator.kt`
- [X] T007 Add stored sort keys, copied feed sort values, derived media booleans, and common-profile indexes to `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/model/feed/LocalRssContentFeedPost.kt`
- [X] T008 Add feed display sort storage and supporting feed indexes to `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/model/feed/LocalRssFeed.kt`
- [X] T009 Add exact category value storage with normalized category matching indexes in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/model/feed/LocalRssPostCategory.kt`
- [X] T010 Register the category entity and updated entity list in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/DevPulseDatabase.kt`
- [X] T011 Add DAO operations for replacing post category values and refreshing feed-derived post sort columns in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/dao/FeedContentDao.kt`
- [X] T012 Populate post sort keys, author/source sort keys, derived media booleans, and category values from parsed posts in `core/data/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/feed/mapper/RssPostMapper.kt`
- [X] T013 Persist category rows and maintain copied feed display/pinned sort values during feed/post upserts in `core/data/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/feed/repository/ContentFeedRepositoryImpl.kt`
- [X] T014 Regenerate and keep the Room schema export after entity/index changes in `core/data/db/schemas/dev.shounakmulay.devpulse.core.data.db.DevPulseDatabase/1.json`

**Checkpoint**: The DB model, typed query surface, and schema are ready for story implementation.

---

## Phase 3: User Story 1 - Browse A Stable Feed Timeline (Priority: P1) MVP

**Goal**: Deliver deterministic cursor pagination for default and timeline sorts with no offset paging, duplicate posts, skipped posts, or expression-based sort fallbacks.

**Independent Test**: Load a seeded mixed dataset, request `PublishedNewest` with no filters, append multiple pages, refresh around an anchor, and verify the paged ID sequence matches the full ordered result set with no duplicates or gaps.

### Tests for User Story 1

- [X] T015 [P] [US1] Add failing SQL tests proving `PublishedNewest` and `PublishedOldest` use `publishedSortAt`, `updatedAt`, `id`, and never use `COALESCE` or `OFFSET` in `core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostTimelineSqlTest.kt`
- [X] T016 [P] [US1] Add failing generic cursor predicate tests for long, text, boolean-first, and feed-derived sort terms in `core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostCursorPredicateTest.kt`
- [X] T017 [P] [US1] Add failing paging stability tests for initial, append, and refresh-around loads with tied sort values in `core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/paging/FeedPostKeysetPagingTest.kt`

### Implementation for User Story 1

- [X] T018 [US1] Refactor page SQL generation to build `ORDER BY` and keyset cursor predicates from `FeedPostSortSpec` terms in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostSqlQueryBuilder.kt`
- [X] T019 [US1] Replace the current nullable long/text cursor fields with a sort-identity plus ordered value cursor in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/paging/LocalRssPostWithFeedMetadataCursor.kt`
- [X] T020 [US1] Extract cursor values from stored sort keys for every timeline sort in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/paging/LocalRssPostWithFeedMetadataPagingDataProvider.kt`
- [X] T021 [US1] Include `LocalRssPostCategory` in paging invalidation tables while preserving post/feed/tag invalidation in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/paging/LocalRssPostWithFeedMetadataPagingDataProvider.kt`
- [X] T022 [US1] Update recent-post retrieval to use `publishedSortAt`, `updatedAt`, and `id` for deterministic ordering in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/dao/FeedContentDao.kt`

**Checkpoint**: User Story 1 is independently complete when default timeline paging passes query and paging tests with no offset behavior.

---

## Phase 4: User Story 2 - Combine Named Filters And Sorts (Priority: P2)

**Goal**: Support the complete named filter and sort catalog through one safe query builder while preserving the existing post-with-feed metadata projection for current feed screens.

**Independent Test**: Apply each named filter alone, representative combinations, and every named sort against a trusted reference dataset, then verify the SQL uses bound arguments and returns the expected ordered result set.

### Tests for User Story 2

- [X] T023 [P] [US2] Add failing SQL tests for all named filters, deterministic `IN` bindings, explicit empty selected sets, and boolean positive/negative filters in `core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostFilterCatalogSqlTest.kt`
- [X] T024 [P] [US2] Add failing SQL tests for all named sorts and their cursor term order in `core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostSortCatalogSqlTest.kt`
- [X] T025 [P] [US2] Add failing search escaping tests for blank text, punctuation, wildcard-like characters, and case normalization in `core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostSearchSqlTest.kt`
- [X] T026 [P] [US2] Add failing category exact-match tests proving substring-only category matches are excluded in `core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostCategorySqlTest.kt`

### Implementation for User Story 2

- [X] T027 [US2] Generate `WHERE` clauses for `FeedIds`, `TagIdsAny`, `TagIdsAll`, `Bookmarked`, `PinnedFeed`, `PublishedRange`, `UpdatedRange`, `CreatedRange`, media flags, `Author`, `SourceFeed`, `Category`, and `SearchText` in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostSqlQueryBuilder.kt`
- [X] T028 [US2] Add SQL escaping and normalized matching helpers for `SearchText`, `Author`, `SourceFeed`, and `Category` in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostSqlTextMatching.kt`
- [X] T029 [US2] Ensure all caller-provided filter values bind through typed `SqlBinding` values without string interpolation in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/SqlBinding.kt`
- [X] T030 [US2] Update the DB paging provider seam to accept the expanded query spec without exposing SQL details in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/paging/FeedPostPagingSourceProvider.kt`
- [X] T031 [US2] Update the feed data repository interface to pass named feed post query intent instead of fixed feed-only arguments in `core/data/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/feed/repository/ContentFeedRepository.kt`
- [X] T032 [US2] Map repository query intent to the DB `FeedPostQuery` while preserving the existing post-with-feed metadata projection in `core/data/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/feed/repository/ContentFeedRepositoryImpl.kt`
- [X] T033 [US2] Update the domain use case to expose optional typed filters and sort while keeping the current feed-detail default behavior in `core/domain/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/domain/feed/feed/GetPaginatedFeedPostsUseCase.kt`
- [X] T034 [US2] Update current feed feature callers to build the default `PublishedNewest` query shape explicitly in `feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/screens/feeddetail/ui/FeedDetailViewModel.kt`

**Checkpoint**: User Story 2 is independently complete when every named filter and sort is accepted through typed catalogs, invalid inputs fail before execution, and existing feed detail paging still compiles.

---

## Phase 5: User Story 3 - Maintain Predictable Performance Coverage (Priority: P3)

**Goal**: Make common and rare query performance profiles explicit, indexed where common, correctness-tested where rare, and easy for maintainers to review.

**Independent Test**: Run each common coverage profile against a representative dataset, verify the expected access path is selected or measured, and confirm rare profiles remain correct with documented classification.

### Tests for User Story 3

- [x] T035 [P] [US3] Add failing coverage catalog tests for all common optimized and rare supported profiles in `core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostQueryCoverageTest.kt`
- [x] T036 [P] [US3] Add failing common-profile SQL/access-path verification tests for `TimelineLatest`, `FeedTimelineLatest`, `BookmarkedTimelineLatest`, `FeedBookmarkedTimelineLatest`, `PinnedFeedsTimelineLatest`, `UpdatedLatest`, `CreatedLatest`, `TitleAlphabetical`, and `FeedNameAlphabetical` in `core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostPerformanceCoverageTest.kt`
- [x] T037 [P] [US3] Add failing rare-profile correctness tests for search, author, source, category, media, tag-all, and multi-filter combinations in `core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostRareCombinationTest.kt`

### Implementation for User Story 3

- [x] T038 [US3] Add named common and rare performance profile definitions with expected filter patterns, sort, classification, and index plan in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostQueryCoverage.kt`
- [x] T039 [US3] Classify every validated `FeedPostQuery` into a common or rare coverage profile before SQL generation in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostQueryValidator.kt`
- [x] T040 [US3] Remove redundant prefix indexes and keep only measured/common post indexes in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/model/feed/LocalRssContentFeedPost.kt`
- [x] T041 [US3] Keep feed and category supporting indexes aligned with the performance coverage contract in `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/model/feed/LocalRssPostCategory.kt`
- [x] T042 [US3] Update the performance coverage contract when implementation-measured index choices differ from the planned matrix in `specs/001-feed-query-engine/contracts/performance-coverage-contract.md`

**Checkpoint**: User Story 3 is independently complete when common profiles have explicit index coverage and rare profiles remain supported without speculative composite indexes.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Verify the full implementation across DB, repository, domain, feature callers, and patch hygiene.

- [x] T043 Run DB query and paging tests from the quickstart against `core/data/db/build.gradle.kts`
- [x] T044 Run shared DB iOS compilation from the quickstart against `core/data/db/build.gradle.kts`
- [x] T045 Run shared feed data iOS compilation from the quickstart against `core/data/feed/build.gradle.kts`
- [x] T046 Run shared domain and feature iOS compilation if query options changed above data in `core/domain/feed/build.gradle.kts` and `feature/feed/build.gradle.kts`
- [x] T047 Run patch whitespace validation from the quickstart for `specs/001-feed-query-engine/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies; can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion; blocks all user stories.
- **User Story 1 (Phase 3)**: Depends on Foundational; MVP scope.
- **User Story 2 (Phase 4)**: Depends on Foundational and should be validated after US1 if the same query builder files are being edited by one agent.
- **User Story 3 (Phase 5)**: Depends on Foundational and benefits from US1/US2 query coverage, but can start in parallel once the coverage catalog interfaces are stable.
- **Polish (Phase 6)**: Depends on all selected user stories being complete.

### User Story Dependencies

- **US1 (P1)**: Can start after Foundational. No dependency on US2 or US3.
- **US2 (P2)**: Can start after Foundational. Integrates with US1 query builder behavior but remains testable by filter/sort catalog tests.
- **US3 (P3)**: Can start after Foundational. Uses final filter/sort names and indexes; validates common/rare coverage.

### Within Each User Story

- Write tests first and verify they fail for the current implementation.
- Implement storage/query model changes before repository/domain/feature callers.
- Keep cursor extraction, SQL sort terms, and query validation in the same sort order.
- Complete each story checkpoint before moving to the next priority if working sequentially.

### Parallel Opportunities

- T002 and T003 can run in parallel after T001.
- T015, T016, and T017 can run in parallel because they add independent US1 test files.
- T023, T024, T025, and T026 can run in parallel because they add independent US2 test files.
- T035, T036, and T037 can run in parallel because they add independent US3 test files.
- US1, US2, and US3 can be assigned to separate agents after Phase 2 if file conflicts in `FeedPostSqlQueryBuilder.kt` and `FeedPostQueryValidator.kt` are coordinated.

---

## Parallel Example: User Story 1

```text
Task: "Add failing SQL tests proving PublishedNewest and PublishedOldest use publishedSortAt, updatedAt, id, and never use COALESCE or OFFSET in core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostTimelineSqlTest.kt"
Task: "Add failing generic cursor predicate tests for long, text, boolean-first, and feed-derived sort terms in core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostCursorPredicateTest.kt"
Task: "Add failing paging stability tests for initial, append, and refresh-around loads with tied sort values in core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/paging/FeedPostKeysetPagingTest.kt"
```

## Parallel Example: User Story 2

```text
Task: "Add failing SQL tests for all named filters, deterministic IN bindings, explicit empty selected sets, and boolean positive/negative filters in core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostFilterCatalogSqlTest.kt"
Task: "Add failing SQL tests for all named sorts and their cursor term order in core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostSortCatalogSqlTest.kt"
Task: "Add failing search escaping tests for blank text, punctuation, wildcard-like characters, and case normalization in core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostSearchSqlTest.kt"
Task: "Add failing category exact-match tests proving substring-only category matches are excluded in core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostCategorySqlTest.kt"
```

## Parallel Example: User Story 3

```text
Task: "Add failing coverage catalog tests for all common optimized and rare supported profiles in core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostQueryCoverageTest.kt"
Task: "Add failing common-profile SQL/access-path verification tests for TimelineLatest, FeedTimelineLatest, BookmarkedTimelineLatest, FeedBookmarkedTimelineLatest, PinnedFeedsTimelineLatest, UpdatedLatest, CreatedLatest, TitleAlphabetical, and FeedNameAlphabetical in core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostPerformanceCoverageTest.kt"
Task: "Add failing rare-profile correctness tests for search, author, source, category, media, tag-all, and multi-filter combinations in core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedPostRareCombinationTest.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 setup.
2. Complete Phase 2 foundation.
3. Complete Phase 3 stable timeline pagination.
4. Validate `PublishedNewest` initial, append, and refresh-around paging with no `OFFSET`, no duplicate IDs, and no skipped IDs.
5. Stop if only the MVP is needed.

### Incremental Delivery

1. Foundation: typed catalogs, stored sort/filter values, category entity, schema export.
2. US1: generic cursor pagination and deterministic default timelines.
3. US2: complete named filter/sort SQL generation and repository/domain caller migration.
4. US3: named performance coverage, indexes, rare-profile correctness, and verification.
5. Polish: run targeted DB tests, KMP compiles, and `git diff --check`.

### Parallel Team Strategy

1. One owner completes Phase 1 and Phase 2 to avoid schema/query model conflicts.
2. After Phase 2, separate owners can add US1, US2, and US3 tests in parallel.
3. Coordinate implementation edits to `FeedPostSqlQueryBuilder.kt`, `FeedPostQueryValidator.kt`, and entity index files before merging.

## Notes

- Do not replace cursor paging with limit/offset paging.
- Do not add unsupported arbitrary SQL fragments to caller-facing APIs.
- Do not add speculative composite indexes without a named common profile.
- Keep current feed screens on the existing post-with-feed metadata projection while migrating callers to the typed query shape.
