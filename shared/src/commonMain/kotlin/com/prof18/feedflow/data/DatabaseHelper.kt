package com.prof18.feedflow.data

import co.touchlab.kermit.Logger
import com.prof18.feedflow.domain.model.FeedItem
import com.prof18.feedflow.domain.model.FeedItemId
import com.prof18.feedflow.domain.model.FeedSource
import com.prof18.feedflow.domain.model.ParsedFeedSource
import com.prof18.feedflow.db.FeedFlowDB
import com.prof18.feedflow.db.SelectFeeds
import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.TransactionWithoutReturn
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DatabaseHelper(
    sqlDriver: SqlDriver,
    private val backgroundDispatcher: CoroutineDispatcher
) {
    private val dbRef: FeedFlowDB = FeedFlowDB(sqlDriver)

    suspend fun getFeedSources(): List<FeedSource> = withContext(backgroundDispatcher) {
        dbRef.feedSourceQueries
            .selectFeedUrls()
            .executeAsList()
            .map { feedSource ->
                FeedSource(
                    id = feedSource.url_hash,
                    url = feedSource.url,
                    title = feedSource.title,
                )
            }
    }

    fun getFeedItems(): Flow<List<SelectFeeds>> =
        dbRef.feedItemQueries
            .selectFeeds()
            .asFlow()
            .mapToList()
            .catch {
                Logger.e(it) { "Something wrong while getting data from Database" }
               emit(listOf())
            }
            .flowOn(backgroundDispatcher)


    suspend fun insertCategories(categories: List<String>) =
        dbRef.transactionWithContext(backgroundDispatcher) {
            categories.forEach { category ->
                dbRef.feedSourceCategoryQueries.insertFeedSourceCategory(category)
            }
        }

    suspend fun insertFeedSource(feedSource: List<ParsedFeedSource>) {
        dbRef.transactionWithContext(backgroundDispatcher) {
            feedSource.forEach { feedSource ->
                if (feedSource.category != null) {
                    dbRef.feedSourceQueries.insertFeedSource(
                        url_hash = feedSource.hashCode(),
                        url = feedSource.url,
                        title = feedSource.title,
                        title_ = feedSource.category,
                    )
                } else {
                    dbRef.feedSourceQueries.insertFeedSourceWithNoCategory(
                        url_hash = feedSource.hashCode(),
                        url = feedSource.url,
                        title = feedSource.title,
                    )
                }
            }
        }
    }

    suspend fun insertFeedItems(feedItems: List<FeedItem>) =
        dbRef.transactionWithContext(backgroundDispatcher) {
            for (feedItem in feedItems) {
                with(feedItem) {
                    dbRef.feedItemQueries.insertFeedItem(
                        url_hash = id,
                        url = url,
                        title = title,
                        subtitle = subtitle,
                        content = content,
                        image_url = imageUrl,
                        feed_source_id = feedSource.id,
                        pub_date = pubDateMillis,
                        comments_url = commentsUrl,
                    )
                }
            }
        }

    suspend fun updateReadStatus(itemsToUpdates: List<FeedItemId>) =
        dbRef.transactionWithContext(backgroundDispatcher) {
            for (item in itemsToUpdates) {
                Logger.d {
                    "Updating read status for: ${item.id}"
                }
                dbRef.feedItemQueries.updateReadStatus(item.id)
            }
        }

    suspend fun updateNewStatus() =
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.feedItemQueries.updateNewStatus()
            Logger.d { "Update new status" }
        }

    suspend fun markAllFeedAsRead() =
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.feedItemQueries.markAllRead()
        }

    suspend fun deleteOldFeedItems(timeThreshold: Long) =
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.feedItemQueries.clearOldItems(timeThreshold)
        }

    private suspend fun Transacter.transactionWithContext(
        coroutineContext: CoroutineContext,
        noEnclosing: Boolean = false,
        body: TransactionWithoutReturn.() -> Unit
    ) {
        withContext(coroutineContext) {
            this@transactionWithContext.transaction(noEnclosing) {
                body()
            }
        }
    }

    internal companion object {
        const val DB_FILE_NAME_WITH_EXTENSION = "FeedFlow.db"
        const val DB_FILE_NAME = "FeedFlow"
        const val DATABASE_NAME = "FeedFlowDB"
    }
}