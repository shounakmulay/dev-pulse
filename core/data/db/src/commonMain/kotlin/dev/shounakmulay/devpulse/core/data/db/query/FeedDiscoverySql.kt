package dev.shounakmulay.devpulse.core.data.db.query

internal object FeedDiscoverySql {
    const val IMPORT_STATUS = """
        (SELECT queue.status FROM LocalRssFeedQueue queue
         WHERE queue.url = feed.url AND queue.actionType = 'IMPORT'
         ORDER BY queue.id DESC LIMIT 1)
    """

    const val SUBSCRIBED = """
        EXISTS(SELECT 1 FROM LocalRssFeed subscribed WHERE subscribed.sourceUrl = feed.url)
    """

    const val METADATA = """
        feed.*,
        """ + SUBSCRIBED + " AS subscribed," + IMPORT_STATUS + """ AS importStatus
    """

    const val NO_HIGHLIGHTS = """
        NULL AS search_highlightedTitle,
        NULL AS search_highlightedDescription,
        NULL AS search_highlightedUrl,
        NULL AS search_highlightedWebsiteUrl
    """

    const val PROJECTION = "$METADATA,$NO_HIGHLIGHTS, 0 AS position, 0.0 AS `rank`"

    const val MEMBERSHIP = """
        EXISTS(SELECT 1 FROM LocalFeedDiscoveryCollectionToFeedMapping membership
               WHERE membership.feedId = feed.id)
    """
}
