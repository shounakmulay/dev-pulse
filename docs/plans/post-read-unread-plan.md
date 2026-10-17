# Mark posts as read and unread

This is a decision document and implementation plan for DevPulse. It records the recommended behavior, rejected branches, code ownership, and verification gates. Source was inspected on 16 October 2026, with feed-query and entity ownership checked again on 17 October 2026. No feature code, database migration, build, or device test was performed for this document.

## Recommended outcome

Mark a post as read when the user sees its reader content. Let the user mark it unread again from the reader or its list item. Keep **All** as the default list filter. Add **Unread** and **Read** alongside the existing filters. Preserve read state across refreshes and restarts.

Use the approved separation of publisher entities and user state. Store `isRead`, `bookmarked`, and `readRevision` in `LocalRssPostUserState`, keyed by the existing post UUID. Store `pinned` and `lastOpenedAt` in `LocalRssFeedUserState`, keyed by the existing feed UUID. Treat absent state as unread, unbookmarked, unpinned, and never opened. Retain rows after commands so a delayed read operation cannot mistake deliberate unread state for untouched state.

The companion [entity and user-state schema and migration design](./entity-user-state-schema-and-migration.md) owns the complete field split, migration choices, and schema rollout. The shared schema foundation precedes reader exposure. This feature plan owns read semantics and the resulting search, filter, paging, and interaction requirements.

Deliver single-post actions and filtering first. Add scoped **Mark all as read** after the same storage and query behavior passes its verification gates. Keep scrolling, prefetching, sharing, and bookmarking independent of read state.

The only product alternative worth choosing now is manual-only marking. It provides stronger certainty that the user intended to acknowledge a post, at the cost of an extra action for every article. It uses the same storage and query design. Section [Final options](#final-options) explains when it earns that cost.

## Evidence and limits

The links below point to the implementation inspected for this plan. Source facts have an evidence ID. Recommendations are product or engineering judgments, rather than observed user preferences. Performance budgets later in this document are proposed acceptance criteria, not measured results.

| ID | Observed source fact | Consequence |
| --- | --- | --- |
| E1 | [FeedFeatureEntries.kt](../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/navigation/FeedFeatureEntries.kt) registers the feed home, feed detail, post list, and post detail. | Integrate the behavior across each post entry point. |
| E2 | [LocalRssContentFeedPost.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/model/feed/tables/LocalRssContentFeedPost.kt) stores bookmarks and publisher content, but no read state. Posts have UUID keys, unique fingerprints, and a cascading feed foreign key. | A new persisted state is required. A preference or list-local Boolean is insufficient. |
| E3 | [ParsedFeedProcessor.kt](../../core/data/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/feed/repository/ParsedFeedProcessor.kt) resolves fingerprints before mapping posts. The fingerprint includes the feed ID. [RssPostMapper.kt](../../core/data/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/feed/mapper/RssPostMapper.kt) reconstructs whole entities. [FeedPostDao.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/dao/FeedPostDao.kt) saves them through `@Upsert`. | Identity survives matching refreshes. A state copied from an earlier identity lookup can overwrite a newer user action if it shares that whole-row write. |
| E4 | [PostDetailViewModel.kt](../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/screens/postdetail/ui/PostDetailViewModel.kt) observes post details and loads article and RSS content independently. [MviViewModel.kt](../../core/ui/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/ui/viewmodel/MviViewModel.kt) binds sources inside `repeatOnSubscription`. | Neither construction nor a repeated data emission proves that the reader was visible. |
| E5 | [PostDetailScreen.kt](../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/screens/postdetail/ui/PostDetailScreen.kt) navigates to the web view, images, and feed detail. [FeedDetailScreenEntry.kt](../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/screens/feeddetail/navigation/FeedDetailScreenEntry.kt) uses list-pane metadata. | Opening another destination and changing adaptive layout must not create another automatic read action. |
| E6 | [LocalFeedPostSqlQueryBuilder.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/LocalFeedPostSqlQueryBuilder.kt) combines filters and optional FTS search with keyset paging. Its cursor uses the selected sort value and UUID. | Apply reading filters in SQL before paging. Read state must not become part of a sort cursor. |
| E7 | [LocalRssPostWithFeedMetadataPagingDataProvider.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/paging/LocalRssPostWithFeedMetadataPagingDataProvider.kt) explicitly lists tables to track for invalidation. [FeedPostDao.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/dao/FeedPostDao.kt) has separate recent-post and detail queries. | Updating only the paged projection leaves other screens stale. Track the new table in every relevant observer. |
| E8 | [PostListViewModel.kt](../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/screens/postlist/ui/PostListViewModel.kt) combines sort and filters with debounced search, enabling search at three characters. [PostSortAndFilterViewModel.kt](../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/components/postFilterSort/PostSortAndFilterViewModel.kt) replaces an existing filter of the same type. [PostInteractor.kt](../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/interactor/post/PostInteractor.kt) seeds default filters. | Add the read filter to default state, mapping, rendering, ordering, emptiness, and clearing. Adding a sealed subtype alone does not enable it. |
| E9 | [UIFeedPost.kt](../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/model/UIFeedPost.kt) has bookmark state but no reading state. The [feature list-item adapter](../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/screens/feed/ui/components/post/FeedPostListItem.kt) delegates to the [shared list item](../../core/ui/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/ui/list/post/FeedPostListItem.kt). | Add one shared visual treatment and route explicit actions through feature events. |
| E10 | [SchemaVersions.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/SchemaVersions.kt) currently ends at version 3. [DevPulseDatabase.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/DevPulseDatabase.kt) registers Room entities and migrations. | Select a preserving upgrade or a fresh baseline at a distinct version. Recheck because discovery work is in progress. |
| E11 | [FeedPostKeysetPagingTest.kt](../../core/data/db/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/data/db/paging/FeedPostKeysetPagingTest.kt) contains older cursor and DAO shapes than current production source. | Existing test filenames are useful locations, but their current compile readiness is unverified. Establish the baseline before assigning failures to this feature. |
| E12 | [FeedDao.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/dao/FeedDao.kt) reads entity-owned `pinned` in full lists, pinned lists, feed detail, feed identity, recent feeds, and search. [FeedColumnsSelector.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/query/FeedColumnsSelector.kt) references `p.bookmarked` and `f.pinned`. | Move all those reads to joined state before removing entity columns. Changing only read-status queries leaves bookmark and pin behavior broken. |
| E13 | [LocalRssFeed.kt](../../core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/model/feed/tables/LocalRssFeed.kt) contains `pinned`, `lastOpenedAt`, `name`, and `title`. Its FTS entity indexes title, name, and description. | Move the approved pin and opening state. Preserve current name and title fields and search semantics until custom naming ownership is separately resolved. |

Repository guidance contains historical statements that Room is planned and the feed data module is a scaffold. E2, E6, and E10 demonstrate that those statements no longer describe the current source. This plan follows current source.

## Decision branches

### Decide what read means

Use a binary acknowledgment state. **Read** means the post was opened in the reader or explicitly marked read. It does not claim that the user finished, understood, or spent a measured amount of time on the article.

| Branch | Decision | Backing logic |
| --- | --- | --- |
| Binary read or unread | Select. | The user needs a way to distinguish attended posts and return to unread ones. There is no current reading-progress model in E2 or E9. |
| Reading, partially read, finished, and unread | Reject for this feature. | An article, a short RSS description, a video, and an external page do not share a reliable completion signal. Extra states require product definitions and controls that the requested workflow does not need. |
| Read timestamp as the state itself | Reject as the primary representation. | Unread must remain an explicit later decision. A timestamp alone does not identify an intervening command or protect Undo from overwriting it. |
| Feed-level last-read date | Reject. | Backdated imports and title-based sorting exist independently of arrival order. A date cannot express one deliberately unread old article. E6 supports several sort orders. |

Do not add public read-history or last-opened analytics fields in this change. A monotonic revision is required for operation ordering. A timestamp is not required for the user experience.

### Decide when automatic marking happens

| Trigger | Decision | Backing logic |
| --- | --- | --- |
| Tap on a post list item | Reject. | Navigation can fail, be cancelled, or target a missing post. A tap is intent to open, not observed reader exposure. |
| Create the detail ViewModel | Reject. | E4 separates content loading and observation from display. Adaptive navigation can keep entries alive without making them the active reader. |
| Any successful post-detail emission | Reject. | Bookmark changes and read-state updates can cause later emissions. Reapplying automatic marking would undo a deliberate **Mark unread**. E4 repeats bindings with subscriptions. |
| Display meaningful reader content for the active entry | Select. | This is the first signal controlled by the app that proves a reader experience exists. It also works offline with cached content. |
| Reach the bottom or remain for a fixed duration | Reject for the default. | Neither measures comprehension. A timer penalizes fast readers and misclassifies an unattended open reader. Bottom detection differs across article, RSS, image, and media sections. |
| Scroll a list item out of view | Reject. | Skimming a list is not opening its articles. Paging and adaptive grids make viewport exposure an unreliable acknowledgment signal. |
| Manual actions only | Retain as the one worthwhile alternative. | It avoids all inference, but turns every normal article visit into another required action. |

The selected trigger requires the active, visible post-detail entry and a rendered reader body. The body can be cached article content, RSS content, or the description fallback. A title with a loading spinner, an image alone, or an empty error screen does not qualify. A network extraction failure does not prevent marking if a meaningful RSS or description fallback is already visible. If every body source fails or is empty, leave the post unread and keep the manual action available.

Determine body readiness where `PostDetailContent` chooses the displayed section. Represent that result as a small explicit presentation value. Do not duplicate content-selection rules in the ViewModel. A metadata-only article still supports **Mark read** manually.

For an explicit **Open original** action, do not add a second automatic trigger. If qualifying reader content appeared first, the post is already read. Otherwise, leave it unread. A URL launch or navigation call proves dispatch, not successful reading in the destination. Revisit this rule only when an actual external-only reading workflow needs it.

### Decide how to handle a reader visit

Model each navigation entry as one reader visit. Keep a serializable `ReaderVisitReadDecision` in `PostDetailScreenState`, with `Pending`, `Consumed`, and `SuppressedByUser` states. Capture the automatic command's eligible revision from the visit's first observed post snapshot. Retain that revision while waiting for readable content. Do not replace it with a newer revision from later observer emissions. `Pending` becomes `Consumed` before dispatching the first automatic command. A deliberate read or unread command in that reader changes it to `SuppressedByUser` before the command is dispatched.

| Event | Required result |
| --- | --- |
| First qualifying body render during a fresh visit | Send one automatic command using the post ID and the visit's captured eligible revision. |
| Recomposition, bookmark update, content fetch completion, or section change | Keep the decision already made for this visit. |
| Rotate, resize, switch tabs and return, background and resume, or restore the same saved entry | Retain the visit decision. Do not reopen a consumed automatic command. |
| Mark unread while the reader remains open | Persist unread and suppress automatic marking for the rest of the visit. Do not navigate away as a side effect. |
| Open a nested image or web destination and return | Continue the existing visit. |
| Pop the reader, then explicitly open that post again | Create a fresh visit. The new reader exposure can mark it read again. |
| A delayed exposure command races with a manual unread elsewhere | Compare the observed revision in the transaction. Reject stale automatic commands. |
| Another screen marks the visible post unread after its automatic action | Show unread. Do not automatically write again during the same visit. |
| Automatic persistence fails | Keep the persisted state unchanged, show a concise failure with Retry, and retain a failed command as visit-local pending feedback. Retry requires explicit action. Do not loop on emissions. |
| Process dies after consuming the visit decision but before commit | Restore the consumed visit without a surprise automatic retry. The post can remain unread. A new explicit visit or manual action resolves it. |

The last rule accepts a small false-unread window to preserve deliberate unread decisions. Exactly-once acknowledgment across process death would require durable command tracking beyond a saved UI state. That complexity does not earn its place for local reading state.

### Decide how posts look and how users act

Keep all posts visible by default. Per the user's visual preference, leave unread items at their normal emphasis and make read items subtly dimmer. Use neutral theme colors to mute the read item's title, summary, and supporting text, with a slight image treatment where an image exists. Preserve readable contrast in light and dark themes. Keep FTS highlights distinct and legible within the muted text. Do not add a colored dot, a read badge, or another visible status marker.

The dimmer treatment communicates reading history. It never means disabled. Read items keep their full click target, keyboard focus, hover and pressed feedback, and navigation behavior. Keep bookmark and overflow controls at their normal contrast and opacity, with the same enabled behavior in both states. Apply de-emphasis to content elements rather than a blanket alpha on the card, which would also fade those controls and focus feedback. The article reader body keeps its normal contrast and opacity after marking read.

Add a spoken **Unread** or **Read** state description to each list item. Do not expose disabled semantics for read items. The explicit **Mark read** or **Mark unread** action gives assistive-technology users the same state information and control without requiring them to detect visual dimming.

Provide an item action labeled **Mark read** or **Mark unread**, based on observed state. Place it in an accessible overflow menu for every list-item size. Add the same explicit action to the reader toolbar or overflow. Use a labeled menu item where compact layouts cannot support another visible button. Preserve the existing bookmark action.

Use existing DP components for menus, buttons, confirmation, and feedback. If the needed component is missing, stop at that component choice and follow the repository's design-system rule. Do not extend existing raw Material usage as a shortcut.

| Interaction branch | Decision and reason |
| --- | --- |
| Overflow action plus accessibility action | Select. It works with touch, keyboard, and assistive technology without competing with scroll gestures. |
| Swipe as the only action | Reject. It is hidden, differs across platforms, and can conflict with grid scrolling and navigation gestures. |
| Swipe as a later shortcut | Defer. The explicit action must exist first. A shortcut earns scope only after a device prototype demonstrates a useful gesture. |
| Long-press selection and multi-select | Defer. Selection, selection restoration, accessibility, and toolbar behavior are a separate interaction model. Scoped bulk read supplies the common cleanup action first. |
| Toggle the state on the entire card tap | Reject. The card already opens the reader. The same gesture cannot safely mean navigation and an alternating stored command. |
| Hide read posts immediately everywhere | Reject. Opening one post must not unexpectedly change the default browsing list. Offer **Unread** explicitly. |

Show manual-action progress on that item's action, without blocking the reader or entire list. Commit before changing the displayed canonical state. Local Room writes are expected to be short, but that expectation is unmeasured. Disable repeated dispatch for the same pending item. Let database observation update every copy of the post.

After a successful manual action, show **Marked read** or **Marked unread** with **Undo**. Avoid a snackbar after every automatic mark. On failure, say **Couldn't update read status** and offer Retry. Do not show success for a missing post or a rejected stale revision.

### Decide filter and paging behavior

Offer one reading-status choice with **All**, **Unread**, and **Read**. Start at **All** and restore the choice with the existing screen state. Do not introduce a global setting or a separate unread tab. Preserve the existing sort when changing this filter.

Use a typed `PostReadFilter` enum with these three values. Add `RssPostFilter.ReadStatus` and its local counterpart. An `All` value is empty for filter counting and clearing. Add it to `PostInteractor.getDefaultFiltersFor`, because E8's update method replaces existing types rather than adding missing ones.

Apply the status predicate with the feed, bookmark, date, category, tags, and search predicates in the same SQL query. Join `LocalRssPostUserState ps` by post UUID and `LocalRssFeedUserState fs` by feed UUID. Project `COALESCE(ps.isRead, 0)`, `COALESCE(ps.bookmarked, 0)`, `COALESCE(ps.readRevision, 0)`, and `COALESCE(fs.pinned, 0)`. Each primary key permits at most one matching state row, so neither join may duplicate posts.

| Situation | Required behavior |
| --- | --- |
| Mark a row read in **All** | Keep its position and apply the subtle read-item de-emphasis. Keep every action enabled. |
| Mark a row read in **Unread** | Remove it after commit. Preserve the nearest surviving anchor and current scroll position. |
| Mark a row unread in **Read** | Remove it after commit, using the same anchor rule. |
| Open a row from **Unread** in a two-pane layout | Keep the reader open even when that row disappears from the list. Do not auto-select the next article. |
| Return to **Unread** after reading the last matching row | Show **No unread posts match these filters**. Keep filters and offer **Show all posts**. |
| Search text has fewer than three characters | Preserve the current search threshold. Explain the threshold if the current UI already does. Never silently broaden a bulk operation because search is inactive. |
| A refresh invalidates paging while a row disappears | Reload from the captured sort value and UUID, not a query that first requires the deleted anchor row to exist. E6 already represents those cursor values. |
| Another screen changes the state | Refresh all relevant observed lists, including recent posts and detail. |
| Clear filters | Restore **All**, while retaining the feed route's required feed scope. |

Do not filter a `PagingData` page in memory. That creates short or empty pages even when unread rows exist later, and makes global counts and bulk membership wrong. Add both user-state tables to `getTablesToTrack()` and the relevant Room observed entities. Measure invalidation and first-frame behavior because these tables introduce more sources of paging refreshes.

Keep read state out of `updatedAt`, publication dates, and sort keys. Those fields describe publisher content or existing content processing. A reading action must not reorder **Newest**, **Oldest**, or title sorts.

### Preserve search, bookmark, pin, and pagination contracts

The user-state split changes the source of existing values, not the meaning of search or existing filters. Complete these query changes as part of the schema foundation, before removing old columns.

| Query or observer | Required change and invariant |
| --- | --- |
| `LocalFeedPostSqlQueryBuilder.appendProjection` and `buildWhereClauses` | Join both state tables. Resolve bookmark, read, and pinned-feed predicates against the effective joined values. Preserve the existing AND composition and optional FTS membership. |
| `FeedColumnsSelector.BOOKMARKED` and `FEED_PINNED` | Replace entity-owned expressions with joined state expressions. Keep projection aliases distinct from entity columns and update all uses together. |
| `FeedPostDao.observePost` and `observeRecentPosts` | Return joined post and feed user state. Recent previews and open detail must update on bookmark, read, and pin changes. |
| `FeedPostDao.getPostPage` and `LocalRssPostWithFeedMetadataPagingDataProvider.getTablesToTrack` | Observe both new tables. A read or bookmark command can remove a row from a filtered result. A feed pin change can change membership for every post in that feed. |
| `FeedDao.getFeedPagingSource`, `getPinnedFeedPagingSource`, `getPinnedAndRecentFeeds`, and `observeFeed` | Return a feed-plus-user-state projection. Filter and sort pins using `COALESCE(fs.pinned, 0)`. Preserve the existing alphabetical ordering after pin precedence and add feed UUID as a deterministic final tie breaker. |
| `FeedDao.getFeedIdentityBySourceUrl` and its mapper callers | Return the user state through a composed projection where consumers need it. The publisher identity used for refresh must not own pin or opening writes. |
| `FeedDao.searchFeeds` | Retain FTS rank and feed UUID ordering. Join feed user state for the displayed pin value and observe it. Search matches remain based on title, name, and description. |
| `RssPostMapper`, `RssFeedMapper`, `PostRepositoryImpl`, and `FeedRepositoryImpl` | Recompose displayed state from projections. Refresh entity mappers stop copying bookmark, pin, and opening state. Command methods write only their owned state fields. |
| `FeedSyncMetadataDao.getFeedsPastNextEligibleFetch` and `LocalRssFeedSyncMetadataWithIdentity` | Remove the old `identity_pinned` projection and user-owned fields from refresh identity contracts. Do not join user state into the worker merely to preserve obsolete mapper arguments. |

Keep user state out of the post, article-content, and feed FTS indexes. Reading, bookmarking, pinning, and opening a feed must not rewrite text indexes. Existing search remains case and tokenizer behavior controlled by the current FTS path, including the three-character UI activation rule. Extend projection and observer coverage without introducing another search pipeline.

Post lists retain their existing keyset cursors based on the chosen publisher sort value and UUID. Bookmark, pin, and read changes can alter query membership, so invalidation must refresh around the last surviving anchor without adding user-state revisions to the cursor. Feed lists currently use offset paging. Preserve that mechanism and add deterministic UUID tie breakers. Do not bundle a feed keyset migration into this change.

`getPinnedAndRecentFeeds` currently orders by pin, name, and title. Moving `lastOpenedAt` does not authorize changing that list into a new recency order. Preserve the current order. Keep the opening timestamp nullable and retain its existing write behavior until a separate recency design establishes otherwise.

Test joined search and filter behavior for absent state rows as well as present false values. The false bookmark, unread, and unpinned branches must include missing rows. A plain inner join or `ps.bookmarked = 0` without default handling would incorrectly hide them.

Defer new unread-count badges on feed cards, tabs, and navigation. The filter and subtle read-item de-emphasis deliver the requested workflow without adding several count observers and badge layouts. The bulk dialog still needs its scoped unread count. If aggregate badges are separately approved later, derive them with database aggregation over posts and reading state, observe both tables, and apply the badge's documented feed scope. Never count loaded pages or maintain a second mutable counter. Measure aggregate invalidation under bulk changes before showing global counts. Aggregate counts do not change the user's instruction to omit visible per-post status markers.

### Cover the requested mockup contexts

The in-conversation prototype for this design update covers the following contexts. It is a visual proposal for review, not an implementation or proof of runtime behavior. Use the same neutral de-emphasis rule in each list context.

| Mockup context | What the review must show |
| --- | --- |
| Home recent posts | Read and unread items together, with subtly dimmer read content and normally contrasted bookmark and menu controls. |
| All posts and feed detail | The same treatment in the full list and a feed-scoped list, including cards with and without images. |
| Search and bookmarked results | Search highlights remain legible on read items. The selected bookmark stays clear and fully interactive. |
| Item overflow menu | A read item offers **Mark unread**. An unread item offers **Mark read**. Neither requires a status dot or badge. |
| Article reader | **Mark unread** remains available after automatic marking. The article body remains at normal contrast and opacity. |
| Reading filter | **All**, **Unread**, and **Read** have explicit labels and a clear selected state. |
| Bulk confirmation | The dialog names the matching scope and makes clear that unloaded posts are included. |
| Feedback | Successful manual and bulk actions show Undo. A persistence failure shows an explicit Retry action without a false success state. |
| Tablet list and reader | A read row can disappear from an unread-filtered list while its fully legible article remains open. The list and reader controls remain active. |

Check the dimming in light and dark themes, at large text sizes, and with keyboard focus. A static mockup cannot establish actual color contrast, touch targets, or screen-reader semantics. Verify those against the real theme tokens and app controls during implementation.

### Decide how existing and future posts begin

Start all existing posts as unread after migration. There is no source-backed visit history to infer which ones were read. Start new posts as unread, including old articles received late. Keep **All** as default so the migration does not suddenly hide content or impose an inbox workflow. Scoped bulk read gives users a deliberate way to clear an old backlog.

Do not mark existing rows read merely because their publication date precedes upgrade time. That would silently assert a history the app never recorded. A one-time upgrade prompt is unnecessary when the feature does not change the default list and offers a bulk cleanup action.

Preserve reading state for an existing UUID across title, body, date, or image edits. Do not reset read state because content changed. The user may have acknowledged the item even when a publisher corrects it. A future **Updated since read** feature would need a separate content-version model.

E3 includes the feed ID in the fingerprint. The same article URL in different subscribed feeds therefore has separate post identities and separate reading states. Keep that behavior. Global URL deduplication would require canonicalization and changes to existing post identity beyond this feature.

If the publisher changes the identity inputs enough that the existing pipeline creates a new UUID, the new post starts unread. This is an existing identity limitation, not a read-state migration problem. Test it and document it without adding heuristic URL matching.

Deleting an actual post cascades to its reading state. Deleting a feed cascades through its posts. Reimporting after deletion starts fresh. Do not prune a state row merely because an item is absent from one fetched feed document. A missing feed item is not proof that its locally stored post was deleted.

## Separate user state before adding reader exposure

### Use the approved shared schema

Create `LocalRssPostUserState` in `core:data:db` with these columns.

| Column | Shape | Rule |
| --- | --- | --- |
| `postId` | `LocalUUID`, primary key | Foreign key to `LocalRssContentFeedPost.id`, cascade on actual post deletion. |
| `isRead` | Boolean, non-null | Effective default is false when there is no row. |
| `bookmarked` | Boolean, non-null | Effective default is false when there is no row. Preserve existing bookmarks during a preserving migration. |
| `readRevision` | Long, non-null | Effective initial value is 0. Increase only for accepted reading commands. Bookmark changes never increment it. |

Create `LocalRssFeedUserState` alongside it.

| Column | Shape | Rule |
| --- | --- | --- |
| `feedId` | `LocalUUID`, primary key | Foreign key to `LocalRssFeed.id`, cascade on actual feed deletion. |
| `pinned` | Boolean, non-null | Effective default is false when there is no row. Preserve existing pins during a preserving migration. |
| `lastOpenedAt` | Nullable epoch milliseconds | Effective default is null. Preserve existing values during a preserving migration. |

Use composed `PostUserState` and `FeedUserState` domain values. A small `PostReadingState` view can expose `isRead` and `readRevision` for read commands if that keeps their ownership clear. Add post and feed state to observation results, including `RssPostWithFeedIdentityAndSearch`, without passing those fields through publisher refresh entities. Add observed values to `UIFeedPost` and existing feed presentation mapping. Exact composed projection names are implementation proposals, not existing APIs.

Do not store another copy in DataStore. Remove `bookmarked` from `LocalRssContentFeedPost`, and remove `pinned` and `lastOpenedAt` from `LocalRssFeed`, only after all readers and commands use the new state. Keep feed `name` and `title` in their existing entity for this release. Their custom naming semantics are unresolved, so do not invent a rename operation or change what feed search indexes.

The feed parser, sanitization hooks, network fetch, and content-extraction queue never write either user-state table. Once a state command creates a row, retain it even when every Boolean returns to false. This preserves read revision history and avoids making row presence a second user-state meaning.

Use field-specific writes. A read command updates only `isRead` and `readRevision`. Bookmark writes update only `bookmarked`. Feed pin writes update only `pinned`, and feed-opening writes update only `lastOpenedAt`. Initialize a missing row with the other fields' defaults, then update the owned fields in the same transaction. Do not use stale whole-state upserts, which would reproduce the overwrite problem inside the new tables.

| Storage branch | Decision | Reason |
| --- | --- | --- |
| A Boolean on the post entity | Reject for the current pipeline. | E3 and `PostRepositoryImpl.upsertPosts` reconstruct full rows. Copying a read flag during mapping still leaves a stale-snapshot overwrite race. |
| Inline Boolean plus publisher-only update commands | Reject for the approved design. | It can be correct, but leaves publisher and user fields in the same entity and requires every refresh writer to remember ownership. |
| Separate post and feed user-state tables | Select. | The approved separation removes reading, bookmark, pin, and opening state from publisher writes. Joins and explicit observation are the measurable cost. |
| A read-only state table while bookmarks and pins remain in entities | Superseded. | The approved scope now fixes the shared ownership problem for existing user fields before adding read behavior. A second bookmark migration later would repeat projection and query work. |
| Store only read IDs and delete the row for unread | Reject with Undo and asynchronous commands. | Deleting the row discards revision history. A stale command can see absence again and overwrite a later deliberate unread choice. |
| Full event history | Reject. | Local read state needs the latest value and a conflict check. An unbounded history does not improve the requested interaction. |
| Feed-level date plus per-post exceptions | Reject. | It adds two competing sources of truth and exception merging before bulk performance has been measured. |

There is a specific cascade risk even with a separate table. Verify that the Room `@Upsert` path updates an existing parent in place. A delete-and-reinsert strategy such as `INSERT OR REPLACE` can cascade-delete its child state. Add a real Room integration test before treating the separation as proven. Never implement refresh with parent replacement.

The user approved moving existing bookmark and feed state as part of this design. Preserve current user-facing bookmark and pin behavior while replacing their storage and commands. This shared foundation must be verified before automatic reader marking is added.

### Choose the database transition explicitly

The companion [schema and migration document](./entity-user-state-schema-and-migration.md) contains the detailed transition. Both paths produce the same final entity and state split.

| Path | When it earns its place | Consequence |
| --- | --- | --- |
| Fresh baseline at the next schema version | Recommended for an unreleased app only when all current development and test databases are explicitly disposable. | Export a fresh version 4 schema if version 3 remains current. Remove old migration edges. Old databases fail to open until an intentional clear or reinstall. Do not silently enable destructive fallback. |
| Preserving migration from current version to next | Choose when anyone needs existing subscriptions, posts, bookmarks, pins, opening history, or cached content. | Create state tables, backfill bookmark and feed state, remove obsolete indexes, then drop moved columns using a verified in-place migration. Preserve parent row IDs, FKs, and FTS. |

The version numbers are conditional on the current schema at implementation time. Do not overwrite an exported version 3 schema with a different identity under the same number. Rebasing the development schema to version 1 is a separate explicit choice and is not the recommended fresh-baseline path.

Writing these documents does not authorize clearing a database, uninstalling an app, or running a migration. Select the transition when implementation starts. If disposability is uncertain, use the preserving path. Whichever path is chosen, test its actual Room schema identity and startup behavior before considering the foundation complete.

### Give commands explicit state and conflict rules

Add `SetPostReadUseCase` and a repository operation `setPostRead`, taking the post UUID, target value, `expectedReadRevision`, and origin. Model origin as explicit `AutomaticVisit`, `Manual`, or `Undo` cases. Return `Result<PostReadMutationResult>`, with typed `Applied`, `Unchanged`, `Conflict`, and `MissingPost` outcomes. Business outcomes are not exceptions.

Run the read and conditional update in one database write transaction. A missing row is unread at `readRevision` 0. A bookmark-only row is also unread at `readRevision` 0 until a reading command occurs. Create or update reading fields only if the observed read revision still matches the expected revision. Preserve `bookmarked` throughout. Verify the parent exists inside the transaction. Use affected-row counts and explicit result mapping rather than a blind upsert that can overwrite newer state.

Automatic commands that find an already-read post return `Unchanged` without invalidation. A manual action acknowledges user intent even if the effective value already matches because another operation just changed it. Record a new revision for an accepted manual intent. This prevents an older Undo from reversing that newer acknowledgment.

Do not retry a stale command with a freshly substituted revision. That would turn a conflict check into a delayed overwrite. Observe the new state and require a fresh user action for a manual conflict. A repeated automatic exposure is already consumed. For a retry after a known persistence failure, reuse the original expected revision. If a prior attempt committed, the retry cannot increment the revision again.

An action receipt contains the prior reading value, applied `readRevision`, post UUID, and target reading value. It does not capture ownership of the bookmark. Keep it local to the active feedback action. Undo conditionally restores the prior reading value only if the applied read revision still owns the row. Undo writes a new read revision and preserves bookmark state. It never decreases or deletes the read revision.

| Race or failure | Required result |
| --- | --- |
| Refresh maps a post, user marks it read, refresh then saves | Reading row remains read. |
| Refresh maps a post or feed, user bookmarks or pins it, refresh then saves | Bookmark and pin state remain intact. |
| Bookmark command and reading command interleave | Each changes only its owned fields. Bookmark changes neither consume nor invalidate `readRevision`. |
| Read Undo follows a bookmark change | Undo restores reading state if the read revision matches and preserves the newer bookmark. |
| Automatic mark waits while the user marks unread | Stale automatic command fails its revision check. |
| Two screens submit opposite values from the same revision | One transaction applies. The other returns Conflict and observes the winning state. |
| User marks read, marks unread, then invokes the first Undo | First Undo skips the newer state. |
| User later explicitly marks the same target value | New manual revision prevents old Undo from taking ownership. |
| Post is deleted before the command | Return MissingPost and remove pending UI feedback. Never create an orphan. |
| Disk write fails or transaction is cancelled before commit | Roll back. Observed state remains authoritative. |
| Process dies after commit | State survives. A transient Undo opportunity can expire with the process. |
| ViewModel is cleared before a command commits | Use the repository transaction's cancellation rules. Never show success before commit. No background job is necessary for a local operation. |

This is local state only. Do not add server sync, cross-device ordering, accounts, or a persistent retry queue. If synchronization is later required, a local revision is not a cross-device conflict-resolution protocol.

Keep OPML focused on subscription exchange. Do not add per-post reading state to OPML import or export. A future full application backup should copy reading rows and their parent post identities together, then restore them transactionally. Importing subscriptions into a fresh database starts posts unread because UUIDs and prior reading rows are absent. Export and platform-backup behavior were not audited for this plan, so verify their current scope before promising backup persistence. Local process restart and database reopen are required persistence guarantees in this feature.

## Define scoped bulk read before adding its button

Add **Mark all as read** to the full post list and feed-detail overflow menus. The label's scope must be explicit.

| Context | Scope |
| --- | --- |
| Feed detail with no optional filters | All locally stored posts in that feed at execution time. |
| Feed detail with filters or active search | All posts matching that feed and the effective filters and search. |
| All-post list | All posts matching the current list query. |
| Recent-post preview on the feed home | No bulk action. A preview's limited row count is not an unambiguous collection boundary. |
| Search below its activation threshold, or query changing during debounce | Disable bulk until the effective query and displayed query agree. Never act on a broader hidden query. |

A confirmation dialog names the scope and says **Mark all matching posts as read? This includes posts that have not loaded on screen.** Show a matching unread count from the same query rules. The preview count can change before confirmation. The transaction's actual changed count determines the result message.

Freeze the effective query when the dialog opens. If the user changes the list query, dismiss the dialog. At confirmation, resolve and update the exact matching set inside one write transaction. New rows committed before that transaction starts are included if they match. New rows committed afterward are excluded. State this as **Posts received after this action stay unread**. Do not promise that the preview count is a frozen membership snapshot.

Select only currently unread rows. Capture IDs, prior state, and revisions inside the same transaction, then update them. Use bounded SQL argument batches inside that transaction if needed. Do not commit each batch independently. A failure leaves the whole operation unchanged. A cancellation after commit is a completed operation, not a partial failure.

Extend the existing SQL assembler to build a matching-ID selection and count from the same filter and FTS predicate definitions. Remove pagination cursor, limit, projection highlights, and sort from bulk membership. Never derive the set from loaded `LazyPagingItems`, rerun pages while mutating their filter, or construct an independent SQL interpretation of filters.

Show **N posts marked read** with Undo after commit. Keep the exact changed-ID reading receipt for the short Undo window. Undo runs conditional updates against the applied read revisions. It restores only reading values that have not received a newer reading command. Bookmark changes remain intact and do not cause a read Undo conflict. Report **Restored N posts; M changed afterward** when it skips rows. Rows already read before the bulk action never enter the receipt.

Disable duplicate bulk submission while a bulk operation is active. Do not enqueue a persistent cleanup job. For unusually large sets, measure transaction duration and receipt memory before shipping. If the acceptance budget fails, redesign bulk storage or execution before exposing the action. Never silently cap the set, drop Undo for large counts, or acknowledge only the loaded page.

Bulk **Mark unread**, selected-item bulk actions, **Mark older than**, and **Mark above this post** stay out of this release. Each needs a stronger scope explanation and creates more accidental-state risk than feed cleanup. They remain possible with the same command model, but they do not need controls now.

## Implement in four verifiable phases

This plan does not authorize implementation, schema changes, commits, PR creation, or deployment. Begin those only after the plan is accepted. Preserve unrelated dirty discovery work. Recheck current source and schema versions before starting.

Throughput checkpoint is n/a for this read-only investigation and planning document. Proposed implementation phases follow the actual data dependency, rather than parallel writers changing the same query and model files.

### Phase 1. Separate and verify all approved user state

- [ ] Select and record the fresh-baseline or preserving transition from [the migration document](./entity-user-state-schema-and-migration.md). Record which current databases need preservation. Do not clear any database as part of plan acceptance.
- [ ] Create `core/data/db/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/db/model/feed/tables/LocalRssPostUserState.kt` and `LocalRssFeedUserState.kt`. Add field-specific `dao/PostUserStateDao.kt` and `dao/FeedUserStateDao.kt` operations with sparse defaults, retained rows, and cascading foreign keys.
- [ ] Update `SchemaVersions.kt` and `DevPulseDatabase.kt` for the selected transition. Export the final schema at the next version. On the preserving path, backfill user fields before removing old columns and related indexes. On the fresh path, verify explicit failure on an old database rather than destructive fallback.
- [ ] Extend `LocalRssPostWithFeedAndSearch` and feed projections with composed user state. Update `FeedPostDao.observePost`, `observeRecentPosts`, and `getPostPage`, plus every `FeedDao` observer and projection named in the query matrix above.
- [ ] Update `LocalFeedPostSqlQueryBuilder`, `FeedColumnsSelector`, feed search SQL, bookmark and pin predicates, and `LocalRssPostWithFeedMetadataPagingDataProvider.getTablesToTrack()`. Observe both state tables. Preserve post keyset ordering and feed offset paging with deterministic UUID tie breakers.
- [ ] Move bookmark and pin writes from `FeedPostDao.updateBookmarkStatus` and `FeedDao.setFeedPinned` to their field-specific state commands. Update `SetPostBookmarkedUseCase`, `SetFeedPinnedUseCase`, `PostRepositoryImpl`, and `FeedRepositoryImpl` without changing visible action semantics. No dedicated `lastOpenedAt` writer was found in the inspected feed code. Preserve its stored values without adding open tracking.
- [ ] Remove moved fields from publisher entities, identity slices, and refresh mappers. Update `RssPostMapper`, `RssFeedMapper`, `FeedSyncMetadataDao.getFeedsPastNextEligibleFetch`, and `LocalRssFeedSyncMetadataWithIdentity` so parsed publisher data and worker identity contracts cannot carry user-owned fields back into an upsert. Retain current feed name and title fields.
- [ ] Create composed domain `PostUserState` and `FeedUserState`, plus `PostReadMutationResult` and reading command origin types. Update `RssPostWithFeedIdentityAndSearch`, feed observation models, UI mapping, and all affected callers together.
- [ ] Add `setPostRead` to `core/data/feed/.../repository/PostRepository.kt` and implement it in `PostRepositoryImpl.kt` with the existing `DevPulseDatabaseTransactionAccessor`.
- [ ] Create `core/domain/feed/.../posts/SetPostReadUseCase.kt`. Follow annotation-based Koin registration. Register the DAO through the existing DB DI pattern after inspecting its current provider.
- [ ] Add real Room JVM integration tests in `core/data/db/src/jvmTest/.../UserStateDatabaseTest.kt`. Cover sparse defaults, the selected schema transition, field-specific interleavings, conditional reading mutations, duplicate retries, parent refresh, cascades, and observer invalidation.

The phase is complete when bookmark and pin actions still work across every existing screen, search, and filter, and refresh cannot overwrite them. Reading commands must persist across reopen and reject stale read revisions. No automatic read trigger ships before this gate. A Kotlin compile alone does not establish these properties.

### Phase 2. Add manual actions and reader exposure

- [ ] Extend `UIFeedPost` and `PostInteractor.toUIFeedPost` with the observed reading state.
- [ ] Extend the feature and shared `FeedPostListItem` functions with neutral read-content de-emphasis and an explicit action callback. Keep bookmark and overflow controls at normal contrast. Add no visible per-post status marker. Update call sites in feed-home posts, `PostListScreen`, and `FeedDetailScreen`.
- [ ] Add read commands, pending state, success, failure, and Undo effects to `FeedScreenEvent`, `PostListScreenEvent`, `FeedDetailScreenEvent`, and their ViewModels. Reuse one feature-level action processor if the command-result handling would otherwise be repeated, following the existing feed-options processor pattern. Do not add a generic event framework.
- [ ] Update `PostDetailScreenEvent`, `PostDetailScreenState`, `PostDetailViewModel`, `PostDetailScreen`, and reader components. Add the visit decision, explicit read action, body-readiness event, and revision-aware automatic command.
- [ ] Determine actual adaptive entry visibility through the Navigation3 entry lifecycle. Verify it on both pane sizes before choosing the exact lifecycle hook. Composition alone is insufficient. Keep the decision inside the entry's saved state.
- [ ] Add strings to `core/resources/src/commonMain/composeResources/values/strings.xml` and pluralized result counts to `plurals.xml`. Use state descriptions, action labels, failure feedback, and Undo text.
- [ ] Add focused behavior tests for fresh exposure, rebind, content fallbacks, manual unread suppression, restoration, and delayed commands. Test rendered state and repository effects rather than calls to private helpers.

The phase is complete when a reader visit produces at most one automatic decision and a manual unread choice survives recompose, resume, nested navigation, and restoration of that visit.

### Phase 3. Filter every paged list consistently

- [ ] Add `PostReadFilter`, `RssPostFilter.ReadStatus`, and `LocalFeedPostFilter.ReadStatus`.
- [ ] Update `RssPostQueryMapper`, `RssPostFilter.order`, `isEmpty`, `PostInteractor.getDefaultFiltersFor`, `PostSortAndFilterViewModel.clearFilters`, and filter rendering in `components/postFilterSort/filter/FilterItems.kt` and its icon mapping.
- [ ] Add status predicates to `LocalFeedPostSqlQueryBuilder.buildWhereClauses` without adding a second paging pipeline. Preserve the existing optional FTS path and search threshold.
- [ ] Add **All**, **Unread**, and **Read** choices using existing DP filter controls. Add explicit empty states and **Show all posts** behavior without clearing required route scope.
- [ ] Extend real database query tests for all existing sort modes, tied sort values, FTS, feed and bookmark combinations, missing state rows, and removal of an anchor row.
- [ ] Drive narrow and two-pane layouts. Verify that state changes remove only the correct rows and retain the active reader and surviving scroll anchor.

The phase is complete when every list displays the same stored state and SQL returns complete results across page boundaries. A short visible page must not conceal matching posts.

### Phase 4. Add scoped bulk read and conditional Undo

- [ ] Extend the existing query assembler with `buildMatchingPostIds` and `buildMatchingUnreadCount`, reusing the same filter and search predicate construction. Names are proposed signatures, not existing functions.
- [ ] Add `MarkPostsReadUseCase`, a typed query-scope command, and repository transaction methods. Keep exact membership resolution, prior-state receipt capture, and bounded writes in one transaction.
- [ ] Add bulk pending state and confirmation to `PostListViewModel` and `FeedDetailViewModel`. Disable bulk while displayed and effective search differ.
- [ ] Add count preview, scope text, confirmation, result count, and conditional Undo. Dismiss confirmation when its query changes.
- [ ] Test an operation spanning several pages, concurrent imports, concurrent manual actions, partial Undo, deletion, transaction failure, and cancellation before and after commit.
- [ ] Measure large-set duration, writer blocking, paging recovery, and Undo receipt memory before exposing the action in release builds.

The phase is complete when the actual changed set matches the confirmed query at its documented transaction boundary. No later-arriving post or later manual command is reversed by an old receipt.

## Verification gates

Tests alone do not establish reader visibility or list stability. Complete database behavior checks, relevant compilation, and live interaction checks before declaring the feature finished. This document records planned checks, not passed checks.

### Establish the baseline and compile affected modules

Before feature edits, record the current branch and dirty files, run the narrow existing test baseline, and distinguish baseline failures from new ones. E11 demonstrates a source-level test drift risk. Do not repair unrelated discovery changes as part of this feature.

Use the affected module commands below after confirming their task availability. These are targeted commands, not evidence that the tasks have passed.

```sh
./gradlew :core:data:db:jvmTest :core:data:feed:jvmTest :core:domain:feed:jvmTest
./gradlew :core:data:db:compileKotlinIosSimulatorArm64 :core:data:feed:compileKotlinIosSimulatorArm64
./gradlew :core:domain:feed:compileKotlinIosSimulatorArm64 :feature:feed:compileKotlinIosSimulatorArm64
./gradlew :feature:feed:compileAndroidMain
```

Add feature ViewModel tests to the existing supported host test target after checking its configuration. Do not invent a `ktlintCheck` task. Use `git diff --check` for whitespace only. Neither it nor successful compilation substitutes for the tests below.

### Verify database behavior against real queries

| Scenario | Pass condition |
| --- | --- |
| Upgrade a populated database on the preserving path | Feed, post, bookmark, pin, opening, and content data survive. Existing posts are effectively unread at read revision 0. Parent row IDs and FTS behavior remain intact. |
| Open old schema on the fresh-baseline path | Startup fails explicitly until an intentional reset. A separately created new database has the final schema and sparse state defaults. |
| Mark read, close the database, reopen | State and revision remain correct. |
| Refresh a known post after reading | Publisher fields update while reading state remains intact. Test the actual Room upsert implementation. |
| Parse first, read second, save parsed post third | Read state survives the intentionally interleaved stale-content save. |
| Opposite commands share one expected revision | Exactly one applies. The loser reports Conflict. |
| Retry the same command after uncertain completion | It cannot apply twice or overwrite a later revision. |
| Undo after another manual command | Newer intent remains. Undo reports skipped rows. |
| Bookmark changes between read and Undo | Reading Undo preserves the bookmark and still applies when `readRevision` matches. |
| Pin or opening writes interleave with feed refresh | Both user fields retain their latest committed values. |
| Delete a post or feed | Reading rows cascade with actual deletion and no orphan remains. |
| Same URL appears in two feeds | Marking one UUID does not mark the other. |
| Query unread with more rows than one page | All matching UUIDs appear once across pages. |
| Remove the anchor from unread | The next refresh loads surviving neighbors without duplicates or missing ranges. |
| Search plus unread plus a second filter | SQL membership agrees with the equivalent unpaged matching-ID selection. |
| Missing state plus false bookmark, unread, or unpinned filters | Missing rows match the default false branch. Left joins neither lose nor duplicate entities. |
| Feed search after pin changes | Pin display updates without a change to text matches, rank, title, or name behavior. |
| Bookmarked and pinned-feed post filters after state changes | Results change from the shared state joins while every post sort remains deterministic. |
| Change read state while list and detail are observed | Every observer emits the committed value. |
| Fail a bulk transaction in a later batch | No batch is committed. |
| Import concurrently with bulk | Membership follows the transaction boundary and the actual result count. |

### Drive the reader and lists on the real app

Run the following scenarios on Android narrow and wide layouts. Repeat the reader lifecycle and manual-action flows on iOS and Desktop because the app targets all three. Use the available native simulator or device control workflow. Save screenshots for state and a short recording for removal, anchor stability, and two-pane behavior.

1. Open unread content online and offline. Confirm one automatic decision when a qualifying body appears.
2. Block article extraction while showing RSS or description fallback. Confirm that the fallback qualifies. Then block every body source and confirm that an empty or error reader remains unread.
3. Mark unread inside the visible reader. Recompose, change text settings, switch sections, rotate, background, resume, and enter and return from an image. Confirm unread survives the visit.
4. Restore the same navigation entry after process death. Confirm that the saved visit decision does not override stored unread. Pop and reopen explicitly, then confirm a new visit can mark read.
5. Open from **Unread** in a two-pane layout. Confirm the list row disappears after commit while the article remains visible. Confirm there is no automatic next selection.
6. Mark rows with different read states through feed home, feed detail, search, and the full post list. Confirm one canonical value everywhere.
7. Use keyboard focus, screen reader actions, large text, light and dark themes, and every list-item size. Confirm state is spoken and FTS highlights remain legible. Confirm subtly dimmer read items still open, retain normal focus feedback, and expose active bookmark and menu controls. Confirm the article body never dims after marking read.
8. Force a write failure. Confirm no false success, no permanent busy state, and an explicit retry path.
9. Bulk mark a filtered result larger than the loaded pages. Import another post during the action and manually change a row before Undo. Confirm exact counts and preservation of later intent.
10. Compare the same feed and paging scenario on baseline and feature code. Baseline lacks read marking, so compare rendering and paging metrics separately from the added write and visible-state completion.

### Measure the costs that could change the design

Use a seeded database with 10,000 posts for the routine case and 100,000 for a stress case. Include sparse and dense read-state tables, one large feed, many small feeds, tied dates, missing dates, and FTS matches. Record the device, build mode, dataset, and raw timings.

Measure baseline first. Then interleave baseline and feature runs under the same configuration. Use at least 20 runs for single-post writes and page loads. Measure the first page, append, anchor refresh, manual commit-to-observed-state duration, bulk transaction duration, and Undo receipt memory. Inspect `EXPLAIN QUERY PLAN` for **All**, **Unread**, and **Read**, with and without FTS and feed scope.

Proposed release gates are a p95 manual commit-to-observed-state duration below 250 ms on the chosen reference device, no more than a 20% p95 regression for equivalent existing page-load scenarios, and no visible main-thread blocking during bulk processing. For the 10,000-row bulk case, propose a commit-to-stable-list budget below 2 seconds and a database write transaction below 1 second. These numbers require agreement against actual baseline and device capability before implementation is called verified. The 100,000-row case identifies the failure point and memory cost rather than granting silent partial behavior.

Do not add a Boolean-only index by habit. Read, bookmark, and pin selectivity changes over time, and joins may make such indexes irrelevant. If the approved design fails the query budget, compare indexed state joins and equivalent `EXISTS` predicates using the same dataset. Do not return user fields to publisher entities as a query shortcut. Reconsidering the approved separation requires a new design decision. Do not claim that separate state is faster before measuring it.

## Final options

### Option A. Automatically acknowledge a visible reader

Recommend this option. The app marks a post read once qualifying reader content appears. Users can always restore unread, with that explicit choice protected for the rest of the visit. Provide **All**, **Unread**, and **Read** filters and the scoped bulk cleanup action.

This minimizes repeated actions for normal reading. Its strongest counterargument is semantic certainty. Opening a readable article does not prove the user actually read it. The feature must use acknowledgment semantics and make **Mark unread** easy to find.

The recommendation fails if device trials show that readers are displayed without intentional exposure, or users routinely open articles as previews they want to remain unread. Fix the visibility signal first. If the remaining disagreement is about meaning, select Option B.

### Option B. Require explicit acknowledgment

Choose this only if read status means a deliberate completion or triage decision. Remove automatic exposure commands. Keep the same storage, manual actions, Undo, filters, and bulk semantics. The reader offers **Mark read** prominently enough that users can finish the workflow.

This avoids assumptions about attention and reduces lifecycle logic. Its cost is one more action per article and a greater chance that the unread list remains inaccurate because users forget. It is worth considering for a deliberate work queue, but the inspected app is currently a feed reader rather than an enforced completion workflow.

Do not ship an automatic-versus-manual preference in the first version. A setting multiplies lifecycle, documentation, and verification cases before there is evidence of two real user groups. Pick the product meaning first. Retain the same persistence design whichever option is selected.

## Decisions deliberately left for implementation evidence

The feature semantics above are resolved recommendations. These narrower choices need observation during implementation, rather than another speculative option list.

- Confirm the exact Navigation3 visibility hook on narrow and two-pane layouts. It must distinguish a retained entry from a visible reader.
- Confirm the existing DP menu and feedback components can support the action and Undo without raw Material additions.
- Verify current Room upsert behavior and migration tooling using integration tests.
- Measure whether the state join needs query or index tuning. Keep performance claims provisional until the query plan and timings exist.
- Establish the current test and KSP baseline before attributing failures. Existing test source and historical notes do not establish current build status.

The Experience First principle selected subtle read-content de-emphasis, fully interactive controls, an explicit unread action, stable reader behavior, and **All** by default. The user's visual preference excludes visible per-post status markers. Model the Domain selected one persisted reading-state value, a visit decision, and typed command outcomes. Make Operations Idempotent selected expected revisions and conditional Undo. Prove It Works selected real database and device gates instead of treating compilation as proof of user behavior.
