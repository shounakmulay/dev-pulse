# Research: Feed Query Engine

## Decision: Use a typed query spec as the only source for dynamic post retrieval

Generate feed post retrieval from a whitelisted query spec containing named filters, one named sort, an optional cursor, and a page size. Callers do not provide SQL fragments or column expressions.

**Rationale**: The current implementation already builds dynamic SQL, but the supported combinations are spread across filter branches and per-sort cursor functions. A typed query spec keeps the feature extensible while making unsupported combinations fail before execution. It also gives tests a single contract to validate.

**Alternatives considered**:

- Static query per combination: rejected because the named filter/sort catalog would create too many variants.
- Free-form SQL input: rejected because callers could bypass ordering, cursor, and index assumptions.
- Keep the current filter model and add branches incrementally: rejected because the spec requires named common/rare coverage and generic cursor compatibility across all sorts.

## Decision: Keep RoomRawQuery for dynamic reads on KMP targets

Use Room's runtime raw-query path for dynamic read queries and keep the DAO return projection unchanged.

**Rationale**: Android's Room KMP documentation states that non-Android raw-query DAO functions should use `RoomRawQuery` instead of `SupportSQLiteQuery`, and the API supports placeholders and argument binding. `@RawQuery` also supports returning mapped objects and observed entities for invalidation. This matches the current KMP checkout and keeps runtime query generation inside Room's object mapping path.

**Alternatives considered**:

- Static `@Query` methods with every filter combination: rejected due combination count.
- Android-only `SupportSQLiteQuery`: rejected because this is a KMP module.
- Bypassing Room for custom SQLite access: rejected because it would duplicate mapping and invalidation work already wired in the DB module.

References:

- https://developer.android.com/kotlin/multiplatform/room
- https://developer.android.com/reference/androidx/room/RawQuery
- https://developer.android.com/reference/kotlin/androidx/room/RoomRawQuery

## Decision: Replace expression-based sort fallbacks with stored sort keys

Add stored values for deterministic, indexable ordering:

- `publishedSortAt`: non-null published sort timestamp derived from published timestamp or the documented missing-value fallback.
- `titleSort`: non-null normalized title sort key.
- `feedNameSort`: non-null normalized feed display name copied onto posts for global feed-name ordering.
- `feedPinnedSort`: copied feed pinned state for pinned-first post ordering.

**Rationale**: The current builder sorts with fallback expressions for published time, post title, and feed name. Those expressions make the query harder to index predictably. Stored sort keys let the common coverage matrix use composite indexes and keep keyset cursors simple.

**Alternatives considered**:

- Keep fallback expressions in `ORDER BY`: rejected for common sorted paths because it weakens index usage.
- Sort in memory after loading rows: rejected because it breaks large-list paging and stable cursor guarantees.
- Store sort keys only on feed and join for feed-name/pinned sorts: rejected for global post ordering because indexes cannot span columns across joined tables.

## Decision: Generate cursor predicates from sort terms

Represent each sort as ordered terms, each with a value expression, direction, and cursor value extractor. Build keyset predicates generically from those terms.

**Rationale**: The current builder has separate cursor code for published, long, and text sorts. The new catalog includes mixed sorts like bookmarked-first and pinned-first. A generic term-based cursor builder keeps every sort stable and testable while making it difficult to forget the unique identity tie-breaker.

**Alternatives considered**:

- One cursor function per sort: rejected because it scales poorly and invites inconsistent tie-break behavior.
- Offset paging: rejected by the spec and because SQLite documentation notes that offset work grows as the skipped row count grows.
- Cursor only by post id: rejected because it does not preserve ordering for non-id sorts.

References:

- https://www.sqlite.org/rowvalue.html

## Decision: Normalize category filtering into a post-category relationship

Introduce a post category value entity instead of filtering raw serialized category text.

**Rationale**: The spec requires category filtering to match values, not accidental substrings. The current post entity stores categories as text, which is not reliable for exact matching or indexing. A post-category relationship makes `Category` a real filter and gives category filtering its own index without bloating the main post row.

**Alternatives considered**:

- Continue substring matching on the existing categories field: rejected because it fails the spec's false-positive edge case.
- Store an escaped delimiter format and search delimiters: rejected because it is fragile and less queryable.
- Put a fixed number of category columns on posts: rejected because category counts vary.

## Decision: Treat search as supported but rare in the first performance matrix

Support `SearchText` with escaped, case-normalized matching across searchable fields, but classify it as rare unless product usage later promotes it to a common path.

**Rationale**: The spec includes search, but the common coverage matrix does not require search to be indexed. A correct escaped search path satisfies the named filter catalog without adding a full search index before the UI proves it is a common workflow.

**Alternatives considered**:

- Full text search in the first implementation: deferred because it adds schema and maintenance cost that is not required by the current common matrix.
- Excluding search: rejected because the feature spec explicitly includes `SearchText`.
- Unescaped wildcard matching: rejected because punctuation and wildcard-like input must not broaden queries unexpectedly.

## Decision: Add derived media booleans for filter correctness, not broad composite media indexes

Add derived boolean values for image, audio, video, YouTube data, and enclosure presence. Do not add every media-plus-sort composite index initially.

**Rationale**: The current audio/video filters inspect multiple nullable fields with null-safe clauses. Derived booleans make positive and negative filters deterministic and easier to test. Media filters are not part of the initial common coverage matrix, so broad composite indexes would spend storage on rare combinations.

**Alternatives considered**:

- Keep media detection only in query predicates: rejected because it repeats complex logic and risks nullable edge cases.
- Add composite indexes for every media filter plus every sort: rejected due storage and write overhead.
- Collapse media into one `HasMedia` filter: rejected because the spec and previous product direction separate image, audio, video, YouTube data, and enclosure.

## Decision: Index common profiles, document rare profiles

Use a named performance coverage profile list where common combinations have composite index coverage and rare combinations remain correctness-tested with documented expected cost.

**Rationale**: SQLite's planner documentation emphasizes that multi-column indexes can satisfy both filtering and ordering when the left-most indexed columns match equality filters and subsequent columns match sort order. It also notes that longer multi-column indexes can make shorter prefix indexes redundant. A named matrix lets the project add the right indexes without indexing every possible filter combination.

**Alternatives considered**:

- Index every filter/sort combination: rejected due storage and write overhead.
- Keep only single-column indexes: rejected because common combined filter/sort paths would sort too much data.
- Depend only on query planner heuristics without a coverage matrix: rejected because the spec requires common and rare classifications to be reviewable.

References:

- https://www.sqlite.org/queryplanner.html
