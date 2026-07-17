# Feature Specification: Feed Query Engine

**Feature Branch**: `feat/feed-details`

**Created**: 2026-06-11

**Status**: Draft

**Input**: User description: "Improve feed post query and pagination support with named filters, named sorts, dynamic safe combinations, stable cursor pagination, and performance coverage for common combinations while avoiding unnecessary storage overhead for rare combinations."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Browse A Stable Feed Timeline (Priority: P1)

As a DevPulse reader, I want feed post lists to stay stable while I scroll, filter, and sort so that I do not see duplicate posts, skipped posts, or sudden reordering caused by tied dates or missing values.

**Why this priority**: The feed timeline is the primary read path, and pagination correctness affects every list that displays posts.

**Independent Test**: Can be fully tested by loading a large mixed feed dataset, scrolling across multiple pages with the default timeline sort, refreshing around an anchor item, and verifying that the visible post order is stable with no duplicates or gaps.

**Acceptance Scenarios**:

1. **Given** posts with identical published times, identical updated times, and different identifiers, **When** the reader scrolls through the default timeline, **Then** every post appears once in a deterministic order.
2. **Given** posts with missing published times, **When** the reader sorts by published newest or oldest, **Then** those posts appear in a documented missing-value position and do not break pagination.
3. **Given** new posts arrive or existing posts are updated while the reader is paging, **When** the list refreshes around the current anchor, **Then** the reader remains near the same post without duplicates or skipped rows.

---

### User Story 2 - Combine Named Filters And Sorts (Priority: P2)

As a DevPulse reader, I want to combine supported feed, saved-item, media, date, source, category, author, tag, and search filters with supported sort modes so that I can quickly narrow the feed to the content I care about.

**Why this priority**: The improved query engine only pays off if callers can safely compose real product filters instead of maintaining one-off query variants.

**Independent Test**: Can be fully tested by applying each supported filter independently, applying representative filter combinations, changing sort modes, and comparing the resulting ordered posts against a trusted reference dataset.

**Acceptance Scenarios**:

1. **Given** the reader filters to one or more feeds and sorts by newest published posts, **When** the list loads, **Then** only posts from those feeds appear in newest-first order.
2. **Given** the reader filters to bookmarked posts that include audio but exclude video, **When** the list loads, **Then** every result is bookmarked, has audio, and has no video match.
3. **Given** the reader filters by tag, category, or author and changes the sort to title order, **When** the list loads, **Then** the same filtered result set is presented in title order with a stable tie-break.
4. **Given** the reader enters search text containing punctuation or wildcard-like characters, **When** the search filter is applied, **Then** results match searchable text without broadening the query unexpectedly.

---

### User Story 3 - Maintain Predictable Performance Coverage (Priority: P3)

As a DevPulse maintainer, I want a named coverage plan for common and rare filter/sort combinations so that common user flows stay fast without adding unnecessary storage cost for uncommon combinations.

**Why this priority**: Dynamic combinations can grow quickly; the feature needs a clear boundary between optimized paths and accepted slower paths.

**Independent Test**: Can be fully tested by running the common coverage matrix against a seeded dataset, confirming each common combination meets the visible load target, and confirming rare combinations remain supported with documented performance expectations.

**Acceptance Scenarios**:

1. **Given** the common coverage matrix, **When** each common combination is exercised on a representative dataset, **Then** it meets the target first-page and next-page load times.
2. **Given** a rare combination that is supported but not optimized, **When** it is exercised, **Then** the result is correct and the documented performance classification explains the expected trade-off.
3. **Given** a maintainer adds a new filter or sort in the future, **When** it is reviewed, **Then** it must be classified as common or rare before release.

### Edge Cases

- Missing published time, title, feed name, author, source, category, image, audio, video, or enclosure values must have deterministic filter and sort behavior.
- Positive and negative media filters must handle missing media fields without incorrectly excluding or including posts.
- Multi-value filters with duplicate values must behave the same as the unique value set.
- Explicit empty multi-value filters must not accidentally return all posts; callers must be able to distinguish "no filter selected" from "filter selected with no values".
- Large feed, tag, category, or source selections must keep argument ordering deterministic so identical requests produce equivalent results.
- Changing filters or sort mode must invalidate any cursor from the previous query shape.
- Category filtering must match category values, not accidental substrings inside a serialized category string.
- Search text must handle blank input, leading/trailing whitespace, punctuation, and case differences predictably.
- Unsupported filter or sort names must fail deterministically before a query is executed.
- Rare combinations may be slower than common combinations, but they must not return incorrect ordering or pagination results.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST expose a named filter catalog containing `FeedIds`, `TagIdsAny`, `TagIdsAll`, `Bookmarked`, `PinnedFeed`, `PublishedRange`, `UpdatedRange`, `CreatedRange`, `HasImage`, `HasAudio`, `HasVideo`, `HasYouTubeData`, `HasEnclosure`, `Author`, `SourceFeed`, `Category`, and `SearchText`.
- **FR-002**: The system MUST expose a named sort catalog containing `PublishedNewest`, `PublishedOldest`, `UpdatedNewest`, `UpdatedOldest`, `CreatedNewest`, `CreatedOldest`, `TitleAtoZ`, `TitleZtoA`, `FeedNameAtoZ`, `FeedNameZtoA`, `BookmarkedFirstLatest`, and `PinnedFeedsFirstLatest`.
- **FR-003**: The system MUST generate post retrieval requests only from the named filter and sort catalogs.
- **FR-004**: The system MUST reject unsupported filter names, unsupported sort names, and cursor values that do not match the active sort shape before retrieving posts.
- **FR-005**: Every supported sort MUST define a complete deterministic ordering that ends with a unique post identity tie-breaker.
- **FR-006**: Every supported sort MUST define how missing values are ordered, including missing published time, title, and feed name.
- **FR-007**: Pagination MUST be cursor-based and MUST NOT rely on page-number or offset-based scrolling for feed post lists.
- **FR-008**: Cursor pagination MUST support initial page load, next page load, and refresh around an existing anchor item for every supported sort.
- **FR-009**: Multi-value filters MUST bind each selected value independently and MUST preserve deterministic argument ordering.
- **FR-010**: Boolean filters MUST support positive and negative matches where meaningful, including bookmarked, pinned feed, image, audio, video, YouTube data, and enclosure filters.
- **FR-011**: Date range filters MUST support minimum-only, maximum-only, and bounded ranges for published, updated, and created timestamps.
- **FR-012**: Tag filtering MUST support both "match any selected tag" and "match all selected tags" behavior as separate named filters.
- **FR-013**: Category filtering MUST operate on category values and MUST avoid false positives caused by matching unrelated text.
- **FR-014**: Search filtering MUST be optional, must ignore blank input, and must match title, description, content, author, feed/source name, and source URL where those values exist.
- **FR-015**: The default post retrieval behavior MUST be `PublishedNewest` with no filters applied.
- **FR-016**: The system MUST define a common performance coverage matrix for `TimelineLatest`, `FeedTimelineLatest`, `BookmarkedTimelineLatest`, `FeedBookmarkedTimelineLatest`, `PinnedFeedsTimelineLatest`, `UpdatedLatest`, `CreatedLatest`, `TitleAlphabetical`, and `FeedNameAlphabetical`.
- **FR-017**: Common performance combinations MUST have optimized access paths and verification coverage before release.
- **FR-018**: Rare supported combinations MAY omit dedicated optimized access paths when the storage cost is not justified, but their expected performance class MUST be documented.
- **FR-019**: Common and rare combination classifications MUST be reviewable as named values, not implied by scattered conditional logic.
- **FR-020**: The system MUST preserve the existing feed-with-post metadata result shape so current feed screens can migrate without losing displayed post or feed information.
- **FR-021**: The system MUST provide acceptance coverage for default timeline paging, feed-scoped paging, bookmarked paging, tag filtering, positive and negative media filtering, missing-value ordering, search filtering, and every named sort.
- **FR-022**: The system MUST provide a migration-safe path for any new stored values needed to make missing-value sorting deterministic.

### Key Entities *(include if feature involves data)*

- **Feed Post**: A saved article or media item from a feed, including identity, feed identity, title, author, dates, content, media indicators, categories, saved state, and source metadata.
- **Feed**: The source that owns posts, including identity, display name, source URL, pinned state, and update metadata.
- **Post Query Spec**: A complete request for posts, composed from named filters, one named sort, an optional cursor, and a requested page size.
- **Filter Option**: A whitelisted post restriction with a clear name, value type, matching behavior, and missing-value behavior.
- **Sort Option**: A whitelisted ordering mode with explicit direction, missing-value placement, secondary ordering, and unique identity tie-breaker.
- **Cursor**: A token derived from the active sort values and post identity that identifies where paging should continue or refresh.
- **Performance Coverage Profile**: A named classification that marks a filter/sort combination as common and optimized or rare and supported without dedicated storage optimization.
- **Category Value**: A normalized category assigned to a post and matched as a value instead of as a raw text fragment.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: On a representative dataset of at least 10,000 posts across at least 200 feeds, common combinations show the first visible page in under 1 second for 95% of attempts on target development hardware.
- **SC-002**: On the same dataset, next-page loads for common combinations complete in under 500 milliseconds for 95% of attempts on target development hardware.
- **SC-003**: Across 100 repeated paging runs per named sort, no run contains duplicate posts, skipped posts, or order instability when compared with the full ordered result set.
- **SC-004**: 100% of named filters and named sorts have acceptance coverage for both query result correctness and cursor compatibility.
- **SC-005**: At least 90% of tested combinations made from the named catalog return the same ordered result set as the reference evaluator.
- **SC-006**: Rare combination support adds no more than the approved storage budget determined during planning, while every common combination is covered by an explicit performance profile.
- **SC-007**: A maintainer can identify whether a filter/sort combination is common or rare from one named coverage list in under 2 minutes.
- **SC-008**: Existing feed screens can adopt the improved query engine without changing the post and feed information they display.

## Assumptions

- This feature targets feed post retrieval and pagination behavior; it does not require new feed reader UI controls in the first implementation.
- Search is included as a supported filter because this feature request expands the previous feed-post query scope.
- Combined "has media" filtering is not a supported named filter; image, audio, video, YouTube data, and enclosure are separate filters.
- Common combinations are initially defined by current app flows: all-post timelines, feed detail timelines, bookmarked timelines, pinned-feed timelines, and alphabetical browse modes.
- Rare combinations remain supported for correctness, but planning may decide they are acceptable without dedicated storage optimization.
- Category filtering is expected to become value-based if existing stored category text cannot satisfy exact category matching reliably.
- The improved query engine must remain compatible with the existing post-with-feed metadata projection used by current feed screens.
