package dev.shounakmulay.devpulse.core.data.db.query

enum class FeedPostPerformanceClassification {
    CommonOptimized,
    RareSupported
}

enum class FeedPostQueryCoverageProfile(
    val classification: FeedPostPerformanceClassification,
    val expectedIndexPlan: String? = null
) {
    TimelineLatest(
        classification = FeedPostPerformanceClassification.CommonOptimized,
        expectedIndexPlan = "Post index on publishedSortAt, updatedAt, id"
    ),
    FeedTimelineLatest(
        classification = FeedPostPerformanceClassification.CommonOptimized,
        expectedIndexPlan = "Post index on feedId, publishedSortAt, updatedAt, id"
    ),
    BookmarkedTimelineLatest(
        classification = FeedPostPerformanceClassification.CommonOptimized,
        expectedIndexPlan = "Post index on bookmarked, publishedSortAt, updatedAt, id"
    ),
    FeedBookmarkedTimelineLatest(
        classification = FeedPostPerformanceClassification.CommonOptimized,
        expectedIndexPlan = "Post index on feedId, bookmarked, publishedSortAt, updatedAt, id"
    ),
    PinnedFeedsTimelineLatest(
        classification = FeedPostPerformanceClassification.CommonOptimized,
        expectedIndexPlan = "Post index on feedPinnedSort, publishedSortAt, updatedAt, id"
    ),
    UpdatedLatest(
        classification = FeedPostPerformanceClassification.CommonOptimized,
        expectedIndexPlan = "Post index on updatedAt, id"
    ),
    CreatedLatest(
        classification = FeedPostPerformanceClassification.CommonOptimized,
        expectedIndexPlan = "Post index on createdAt, id"
    ),
    TitleAlphabetical(
        classification = FeedPostPerformanceClassification.CommonOptimized,
        expectedIndexPlan = "Post index on titleSort, id"
    ),
    FeedNameAlphabetical(
        classification = FeedPostPerformanceClassification.CommonOptimized,
        expectedIndexPlan = "Post index on feedNameSort, publishedSortAt, id"
    ),
    RareCombination(
        classification = FeedPostPerformanceClassification.RareSupported
    );

    companion object {
        fun classify(query: FeedPostQuery): FeedPostQueryCoverageProfile {
            val filters = query.filters
            val sort = query.sort

            val isFeedOnly = filters.size == 1 && filters.single() is FeedPostFilter.FeedIds
            val isBookmarkedOnly = filters.size == 1 && filters.single() is FeedPostFilter.Bookmarked
            val isFeedAndBookmarked = filters.size == 2 && 
                filters.any { it is FeedPostFilter.FeedIds } && 
                filters.any { it is FeedPostFilter.Bookmarked }
            val isPinnedFeedOnly = filters.size == 1 && filters.single() is FeedPostFilter.PinnedFeed

            return when {
                sort == FeedPostSort.PublishedNewest && filters.isEmpty() -> TimelineLatest
                sort == FeedPostSort.PublishedNewest && isFeedOnly -> FeedTimelineLatest
                sort == FeedPostSort.PublishedNewest && isBookmarkedOnly -> BookmarkedTimelineLatest
                sort == FeedPostSort.PublishedNewest && isFeedAndBookmarked -> FeedBookmarkedTimelineLatest
                sort == FeedPostSort.PinnedFeedsFirstLatest && (filters.isEmpty() || isPinnedFeedOnly) -> PinnedFeedsTimelineLatest
                sort == FeedPostSort.UpdatedNewest && (filters.isEmpty() || isFeedOnly) -> UpdatedLatest
                sort == FeedPostSort.CreatedNewest && filters.isEmpty() -> CreatedLatest
                sort == FeedPostSort.TitleAtoZ && filters.isEmpty() -> TitleAlphabetical
                sort == FeedPostSort.FeedNameAtoZ && filters.isEmpty() -> FeedNameAlphabetical
                else -> RareCombination
            }
        }
    }
}
