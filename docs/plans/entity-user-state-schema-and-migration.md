# Separate feed and post entities from user state

This document defines the proposed entity/user-state schema, its one-time baseline reset, and the migration policy afterward. It supplements the [read and unread feature plan](./post-read-unread-plan.md). Source inspection occurred on 17 October 2026. The user selected a new version 1 baseline on 19 October 2026 because the app has no users. This documentation update does not execute database changes or reset an installation.

## Establish the new version 1 baseline

Complete the database changes for this feature, then make the resulting schema the new `BASE = 1`. There is no upgrade migration or backfill from the previous development schema. Old development databases and the previous migration history are retired from the supported lineage.

Create fresh databases with the complete target schema, including both user-state tables, current discovery tables, FTS, indexes, and triggers. Both user-state tables begin empty. Missing state means unread, unbookmarked, unpinned, reading revision zero, and no opening timestamp.

This is the only baseline reset. After this feature is complete, every database schema change requires a version increment, an exported schema, and a tested migration from the established baseline lineage. This applies before release as well as after release. Do not reintroduce an unreleased-app exception.

The ownership, query, mapper, repository, UI, search, filtering, and paging changes below remain required. Resetting the baseline removes legacy migration and backfill work. It does not remove those application changes.

## Establish the current source facts

| Source | Current behavior relevant to the separation |
| --- | --- |
| [LocalRssContentFeedPost.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/model/feed/tables/LocalRssContentFeedPost.kt) | Stores publisher content and `bookmarked`. Two indexes include `bookmarked`. |
| [LocalRssFeed.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/model/feed/tables/LocalRssFeed.kt) | Stores feed details, `pinned`, and `lastOpenedAt`. One index includes `pinned`. |
| [RssPostMapper.kt](../../core/data/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/feed/mapper/RssPostMapper.kt) and [RssFeedMapper.kt](../../core/data/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/feed/mapper/RssFeedMapper.kt) | Copy user fields from earlier snapshots into whole-entity writes. This permits stale refreshes to overwrite later user actions. This is source-derived race analysis, not a reproduced device incident. |
| [FeedPostDao.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/dao/FeedPostDao.kt) | Has post upserts, fingerprint identity lookup, bookmark update, recent posts, detail observation, and raw paged queries. |
| [FeedDao.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/dao/FeedDao.kt) | Has integer-keyed feed paging, pinned feed paging, feed observation, recent-preview selection, pin writes, and ranked feed search. |
| [LocalFeedPostSqlQueryBuilder.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/LocalFeedPostSqlQueryBuilder.kt) | Combines post filters, optional FTS matches and highlights, ordering, and keyset cursors. It currently filters `p.bookmarked` and `f.pinned`. |
| [FeedSyncMetadataDao.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/dao/FeedSyncMetadataDao.kt) | Also selects feed pin state into a refresh identity projection. Updating only visible screens would leave this query broken. |
| [LocalRssFeedSyncMetadata.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/model/feed/tables/LocalRssFeedSyncMetadata.kt) | Already separates fetch scheduling, counters, ETag, and Last-Modified from feed details. Keep that separation. |
| [SchemaVersions.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/SchemaVersions.kt), [DevPulseDatabase.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/DevPulseDatabase.kt), and [exported schema 3](../../core/data/db/schemas/dev.shounakmulay.devpulse.core.data.db.DevPulseDatabase/3.json) | Current version is 3. Auto-migration edges are 1 to 2 and 2 to 3. Feed and post metadata FTS tables use external content linked through SQLite row IDs. |

Recheck these facts when implementation starts. Unrelated discovery work is currently dirty. Do not reset it, reuse its schema number without checking, or describe its behavior as validated by this plan.

## Define ownership in the target schema

### Keep entity facts and move user choices

| Table | Target responsibility | Fields removed or added |
| --- | --- | --- |
| `LocalRssContentFeedPost` | Post UUID, feed relationship, fingerprint, publisher fields, and existing ingestion timestamps. | Remove `bookmarked`. Do not add read columns. |
| `LocalRssPostUserState` | The user's state for one locally stored post. | Add `postId`, `isRead`, `bookmarked`, and `readRevision`. |
| `LocalRssFeed` | Feed UUID, URL, existing name and title behavior, publisher details, and ingestion timestamps. | Remove `pinned` and `lastOpenedAt`. |
| `LocalRssFeedUserState` | The user's state for one locally stored feed. | Add `feedId`, `pinned`, and `lastOpenedAt`. |
| `LocalRssFeedSyncMetadata` | Fetch processing and scheduling state. | No ownership change. Its feed-identity projection needs updating. |

Do not move `createdAt` or `updatedAt` into user state. Preserve their current ingestion meanings. A read, bookmark, pin, or open action must not update those timestamps or reorder publication-based lists.

Retain `name` and `title` in the feed entity for this change. The current mapper takes `name` from the import queue and preserves an existing title. That does not establish a complete user-rename contract. A future explicit `customTitle` would belong to feed user state, with a separate publisher title and a defined effective-name search rule. Do not introduce that product change during extraction.

Categories received from feeds remain publisher data. Existing tag and mapping tables remain separate. Cached article bodies and the content extraction queue retain their existing responsibilities.

### Use typed rows and effective defaults

| Table | Column | Kotlin shape | SQL rule |
| --- | --- | --- | --- |
| Post user state | `postId` | `LocalUUID` | `TEXT NOT NULL PRIMARY KEY`, foreign key to the post UUID with `ON DELETE CASCADE`. |
| Post user state | `isRead` | `Boolean` | `INTEGER NOT NULL DEFAULT 0`. |
| Post user state | `bookmarked` | `Boolean` | `INTEGER NOT NULL DEFAULT 0`. |
| Post user state | `readRevision` | `Long` | `INTEGER NOT NULL DEFAULT 0`. Increase only for accepted reading commands, including conditional Undo. |
| Feed user state | `feedId` | `LocalUUID` | `TEXT NOT NULL PRIMARY KEY`, foreign key to the feed UUID with `ON DELETE CASCADE`. |
| Feed user state | `pinned` | `Boolean` | `INTEGER NOT NULL DEFAULT 0`. |
| Feed user state | `lastOpenedAt` | `Long?` | `INTEGER DEFAULT NULL`. Preserve existing values without inventing new tracking behavior. |

Use zero or one state row per entity. A missing post row means unread, unbookmarked, and reading revision 0. A missing feed row means unpinned with no last-opened time. Queries must implement these defaults consistently. An inner join would incorrectly hide new entities without state.

Once an operation has created a state row, retain it while the parent exists, including after setting every value back to its default. In particular, deleting a post state row would erase `readRevision` and allow stale reading commands to treat it as untouched again.

Do not seed rows from old entities or backfill old user fields. Fresh imports can remain sparse, and state commands create rows on first mutation. Preserve UUIDs and fingerprints across subsequent content refreshes within the new database. Do not deduplicate state by URL across feeds.

### Separate writers inside the user-state table

Creating a state table does not make whole-state upserts safe. Do not read the combined state, change one field in Kotlin, and upsert the whole row.

| Command | Allowed writes | Required isolation |
| --- | --- | --- |
| Refresh feed or post content | Parent entity columns only. | Never create, reset, or copy user state on refresh. Verify actual Room parent upserts update existing parents without replacement cascades. |
| Set bookmark | `bookmarked` only. | Preserve `isRead` and `readRevision`. Use an explicit target, not a database toggle. |
| Set reading state | `isRead` and `readRevision` only. | Check the expected reading revision within the write transaction. Preserve bookmark state. |
| Undo reading action | `isRead` and a new `readRevision` only. | Apply only if the receipt's applied reading revision still owns the row. A later bookmark must neither be reverted nor block reading Undo. |
| Set feed pinned | `pinned` only. | Preserve `lastOpenedAt`. |
| Record a feed open, if that behavior is implemented | `lastOpenedAt` only. | Preserve pin state. Decide event ordering and timestamp policy when enabling the writer. This extraction does not add the feature. |

For a first mutation, create a default row without overwriting any existing row, then update only the command's columns in the same transaction. Check that the parent still exists. Handle affected-row counts and stale revisions explicitly. Keep cancellation and rollback behavior consistent with `DevPulseDatabaseTransactionAccessor`.

Reading revision belongs to reading intent. Do not rename it to a universal row revision or increment it for bookmarks. A shared revision would make unrelated bookmark actions invalidate delayed reading commands and reading Undo. If bookmarks later acquire conditional Undo, add a bookmark-specific revision when that requirement exists.

## Update projections and application contracts

Remove `bookmarked` from `RssFeedPost`, `RssFeedPostIdentity`, and `LocalRssContentFeedPostIdentitySlice`. Remove `pinned` and `lastOpenedAt` from `RssFeed`, `RssFeedIdentity`, and `LocalRssFeedIdentitySlice`. Those identity objects feed refresh processing and must no longer carry user fields merely for preservation.

Add typed `PostUserState` and `FeedUserState` domain values, plus matching local projection types. Use a proposed `RssFeedWithUserState` wrapper for feed-facing reads. Extend `RssPostWithFeedIdentityAndSearch` with post user state and feed user state while keeping its content identity and search highlights distinct. Update `LocalRssPostWithFeedAndSearch` in the same way. These proposed names are implementation targets, not existing symbols.

Use explicit column aliases for every embedded state projection, such as `postState_isRead`, `postState_bookmarked`, `postState_readRevision`, `feedState_pinned`, and `feedState_lastOpenedAt`. Keep search aliases separate. Do not manufacture a parent-entity `bookmarked` field from an SQL alias after removing it from the entity.

| Area | Required changes |
| --- | --- |
| `RssPostMapper`, `RssFeedMapper`, `ParsedFeedProcessor` | Remove user-field copying from content creation and identity conversion. Add mapping of joined state only on read projections. Preserve identity generation and ingestion timestamp behavior. |
| `FeedRepositoryImpl` | Return feed-with-state reads where the caller displays user fields. Keep refresh-only identity lookup state-free. Route `setFeedPinned` to the state DAO. |
| `PostRepositoryImpl` | Map joined post and feed state in detail, recent-post, and paged reads. Route `setPostBookmarked` to the state DAO. Add revision-aware reading mutations. Pure content extraction can continue using parent-only `getPost`. |
| `FeedSyncRepositoryImpl`, `FeedSyncMetadataRepositoryImpl`, and `FeedSyncMetadataDao` | Update refresh identities and `getFeedsPastNextEligibleFetch`. Remove `identity.pinned` from that query and the matching projection. Do not add a user-state join solely to maintain an obsolete refresh contract. |
| `GetFeedUseCase`, feed list and pinned-feed use cases, and post observation use cases | Migrate result contracts to the new read aggregates. Preserve observable database-backed reads and existing error conventions. Verify exact current use-case names before editing. |
| Feature interactors, `UIFeed`, and `UIFeedPost` | Read display fields from state aggregates. Keep the UI models convenient for rendering. A UI Boolean does not require the persistence entity to own it. |
| Feed home, feed list, feed detail, post list, and post detail ViewModels | Keep events explicit. Replace old field access, preserve loading and error behavior, and rely on observed committed state. |
| Shared list components, previews, fixtures, fake DAOs, and tests | Update constructor inputs and result shapes. Keep bookmarks and pinning functional before enabling new read interactions. |
| `DevPulseDatabase`, `DbModule`, and annotation-scanned modules | Register entities and state DAOs. Wire repository dependencies through the existing Koin patterns. No additional feature module is required. |

Migrate callers and remove old write APIs in the same implementation unit. Do not retain compatibility properties on entities or dual-write both representations. There must be one owner for each value.

## Change SQL before applying limits

### Join post and feed state once

The following SQL is a proposed query shape. It illustrates the state additions. The actual query builder must retain its full feed projection, search CTEs, highlight selection, filters, order, and bound parameters.

```sql
SELECT
    p.*,
    COALESCE(ps.isRead, 0) AS postState_isRead,
    COALESCE(ps.bookmarked, 0) AS postState_bookmarked,
    COALESCE(ps.readRevision, 0) AS postState_readRevision,
    COALESCE(fs.pinned, 0) AS feedState_pinned,
    fs.lastOpenedAt AS feedState_lastOpenedAt
FROM LocalRssContentFeedPost AS p
JOIN LocalRssFeed AS f ON f.id = p.feedId
LEFT JOIN LocalRssPostUserState AS ps ON ps.postId = p.id
LEFT JOIN LocalRssFeedUserState AS fs ON fs.feedId = f.id
```

One primary-key state row per parent prevents duplicate results. Retain category and tag membership subqueries rather than joining their many-to-many rows into the page. Do not add `DISTINCT` to conceal a wrong join.

Update `LocalFeedPostSqlQueryBuilder.appendProjection`, `buildWhereClauses`, and `FeedColumnsSelector`. Replace `BOOKMARKED = p.bookmarked` with an effective post-state expression. Replace `FEED_PINNED = f.pinned` with an effective feed-state expression. Keep output aliases and filter expressions distinct so embedded Room mappings remain unambiguous.

Update the separate detail and recent-post SQL in `FeedPostDao.observePost` and `observeRecentPosts`. Adding joins only to the paging builder would leave those reads inconsistent or referring to removed columns.

### Preserve search meaning and highlighting

| Search path | Required behavior |
| --- | --- |
| Post metadata FTS | Keep `LocalRssContentFeedPostFts` over parent title, description, and content. Match through the original parent `rowid`. |
| Extracted article FTS | Keep `LocalRssPostContentFts` and its UUID-based `postId` matches. Preserve content snippet precedence and current replacement behavior. |
| Feed FTS | Keep `LocalRssFeedFts` over title, name, and description. Join the matching parent to feed user state for display or pin predicates. Preserve `ORDER BY rank, feed.id`. |
| Search with bookmark, unread, or pinned-feed filters | Apply every state predicate in the same SQL selection before `ORDER BY` and `LIMIT`. Search membership must extend beyond currently loaded pages. |
| Non-search paths | Retain the existing no-FTS branch and null highlight projection. Do not require FTS joins for ordinary browsing. |

User state is not searchable text. A bookmark, pin, or reading action must not rewrite an FTS row or change text rank. It can change filtered membership and invalidate the visible search results. Track the state tables even while the FTS tables remain unchanged.

Preserve current sanitization, minimum search length, debounce, highlight delimiters, and binding order. In particular, keep FTS CTE bindings ahead of state/filter bindings where that is the generated SQL order. Verify short and sanitized-empty queries independently for post search and feed search. Do not silently unify their implementations during this schema change.

`FeedDao.searchFeeds` currently selects `feed.pinned`. Replace that projection with the effective state value and return the updated result type. Plain feed search should keep all textual matches. Do not introduce pinned-only search behavior unless that filter is actually selected.

### Specify filter truth tables

| Filter | Predicate | Missing-state result |
| --- | --- | --- |
| Bookmarked | `COALESCE(ps.bookmarked, 0) = 1` | Exclude. |
| Not bookmarked | `COALESCE(ps.bookmarked, 0) = 0` | Include. |
| Read | `COALESCE(ps.isRead, 0) = 1` | Exclude. |
| Unread | `COALESCE(ps.isRead, 0) = 0` | Include. |
| Pinned feed | `COALESCE(fs.pinned, 0) = 1` | Exclude. |
| Unpinned feed | `COALESCE(fs.pinned, 0) = 0` | Include. |
| All or an unset filter | No predicate for that dimension. | Include if other filters match. |

Do not use only `ps.isRead = 0`, `ps.bookmarked = 0`, or `fs.pinned = 0` for negative filters. SQL null comparisons would exclude missing rows. Test sparse rows, migrated default rows, and changed-then-reset rows against the same expected membership.

Preserve existing feed, author, date, source, media, category, and tag semantics. Combine reading status, bookmark status, and pinned-feed status with those filters. Seed the new read filter in `PostInteractor.getDefaultFiltersFor`, and update filter ordering, active-filter counting, rendering, and clearing. Existing bookmark and pinned-feed filters retain their public meaning even though their columns move.

Counts, bulk matching IDs, and paged rows must share predicate construction. Count rows by unique parent ID. Bulk membership excludes cursor conditions and page limits. It includes the same effective search and filters, with the transaction boundary and conditional reading Undo defined in the feature plan.

### Preserve both pagination strategies

| Query family | Current strategy | Change required |
| --- | --- | --- |
| Post lists and in-place post search | Custom keyset cursor using selected publication/title value and post UUID. | Preserve the four existing sorts, comparison directions, UUID tie-breaker, prepend reversal, and refresh-around behavior. Add state joins and predicates before paging. |
| Feed list and pinned-feed list | Room `PagingSource<Int, ...>`. | Return feed-with-state projections. Join before filtering, ordering, and limiting. Retain integer-keyed paging. |
| Feed search | Room integer-keyed paging ordered by FTS rank and UUID. | Preserve rank ordering and join state once. Invalidate after pin changes so projected values update. |
| Recent previews | Observed limited queries. | Join before the existing limit. Preserve current selection semantics. |

For normal feed ordering, append `f.id` as a deterministic tie-breaker after the existing `name`, `title`, and `updatedAt DESC` terms. The current pinned-and-recent query actually orders by pin, name, and title, not `lastOpenedAt`. Preserve those terms and add the UUID tie-breaker. Moving `lastOpenedAt` is not permission to introduce a recency-ranking feature.

A state change never changes the post sort value. In an All list it updates appearance in place. In a filtered list it can remove or insert a post. Refresh from the captured cursor values even if the anchor no longer matches. Preserve the nearest surviving scroll anchor, keep the active reader open, and do not select another article automatically.

Changing the active query invalidates its old cursor context. Create the new paging generation through the existing query flow. Do not reuse a cursor from a different filter or sort. Concurrent imports mean paging is a live result, not a frozen snapshot across all loads.

Feed pinning changes the membership of pinned lists and the order of the pinned preview. Room paging must invalidate and refresh that generation. Keep UUID item keys in the UI. Offset shifts under a changed dataset are a reason to refresh, not a reason to introduce another pagination architecture here.

### Observe state changes in every read path

Add both user-state tables to `LocalRssPostWithFeedMetadataPagingDataProvider.getTablesToTrack()` and `FeedPostDao.getPostPage` observed entities. The custom `LocalCursorPagingSource` builds invalidation directly from the provider's table list. Adding an annotation alone does not replace that list.

Static Room queries that join the state tables should gain generated dependency tracking. Verify this using real observers and generated paging behavior. Cover detail, recent posts, feed lists, pinned feeds, and feed search. No manual repository cache, manual refresh button requirement, or UI-only state is needed.

Room invalidation is table-oriented. A change to `lastOpenedAt` can invalidate queries that join feed user state even when their displayed pin value did not change. Do not enable frequent open-event writes as part of extraction. Measure normal invalidation costs. Split activity from preferences later only if an actual write pattern justifies another table.

### Replace indexes without claiming equivalent performance

Remove the following indexes with the removed parent fields.

```text
index_LocalRssFeed_pinned_id
index_LocalRssContentFeedPost_bookmarked_publishedAtEpochMillis_updatedAt_id
index_LocalRssContentFeedPost_feedId_bookmarked_publishedAtEpochMillis_updatedAt_id
```

Retain the unique source URL and fingerprint indexes, parent feed relationships, and content sort indexes. Start with `LocalRssPostUserState(bookmarked, postId)` and `LocalRssFeedUserState(pinned, feedId)` as candidate indexes supporting the extracted positive filters. Match their final names and definitions between entity annotations and migration SQL. Decide whether to keep them from actual query plans and timings. A reading index on `(isRead, postId)` needs the same evidence.

The old composite indexes could combine bookmark filtering and content ordering inside one table. An index cannot span the new state and content tables. The planner may scan content in sort order and probe state, or begin with matching state and sort joined content. Sparse and dense distributions can favor different plans. Do not copy publication dates into state to imitate the old indexes before measuring the actual cost.

## Create the new baseline once

### Reset schema history together with the final entity definitions

- [ ] Complete the user-state entity, DAO, projection, query, and caller changes before finalizing the baseline export.
- [ ] Set `SchemaVersions.BASE = 1` and use it as the database version. Remove the old discovery version constants and the old 1-to-2 and 2-to-3 auto-migration declarations.
- [ ] Remove migration specifications that have no remaining caller, including the old discovery search migration if it is used only by the retired lineage. Retain any reusable fresh-creation logic still needed by the new schema.
- [ ] Replace the previous development schema exports with the generated final `1.json`. Remove retired exports 2 and 3 from the active Room schema directory. Git retains the previous history. Generate the new identity hash through Room rather than editing JSON by hand.
- [ ] Regenerate fixtures and any actual bundled database artifacts for the complete new schema. Keep the current discovery tables and their seed behavior. Do not mistake a discovery JSON seed for a complete Room database snapshot.
- [ ] Recreate development databases when running the new baseline. The previous development version 1 is structurally different from the new version 1. Changing the integer alone cannot make an existing file compatible. Do not add a 3-to-1 downgrade or a legacy backfill path.
- [ ] Verify creation, reopen, imports, offline content, bookmarks, pinning, reading state, search, filters, paging, and database observation on a freshly created database.

Do not ship an automatic destructive-migration fallback. Subsequent schema versions must upgrade through the migration policy below. This turn updates the plan only. No installed database has been deleted or recreated.

### Preserve runtime relationships within the new baseline

Feed and post metadata FTS use parent SQLite row IDs. Child relationships use UUID foreign keys. Verify that fresh creation installs the Room-managed FTS synchronization triggers and [the custom content-FTS delete trigger](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/triggers/PostTriggers.kt).

After import, test content inserts, updates, and deletes through actual DAOs. A publisher refresh must preserve existing state. A real deletion must cascade state and perform existing content-FTS cleanup. Do not implement parent refresh through `INSERT OR REPLACE`, which can delete child state.

Run Room schema validation and foreign-key checks on the new database. External-content FTS matches, highlights, and later index maintenance must work. Legacy row-ID preservation during an upgrade is no longer a requirement for this one-time reset. Normal row-ID and relationship correctness within the new database remains required.

## Require migrations for every later schema change

Freeze the generated new `1.json` when this feature is complete. The next schema change becomes version 2 with a tested 1-to-2 migration. Later changes continue that lineage. Keep every established historical schema immutable.

The policy covers tables, columns, defaults, nullability, primary and foreign keys, indexes, FTS configuration, and database triggers. Trigger changes made outside Room annotations still require a database version change and a migration that updates already-created databases. Editing `onCreate` alone does not upgrade an existing installation.

For each later schema change, require the following evidence.

- [ ] Increment the database version and generate its new schema export.
- [ ] Supply and register a manual migration or a Room auto-migration with the required specification. An exported file alone is not a migration.
- [ ] Preserve established schema exports. Do not rewrite `1.json` or any later historical export to make a schema diff disappear.
- [ ] Test every supported baseline-to-current upgrade route against the real Room driver, including representative persisted user state.
- [ ] Validate migrated structure against fresh creation at the target version. Verify foreign keys, FTS, triggers, user-state preservation, and post-upgrade content writes.
- [ ] Keep destructive fallback and another baseline reset out of the upgrade path.

Add a proposed CI schema-history check after establishing the new baseline. Compare against the PR merge base. Reject edits or deletion of established schema exports. When the generated structure changes, require a higher version, its new export, and migration tests proving a registered path. Include a check for versioned custom trigger definitions because Room's exported schema does not fully describe their behavior. This enforcement is planned, not implemented in this documentation task.

Normal row inserts, state updates, and catalog refreshes within an unchanged schema do not need schema migrations. The rule governs changes to the stored database structure and its versioned behavior.

## Deliver the change in verifiable units

The throughput checkpoint for this documentation task is n/a. For implementation, complete one coherent schema-and-caller change before adding reading interactions. Use the selected new version 1 baseline for this feature.

1. **Establish the selected baseline.** Finish the entity/user-state redesign, generate the new version 1 schema, retire the previous development lineage, and record the test baseline. Migration and backfill choices are resolved.
2. **Separate ownership across persistence and callers.** Add state entities, DAOs, projections, domain aggregates, and narrow setters. Remove old entity fields and preservation copying. Update every query and mapper in the same unit so the target compiles without dual writes.
3. **Prove existing behavior survives.** Verify bookmarks, pins, refresh interleavings, feed search, post search, all existing filters, both pagination strategies, and observers before enabling automatic read marking.
4. **Add reading UX and filtering.** Execute the reader-visit, manual controls, status filter, empty states, and conditional Undo phases in the feature plan, using `readRevision` rather than a universal state revision.
5. **Add scoped bulk read.** Reuse query predicates and transaction receipts. Verify large result sets and later-action preservation before exposing the control.

No implementation, build, commit, PR, deployment, or data reset has been executed by this document. Phase boundaries describe work order and evidence, not authorization to perform those actions now.

## Verify search, filters, and pages as a combined system

| Scenario | Required evidence |
| --- | --- |
| Content refresh after bookmark, pin, or reading change | Real Room integration test preserves the newer state. Intentionally interleave snapshot creation and content save. |
| Concurrent read and bookmark commands | Both values survive, and bookmark changes leave `readRevision` unchanged. Repeat for pin and open-time columns if open tracking is enabled. |
| Absent, explicit-default, and non-default state rows | SQL truth-table tests return the expected positive, negative, and All memberships. |
| Post search with unread, bookmark, pinned feed, and another content filter | Exact ordered IDs match an equivalent unpaged expected result across several pages. Highlight fields remain correct. |
| Four post sorts with tied titles, tied dates, missing dates, and both page directions | No duplicates or skipped IDs in a stable dataset. Preserve existing UUID tie-breaker directions. |
| Filtered anchor disappears, or its feed is unpinned | Refresh finds surviving neighbors, retains the active reader, and does not navigate automatically. |
| Feed search rank ties and feed-list sort ties | Stable ordering by the documented UUID tie-breaker, with correct state values after a pin action. |
| State changes on another screen | Detail, recent previews, feed lists, pinned feeds, and active search update through observation. Verify the custom provider's table list as well as Room static-query tracking. |
| Counts and bulk action span unloaded pages | Count and exact matching IDs agree with the same predicates. Bulk does not stop at loaded rows or reverse later bookmarks. |
| Fresh version 1 startup | Actual Room validation passes, user-state tables begin empty, and parent-child relationships and FTS work after imports and later content writes. |

Use the targeted Gradle and live-platform checks in the feature plan after confirming current task names. Run the existing baseline first and distinguish unrelated test drift or KSP failures from this work. Native interaction checks must include narrow and two-pane Android, iOS, and Desktop. Compilation alone does not establish paging stability or visible reader behavior.

Measure first page, append, prepend, anchor refresh, feed search, and state-change-to-observed-result duration with both sparse and dense states. Include positive and negative filters, selective and broad FTS matches, 10,000 and 100,000 posts, one large feed, and many small feeds. Record `EXPLAIN QUERY PLAN`, dataset, device, build mode, and timings. Compare baseline and target query memberships before comparing their performance. The feature plan's performance budgets are proposed gates, not observed results.

## Record what was checked for this document

Earlier source, exported schema 3, index definitions, and query callers were inspected. An isolated Python SQLite 3.53.1 probe checked an earlier preservation proposal. That proposal is superseded by the selected version 1 baseline reset. The probe is not evidence that the new baseline has been created or verified.

That probe did not use the app's `BundledSQLiteDriver`, Room migration validation, Room-managed FTS synchronization triggers, custom paging runtime, or a device. Fresh creation and runtime checks remain required implementation evidence. Migration checks become mandatory for every subsequent schema change. The system Python SQLite lacked the trigram tokenizer, so the probe used the bundled workspace Python runtime. No live database was opened or modified.

Model the Domain shaped the content/user-state boundary and field-specific reading revision. Make Operations Idempotent shaped explicit-target setters and conditional Undo. Experience First retained existing search meaning and paging behavior while moving their storage dependencies. The remaining design cost is joined query complexity and performance. Future changes also carry the required migration work. If measured query performance is unacceptable, compare targeted indexes and publisher-only update contracts before adding duplicated content fields or denormalized counters.
