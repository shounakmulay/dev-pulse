# Separate feed and post entities from user state

This document defines the proposed schema, application changes, and two ways to adopt it. It supplements the [read and unread feature plan](./post-read-unread-plan.md). Source and exported schema inspection occurred on 17 October 2026. The separation is agreed in this conversation. Application implementation, database resets, and migration execution are not part of this documentation task.

## Select one target schema and one adoption path

Both adoption paths produce the same schema and application behavior. A fresh installation already creates the latest schema, including when the application also supports upgrade migrations. The choice is whether to preserve databases created by earlier development builds.

| Path | Use it when | Cost and limitation |
| --- | --- | --- |
| A. Preserve existing databases | Any development or test installation has subscriptions, bookmarks, pins, cached articles, or other database data worth retaining. | Implement and verify an upgrade migration, including external-content FTS and foreign keys. |
| B. Make this the fresh baseline | Every existing installation and database fixture can deliberately be replaced. The user reports that the app is unreleased. | Implement only the target schema and application changes. Old databases require an explicitly agreed reset or a later supported migration. |

Recommend Path B for the unreleased app if the existing development data is disposable. Unreleased does not itself prove that every tester's data is disposable. Keep Path A documented as the preservation alternative. This recommendation does not authorize clearing any installed database.

The query, mapper, repository, UI, search, filtering, and paging work below is required under both paths. Choosing a fresh baseline removes migration engineering. It does not remove application changes.

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

The preservation migration can seed rows for every existing entity. Fresh imports can remain sparse. Both representations must return identical effective values. Preserve UUIDs and fingerprints. Do not deduplicate state by URL across feeds.

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

## Path A. Preserve development databases through migration

### Register a new explicit upgrade

Use the next unused version. Based on the inspected source, that is a proposed version 4 with a constant such as `ENTITY_USER_STATE`. Preserve schema exports 1, 2, and 3 and their existing auto-migration edges. Register an explicit 3-to-4 migration through the supported Room migration builder API. Verify supported older-version upgrade paths in tests.

Use an explicit migration because values must be copied before their old columns are removed. An auto-migration that only creates tables and deletes columns does not establish that preservation order.

Stop old application writers by performing the upgrade during normal database opening before exposing DAOs and starting feed workers. Use the migration transaction managed by Room. Do not toggle foreign keys inside an already-running migration transaction.

### Create and backfill before removing columns

The SQL below defines the proposed core transformation. It is a design fragment, not an already-registered Room migration. The final exported schema must validate column defaults, foreign-key actions, and the chosen state indexes.

```sql
CREATE TABLE LocalRssPostUserState (
    postId TEXT NOT NULL PRIMARY KEY,
    isRead INTEGER NOT NULL DEFAULT 0,
    bookmarked INTEGER NOT NULL DEFAULT 0,
    readRevision INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (postId) REFERENCES LocalRssContentFeedPost(id)
        ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE LocalRssFeedUserState (
    feedId TEXT NOT NULL PRIMARY KEY,
    pinned INTEGER NOT NULL DEFAULT 0,
    lastOpenedAt INTEGER DEFAULT NULL,
    FOREIGN KEY (feedId) REFERENCES LocalRssFeed(id)
        ON UPDATE NO ACTION ON DELETE CASCADE
);

INSERT INTO LocalRssPostUserState(postId, isRead, bookmarked, readRevision)
SELECT id, 0, bookmarked, 0 FROM LocalRssContentFeedPost;

INSERT INTO LocalRssFeedUserState(feedId, pinned, lastOpenedAt)
SELECT id, pinned, lastOpenedAt FROM LocalRssFeed;

DROP INDEX index_LocalRssFeed_pinned_id;
DROP INDEX index_LocalRssContentFeedPost_bookmarked_publishedAtEpochMillis_updatedAt_id;
DROP INDEX index_LocalRssContentFeedPost_feedId_bookmarked_publishedAtEpochMillis_updatedAt_id;

ALTER TABLE LocalRssContentFeedPost DROP COLUMN bookmarked;
ALTER TABLE LocalRssFeed DROP COLUMN pinned;
ALTER TABLE LocalRssFeed DROP COLUMN lastOpenedAt;
```

Existing posts start unread at reading revision 0. Migrating a true bookmark is not a reading command. Preserve both true and false bookmarks, both pin values, null and non-null last-opened values, IDs, ingestion timestamps, and all content. Create any selected state indexes after the copy.

Prefer in-place column removal over parent-table rebuilds, provided the app's actual SQLite runtime and Room migration lifecycle support it. Verify dependent indexes, triggers, views, and runtime capabilities first. Do not assume that a Python SQLite result proves the bundled app driver supports the same operation.

### Protect row IDs, foreign keys, and FTS

Feed and post metadata FTS currently identify content through the parent's implicit `rowid`, while child tables use UUID foreign keys. Preserving UUIDs alone is insufficient for a table rebuild. A rebuild can change row IDs, trigger cascading deletions, or run the custom post-delete trigger against extracted article FTS.

Inventory relevant `sqlite_master` definitions from a test database opened by the current Room implementation. Capture Room-managed FTS synchronization triggers and [the custom content-FTS delete trigger](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/triggers/PostTriggers.kt). Verify that dropping only user columns leaves those definitions valid. Room may manage FTS triggers during migration, so test subsequent inserts, content updates, and deletes after reopening, not only pre-existing search results.

Do not drop parent tables or use `INSERT OR REPLACE` as a convenient migration or refresh primitive. If in-place removal fails the actual runtime gate, redesign the migration with a fully specified preservation procedure. Do not substitute an untested drop-and-copy recipe. Path B remains available if preserving these databases is no longer needed.

The custom delete trigger is currently installed in `onCreate`. Confirm it exists after upgrade as well as fresh creation. If the chosen migration path removes it, explicitly restore it through the correct migration hook. Rebuild external-content FTS only if required by the verified Room migration behavior, and use the preserved current parent row IDs. Preserve standalone article-content FTS separately.

### Verify preservation before accepting the upgrade

- [ ] Upgrade a database containing multiple feeds, gapped parent row IDs, bookmarks, pins, null and non-null last-opened values, cached bodies, categories, tags, queue entries, sync metadata, and discovery content.
- [ ] Compare all preserved values and parent row IDs against the pre-upgrade fixture. Confirm every post is initially unread with reading revision 0.
- [ ] Validate the migrated database against the target Room schema and compare its structure with a freshly created target database. Sparse row contents can differ while effective query results agree.
- [ ] Run `PRAGMA foreign_key_check` and the appropriate FTS integrity and match checks. Verify highlights, snippets, rank, and extracted content search.
- [ ] Insert, update, and delete through the actual DAOs after migration and reopen. Confirm Room-managed FTS triggers, custom cleanup, and state cascades still work.
- [ ] Exercise every supported old version to the target, including 1-to-2-to-3-to-4 and 2-to-3-to-4 if those versions remain supported. Do not label unrun paths verified.
- [ ] Inject failure after backfill and before column removal completes. Confirm rollback leaves the old database usable without a partial upgrade.

There is no planned downgrade. Keep a supported backup before a real preservation migration, using a consistent database backup procedure that accounts for WAL. Do not copy an actively written database file and assume the copy is complete. The app must not attempt to open the new schema with an older binary.

## Path B. Adopt the target as the unreleased baseline

### Keep a distinct schema identity

Use the same target entities and query implementation, but omit the 3-to-4 migration. Prefer the next version number, currently proposed as 4, even when the build supports only newly created databases. A baseline is a schema contract, not a requirement to call it version 1.

Remove obsolete auto-migration declarations from the fresh-only database configuration. Generate the target schema export with the existing Room tooling. Historical exports can remain as historical evidence unless a separate cleanup is agreed. Do not edit old schema JSON files by hand to imitate the new shape.

Do not replace exported version 3 with a structurally different version 3. Existing databases carry a Room identity hash, and the same version number does not make incompatible layouts interchangeable. If the team explicitly chooses to restart numbering at version 1, do it only after intentionally retiring every old database and replacing the whole schema history consistently. It provides no runtime benefit over a distinct new version and is not the recommendation.

### Replace development data deliberately

- [ ] Identify every Android installation, iOS simulator or device, Desktop database, test fixture, and backup that the build is expected to open.
- [ ] Decide whether each can be discarded. If any must survive, select Path A or retain an explicit supported conversion for that database.
- [ ] Obtain the specific reset decision before clearing a real installation. This document grants no deletion permission.
- [ ] Recreate the database through the normal app startup on the intentionally reset installation. A fresh database starts with no subscriptions or local user state until import and user actions populate it.
- [ ] Regenerate fixtures and any bundled or prebuilt database artifacts that actually use `DevPulseDatabase`. The current discovery JSON seed is not automatically a full Room database snapshot.
- [ ] Test an old database against the fresh-only build. It must require the documented intentional reset or supported conversion. Do not silently fall back to destructive migration.
- [ ] Verify fresh create, process restart, imports, offline cache, pinning, bookmarking, reading state, search, filtering, and pagination on the target.

OPML can restore subscriptions if a working export exists. It is not a replacement for bookmarks, pin state, cached content, or reading state. This task has not established a complete export or backup implementation. Do not promise restoration from a capability that has not been verified.

The fresh-only decision does not remove future migration obligations. Once any build's data must survive upgrades, keep its exported schema and add tested migration paths for subsequent changes.

## Deliver the change in verifiable units

The throughput checkpoint for this documentation task is n/a. For implementation, complete one coherent schema-and-caller change before adding reading interactions. Both schema adoption paths use the same application units.

1. **Select the adoption path.** Record preservation versus intentional fresh baseline, actual schema version, supported installations, and current test baseline. This is the remaining data-retention decision.
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
| Migration or fresh startup | Actual Room validation passes, parent-child relationships survive or begin empty as intended, and FTS continues working after later content writes. |

Use the targeted Gradle and live-platform checks in the feature plan after confirming current task names. Run the existing baseline first and distinguish unrelated test drift or KSP failures from this work. Native interaction checks must include narrow and two-pane Android, iOS, and Desktop. Compilation alone does not establish paging stability or visible reader behavior.

Measure first page, append, prepend, anchor refresh, feed search, and state-change-to-observed-result duration with both sparse and dense states. Include positive and negative filters, selective and broad FTS matches, 10,000 and 100,000 posts, one large feed, and many small feeds. Record `EXPLAIN QUERY PLAN`, dataset, device, build mode, and timings. Compare baseline and target query memberships before comparing their performance. The feature plan's performance budgets are proposed gates, not observed results.

## Record what was checked for this document

Current source, exported schema 3, index definitions, and query callers were inspected. An isolated in-memory probe recreated the exported table DDL using Python SQLite 3.53.1. It passed state backfill, in-place column removal, preservation of gapped feed and post row IDs, feed and post FTS matches, foreign-key checks, isolated bookmark and read writes, content-update preservation, missing-state unread filtering, and cascading deletion.

That probe did not use the app's `BundledSQLiteDriver`, Room migration validation, Room-managed FTS synchronization triggers, custom paging runtime, or a device. Those checks remain required implementation evidence. The system Python SQLite lacked the trigram tokenizer, so the probe used the bundled workspace Python runtime. No live database was opened or modified.

Model the Domain shaped the content/user-state boundary and field-specific reading revision. Make Operations Idempotent shaped explicit-target setters and conditional Undo. Experience First retained existing search meaning and paging behavior while moving their storage dependencies. The strongest counterargument remains join and migration cost. If measured query performance is unacceptable, compare targeted indexes and publisher-only update contracts before adding duplicated content fields or denormalized counters.
