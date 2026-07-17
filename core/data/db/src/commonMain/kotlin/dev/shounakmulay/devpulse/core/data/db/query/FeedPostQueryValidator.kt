package dev.shounakmulay.devpulse.core.data.db.query

object FeedPostQueryValidator {

    fun validate(query: FeedPostQuery): FeedPostQuery {
        validateUniqueFilterTypes(query.filters)
        validateExplicitEmptyFilters(query.filters)
        validateCursor(query)
        val profile = FeedPostQueryCoverageProfile.classify(query)
        return query.copy(coverageProfile = profile)
    }

    fun validateLimit(limit: Int) {
        require(limit in FeedPostPageSize.Min..FeedPostPageSize.Max) {
            "Feed post page limit must be between ${FeedPostPageSize.Min} and ${FeedPostPageSize.Max}. limit=$limit"
        }
    }

    private fun validateUniqueFilterTypes(filters: Set<FeedPostFilter>) {
        val filterTypeNames = filters.map { it::class.simpleName.orEmpty() }
        require(filterTypeNames.size == filterTypeNames.toSet().size) {
            "Feed post query cannot contain duplicate filter types"
        }
    }

    private fun validateExplicitEmptyFilters(filters: Set<FeedPostFilter>) {
        filters.forEach { filter ->
            when (filter) {
                is FeedPostFilter.FeedIds -> require(filter.values.isNotEmpty()) {
                    "FeedIds filter cannot be explicitly empty"
                }
                is FeedPostFilter.TagIdsAny -> require(filter.values.isNotEmpty()) {
                    "TagIdsAny filter cannot be explicitly empty"
                }
                is FeedPostFilter.TagIdsAll -> require(filter.values.isNotEmpty()) {
                    "TagIdsAll filter cannot be explicitly empty"
                }
                is FeedPostFilter.Author -> require(filter.values.isNotEmpty()) {
                    "Author filter cannot be explicitly empty"
                }
                is FeedPostFilter.SourceFeed -> require(filter.values.isNotEmpty()) {
                    "SourceFeed filter cannot be explicitly empty"
                }
                is FeedPostFilter.Category -> require(filter.values.isNotEmpty()) {
                    "Category filter cannot be explicitly empty"
                }
                is FeedPostFilter.Bookmarked,
                is FeedPostFilter.PinnedFeed,
                is FeedPostFilter.PublishedRange,
                is FeedPostFilter.UpdatedRange,
                is FeedPostFilter.CreatedRange,
                is FeedPostFilter.HasImage,
                is FeedPostFilter.HasAudio,
                is FeedPostFilter.HasVideo,
                is FeedPostFilter.HasYouTubeData,
                is FeedPostFilter.HasEnclosure,
                is FeedPostFilter.SearchText -> Unit
            }
        }
    }

    private fun validateCursor(query: FeedPostQuery) {
        val cursor = query.cursor ?: return
        require(cursor.sort == query.sort) {
            "Feed post cursor sort must match query sort. cursor=${cursor.sort} query=${query.sort}"
        }

        val terms = query.sort.spec.terms
        require(cursor.values.size == terms.size) {
            "Feed post cursor value count must match sort term count. values=${cursor.values.size} terms=${terms.size}"
        }

        cursor.values.zip(terms).forEachIndexed { index, (cursorValue, term) ->
            require(cursorValue.type == term.cursorValueType) {
                "Feed post cursor value type mismatch at index=$index. value=${cursorValue.type} term=${term.cursorValueType}"
            }
        }
    }
}
