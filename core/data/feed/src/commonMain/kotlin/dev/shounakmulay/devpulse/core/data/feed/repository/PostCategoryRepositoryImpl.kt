package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostCategoryDao
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.UuidMapper
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostCategory
import org.koin.core.annotation.Factory

@Factory
class PostCategoryRepositoryImpl(
    private val postCategoryDao: FeedPostCategoryDao,
    private val postMapper: RssPostMapper,
    private val uuidMapper: UuidMapper,
) : PostCategoryRepository {
    override suspend fun deletePostCategories(postIds: Set<UUID>) {
        postCategoryDao.deletePostCategories(postIds.map(uuidMapper::fromUuid).toSet())
    }

    override suspend fun upsertPostCategories(categories: List<RssPostCategory>) {
        postCategoryDao.upsertPostCategories(categories.map(postMapper::toLocalRssPostCategory))
    }
}