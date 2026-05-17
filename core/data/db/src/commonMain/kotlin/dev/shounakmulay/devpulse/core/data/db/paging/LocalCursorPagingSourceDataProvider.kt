package dev.shounakmulay.devpulse.core.data.db.paging

interface LocalCursorPagingSourceDataProvider<KEY, VALUE> {
    fun getTablesToTrack(): List<String>
    suspend fun getInitialPage(loadSize: Int): List<VALUE>
    suspend fun getPageAfter(cursor: KEY, loadSize: Int): List<VALUE>
    suspend fun getRefreshPageAround(anchorCursor: KEY, loadSize: Int): List<VALUE>

    fun getId(item: VALUE): KEY
}
