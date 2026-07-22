package dev.shounakmulay.devpulse.core.data.db.query

enum class LocalFeedPostSort {
    PublishedNewest,
    PublishedOldest,
    TitleAtoZ,
    TitleZtoA;

    fun reversed(): LocalFeedPostSort {
        return when (this) {
            PublishedNewest -> PublishedOldest
            PublishedOldest -> PublishedNewest
            TitleAtoZ -> TitleZtoA
            TitleZtoA -> TitleAtoZ
        }
    }
}