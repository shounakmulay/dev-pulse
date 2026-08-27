package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostCategory

interface PostCategoryRepository {
    suspend fun deletePostCategories(postIds: Set<UUID>)
    suspend fun upsertPostCategories(categories: List<RssPostCategory>)
}