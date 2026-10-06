package dev.shounakmulay.devpulse.core.data.db.triggers

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

internal fun createPostContentFtsDeleteTrigger(connection: SQLiteConnection) {
    connection.execSQL(
        """
            CREATE TRIGGER IF NOT EXISTS LocalRssContentFeedPost_delete_content_fts
            AFTER DELETE ON LocalRssContentFeedPost
            BEGIN
                DELETE FROM LocalRssPostContentFts
                WHERE postId = OLD.id;
            END
            """.trimIndent()
    )
}
