package dev.shounakmulay.devpulse.core.data.feed.hook

internal fun interface CoreItemHook<R> {
    suspend fun process(post: R): R
}

internal fun interface CoreBatchHook<R> {
    suspend fun process(posts: List<R>): List<R>
}
