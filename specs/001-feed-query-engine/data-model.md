# Data Model: Feed Query Engine

## Feed Post

Represents a saved feed item that can appear in timeline, feed-detail, bookmarked, media, category, tag, search, and sorted lists.

Fields used by this feature:

- `id`: Unique post identity and final sort tie-breaker.
- `feedId`: Owning feed identity.
- `title`: Display title.
- `titleSort`: Non-null normalized title sort key.
- `author`: Display author value.
- `authorSort`: Normalized author filter value.
- `publishedAtEpochMillis`: Original parsed published timestamp, nullable.
- `publishedSortAt`: Non-null published timestamp used for sorting and cursor values.
- `createdAt`: Local creation timestamp.
- `updatedAt`: Local update/import timestamp.
- `bookmarked`: Saved state.
- `image`, `audio`, `video`, `youtubeData`, `rawEnclosure`, `rawMedia`: Source media metadata.
- `hasImage`, `hasAudio`, `hasVideo`, `hasYouTubeData`, `hasEnclosure`: Derived media presence flags.
- `sourceName`, `sourceUrl`: Source metadata copied from parsed content where present.
- `sourceNameSort`: Normalized source-name filter value.
- `feedNameSort`: Normalized feed display name copied from the owning feed for global post ordering.
- `feedPinnedSort`: Copied feed pinned state for pinned-first post ordering.

Relationships:

- Belongs to one Feed through `feedId`.
- Has zero or more Post Category Values.
- Has zero or more tags through the existing post-tag mapping.

Validation rules:

- `publishedSortAt` must equal the parsed published timestamp when present, otherwise the documented missing-value fallback.
- Sort keys must be deterministic for null, blank, mixed-case, and whitespace-heavy source values.
- Derived media flags must be recomputed whenever the underlying post media fields are inserted or updated.
- Copied feed sort values must be updated when feed display name or pinned state changes.

## Feed

Represents a feed source that owns posts.

Fields used by this feature:

- `id`: Unique feed identity.
- `title`: Source title.
- `name`: User-visible feed name override.
- `displayNameSort`: Non-null normalized feed display name.
- `sourceUrl`: Unique source URL.
- `pinned`: Feed pin state.
- `createdAt`, `updatedAt`: Local timestamps.

Relationships:

- Owns many Feed Posts.

Validation rules:

- Display name sort uses the same normalization rules as `feedNameSort` on posts.
- Changes to display name or pin state must preserve post query ordering by updating copied post values before affected lists are considered current.

## Post Category Value

Represents one exact category assigned to one post.

Fields:

- `postId`: Owning post identity.
- `category`: Display category value.
- `categorySort`: Normalized category value for matching and ordering.

Relationships:

- Belongs to one Feed Post.

Validation rules:

- Duplicate category values for the same post collapse to one value.
- Blank category values are ignored.
- Matching is by normalized category value, not substring search over serialized text.

## Post Query Spec

Represents one request for feed posts.

Fields:

- `filters`: Set of named filter options.
- `sort`: One named sort option.
- `cursor`: Optional cursor derived from the same sort.
- `pageSize`: Requested number of rows.
- `coverageProfile`: Common or rare performance classification.

Validation rules:

- Unsupported filter and sort names are rejected before retrieval.
- Cursor sort identity must match the active sort.
- Cursor value count and value types must match the active sort terms.
- Page size must be positive and capped by the paging layer's configured maximum.
- Empty multi-value filters must be represented explicitly and must not be confused with absent filters.

State transitions:

- `Draft`: Created by caller.
- `Validated`: Names, values, cursor shape, and page size pass checks.
- `Built`: Converted to a bound query.
- `Executed`: Used to load initial, append, or refresh page.
- `Rejected`: Invalid name, value, cursor, or page size.

## Filter Option

Represents one named restriction from the supported catalog.

Supported filters:

- `FeedIds`: Multi-value feed identity match.
- `TagIdsAny`: Match posts with at least one selected tag.
- `TagIdsAll`: Match posts with every selected tag.
- `Bookmarked`: Boolean saved-state match.
- `PinnedFeed`: Boolean owning-feed pin match.
- `PublishedRange`: Minimum and/or maximum published sort time.
- `UpdatedRange`: Minimum and/or maximum update time.
- `CreatedRange`: Minimum and/or maximum creation time.
- `HasImage`: Boolean image-presence match.
- `HasAudio`: Boolean audio-presence match.
- `HasVideo`: Boolean video-presence match.
- `HasYouTubeData`: Boolean YouTube-presence match.
- `HasEnclosure`: Boolean enclosure-presence match.
- `Author`: Multi-value normalized author match.
- `SourceFeed`: Multi-value source feed identity or source URL match.
- `Category`: Multi-value normalized category match.
- `SearchText`: Escaped text search across supported searchable fields.

Validation rules:

- Multi-value filters de-duplicate values before binding.
- Boolean filters use three states: absent, true, false.
- Blank text values are ignored unless the caller explicitly supplies an empty selected set.
- Search text is trimmed and escaped before matching.

## Sort Option

Represents one named stable ordering mode.

Supported sorts:

- `PublishedNewest`: `publishedSortAt` descending, `updatedAt` descending, `id` descending.
- `PublishedOldest`: `publishedSortAt` ascending, `updatedAt` ascending, `id` ascending.
- `UpdatedNewest`: `updatedAt` descending, `id` descending.
- `UpdatedOldest`: `updatedAt` ascending, `id` ascending.
- `CreatedNewest`: `createdAt` descending, `id` descending.
- `CreatedOldest`: `createdAt` ascending, `id` ascending.
- `TitleAtoZ`: `titleSort` ascending, `id` ascending.
- `TitleZtoA`: `titleSort` descending, `id` descending.
- `FeedNameAtoZ`: `feedNameSort` ascending, `publishedSortAt` descending, `id` descending.
- `FeedNameZtoA`: `feedNameSort` descending, `publishedSortAt` descending, `id` descending.
- `BookmarkedFirstLatest`: `bookmarked` descending, `publishedSortAt` descending, `updatedAt` descending, `id` descending.
- `PinnedFeedsFirstLatest`: `feedPinnedSort` descending, `publishedSortAt` descending, `updatedAt` descending, `id` descending.

Validation rules:

- Every sort ends with `id`.
- Every sort defines the same term order for page load, cursor extraction, and refresh.
- Missing values use stored sort key defaults, never ad hoc per-query fallback expressions.

## Cursor

Represents the position for next-page or refresh-around loading.

Fields:

- `sort`: Sort identity used to create the cursor.
- `values`: Ordered values matching the active sort terms.
- `postId`: Unique post identity tie-breaker.

Validation rules:

- Cursor cannot be reused after filters or sort change.
- Cursor values must match the active sort term count and value types.
- Cursor for append is exclusive.
- Cursor for refresh-around may include the anchor item.

## Performance Coverage Profile

Represents the expected performance class for a query combination.

Fields:

- `name`: Stable coverage name.
- `filters`: Filter pattern covered by the profile.
- `sort`: Sort covered by the profile.
- `classification`: Common optimized or rare supported.
- `indexPlan`: Expected index support for common profiles.
- `verification`: Required tests or explain-plan checks.

Validation rules:

- Every common profile must have explicit index support and verification.
- Rare profiles must document why no dedicated composite index is required.
- Adding a new filter or sort requires assigning affected combinations to common or rare coverage.
