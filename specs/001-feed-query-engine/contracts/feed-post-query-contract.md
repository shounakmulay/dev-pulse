# Contract: Feed Post Query

## Purpose

Define the allowed request shape for retrieving feed posts. All callers must use this contract instead of providing SQL fragments, column names, or arbitrary order clauses.

## Request Shape

Required values:

- `sort`: One supported sort option.
- `pageSize`: Positive page size within the paging configuration limit.

Optional values:

- `filters`: Zero or more supported filters.
- `cursor`: Cursor produced by a previous response using the same filter and sort shape.

## Supported Filters

| Name | Value shape | Match behavior | Missing-value behavior |
|------|-------------|----------------|------------------------|
| `FeedIds` | Set of feed IDs | Post feed ID is in set | Missing feed ID is invalid data |
| `TagIdsAny` | Set of tag IDs | Post has at least one selected tag | Posts with no tags do not match |
| `TagIdsAll` | Set of tag IDs | Post has every selected tag | Posts with no tags do not match |
| `Bookmarked` | Boolean | Post saved state equals value | Missing value defaults to false |
| `PinnedFeed` | Boolean | Owning feed pinned state equals value | Missing feed state is invalid data |
| `PublishedRange` | Min and/or max timestamp | Published sort time is in range | Missing published time uses stored fallback |
| `UpdatedRange` | Min and/or max timestamp | Update time is in range | Missing update time is invalid data |
| `CreatedRange` | Min and/or max timestamp | Creation time is in range | Missing creation time is invalid data |
| `HasImage` | Boolean | Derived image flag equals value | Missing media fields derive to false |
| `HasAudio` | Boolean | Derived audio flag equals value | Missing media fields derive to false |
| `HasVideo` | Boolean | Derived video flag equals value | Missing media fields derive to false |
| `HasYouTubeData` | Boolean | Derived YouTube flag equals value | Missing YouTube fields derive to false |
| `HasEnclosure` | Boolean | Derived enclosure flag equals value | Missing enclosure fields derive to false |
| `Author` | Set of normalized author values | Author value is in set | Missing author does not match |
| `SourceFeed` | Set of feed IDs or source URLs | Source identity is in set | Missing source does not match |
| `Category` | Set of normalized category values | Post has at least one selected category | Posts with no categories do not match |
| `SearchText` | Text | Escaped text appears in searchable fields | Blank input is ignored |

## Supported Sorts

| Name | Term order |
|------|------------|
| `PublishedNewest` | `publishedSortAt DESC`, `updatedAt DESC`, `id DESC` |
| `PublishedOldest` | `publishedSortAt ASC`, `updatedAt ASC`, `id ASC` |
| `UpdatedNewest` | `updatedAt DESC`, `id DESC` |
| `UpdatedOldest` | `updatedAt ASC`, `id ASC` |
| `CreatedNewest` | `createdAt DESC`, `id DESC` |
| `CreatedOldest` | `createdAt ASC`, `id ASC` |
| `TitleAtoZ` | `titleSort ASC`, `id ASC` |
| `TitleZtoA` | `titleSort DESC`, `id DESC` |
| `FeedNameAtoZ` | `feedNameSort ASC`, `publishedSortAt DESC`, `id DESC` |
| `FeedNameZtoA` | `feedNameSort DESC`, `publishedSortAt DESC`, `id DESC` |
| `BookmarkedFirstLatest` | `bookmarked DESC`, `publishedSortAt DESC`, `updatedAt DESC`, `id DESC` |
| `PinnedFeedsFirstLatest` | `feedPinnedSort DESC`, `publishedSortAt DESC`, `updatedAt DESC`, `id DESC` |

## Validation Rules

- Query building fails if `sort` is not in the supported sort catalog.
- Query building fails if any filter is not in the supported filter catalog.
- Empty selected sets are explicit and return no matches for that filter.
- Absent filters do not restrict results.
- Multi-value filters bind each value independently in deterministic order.
- Search text is trimmed and escaped before matching.
- Every generated result ordering uses the exact sort term order in this contract.
- Every generated query uses bound arguments for caller-provided values.

## Compatibility Rules

- The selected result shape remains post data plus feed metadata.
- Existing feed-detail paging can continue using `FeedIds` plus `PublishedNewest`.
- New callers can add filters without adding separate DAO methods for each combination.
