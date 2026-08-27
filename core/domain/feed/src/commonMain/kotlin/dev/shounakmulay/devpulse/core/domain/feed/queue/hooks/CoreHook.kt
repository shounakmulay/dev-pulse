package dev.shounakmulay.devpulse.core.data.feed.hook

fun interface CoreItemHook<R> {
    suspend fun process(post: R): R
}

fun interface CoreBatchHook<R> {
    suspend fun process(posts: List<R>): List<R>
}
