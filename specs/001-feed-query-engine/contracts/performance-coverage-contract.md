# Contract: Performance Coverage

## Purpose

Classify supported query combinations as common optimized or rare supported. Common optimized profiles need explicit index support and verification. Rare supported profiles must remain correct but do not require a dedicated composite index unless promoted later.

## Common Optimized Profiles

| Profile | Filters | Sort | Required access path |
|---------|---------|------|----------------------|
| `TimelineLatest` | None | `PublishedNewest` | Post index on `publishedSortAt`, `updatedAt`, `id` |
| `FeedTimelineLatest` | `FeedIds` | `PublishedNewest` | Post index on `feedId`, `publishedSortAt`, `updatedAt`, `id` |
| `BookmarkedTimelineLatest` | `Bookmarked` | `PublishedNewest` | Post index on `bookmarked`, `publishedSortAt`, `updatedAt`, `id` |
| `FeedBookmarkedTimelineLatest` | `FeedIds`, `Bookmarked` | `PublishedNewest` | Post index on `feedId`, `bookmarked`, `publishedSortAt`, `updatedAt`, `id` |
| `PinnedFeedsTimelineLatest` | Optional `PinnedFeed` | `PinnedFeedsFirstLatest` | Post index on `feedPinnedSort`, `publishedSortAt`, `updatedAt`, `id` |
| `UpdatedLatest` | None or `FeedIds` | `UpdatedNewest` | Post index on `updatedAt`, `id`; add `feedId`, `updatedAt`, `id` only if feed-scoped updated sorting is promoted |
| `CreatedLatest` | None | `CreatedNewest` | Post index on `createdAt`, `id` |
| `TitleAlphabetical` | None | `TitleAtoZ` | Post index on `titleSort`, `id` |
| `FeedNameAlphabetical` | None | `FeedNameAtoZ` | Post index on `feedNameSort`, `publishedSortAt`, `id` |

## Shared Supporting Indexes

- Feed source lookup: feed source URL unique index.
- Tag filtering: post-tag mapping index on tag ID and post ID.
- Category filtering: post-category value index on normalized category and post ID.
- Feed display/pin maintenance: feed index on display sort name and feed ID, and feed pin state where required by feed-list queries.

## Rare Supported Profiles

Rare profiles remain correctness-tested but do not receive every composite index initially:

- `SearchText` combined with any sort.
- `Author` combined with any sort.
- `SourceFeed` beyond direct feed identity filtering.
- Media presence filters combined with alphabetical or feed-name sorts.
- Category filtering combined with non-default sorts.
- Tag all-match filtering combined with non-default sorts.
- Multi-filter combinations involving three or more low-frequency filters.

## Promotion Rules

- Promote a rare profile to common when product UI makes it a primary flow or measurements show it misses the user-facing target frequently.
- Promotion requires a named coverage entry, expected access path, query tests, paging tests, and performance verification.
- Do not keep a shorter prefix index when a longer common index covers the same leading columns and no measured query requires the shorter index.
- Do not add composite indexes for speculative combinations without a named profile.

## Verification Rules

- Each common optimized profile must have a query-builder test asserting the selected order and filter clauses.
- Each common optimized profile must have a cursor test proving no duplicate or skipped IDs across pages.
- Each common optimized profile must have an explain-plan or benchmark-style verification during implementation.
- Rare profiles must have correctness tests and documented performance classification.
