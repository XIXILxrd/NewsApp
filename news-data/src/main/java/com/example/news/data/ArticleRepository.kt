package com.example.news.data

import com.example.news.common.Logger
import com.example.news.data.models.Article
import com.example.news.database.NewsDatabase
import com.example.news.database.models.ArticleDBO
import com.example.newsapi.NewsApi
import com.example.newsapi.models.ArticleDTO
import com.example.newsapi.models.ResponseDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val database: NewsDatabase,
    private val api: NewsApi,
    private val logger: Logger,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAll(
        query: String,
        mergeStrategy:
        MergeStrategy<RequestResult<List<Article>>> = RequestResponseMergeStrategy()
    ): Flow<RequestResult<List<Article>>> {
        val cachedAllArticles: Flow<RequestResult<List<Article>>> =
            getAllFromDatabase()
        val remoteArticles: Flow<RequestResult<List<Article>>> =
            getAllFromServer(query)

        return cachedAllArticles.combine(remoteArticles, mergeStrategy::merge)
            .flatMapLatest { result ->
                if (result is RequestResult.Success) {
                    database.articleDao.observeAll()
                        .map { databaseObjects -> databaseObjects.map { it.toArticle() } }
                        .map { RequestResult.Success(it) }
                } else {
                    flowOf(result)
                }
            }
    }

    private fun getAllFromDatabase(): Flow<RequestResult<List<Article>>> {
        val databaseRequest = database.articleDao::getAll.asFlow()
            .map<List<ArticleDBO>, RequestResult<List<ArticleDBO>>> {
                RequestResult.Success(
                    it
                )
            }
            .catch {
                emit(RequestResult.Error(error = it))
                logger.e(
                    LOG_TAG,
                    "Error getting from database: $it"
                )
            }
        val start = flowOf<RequestResult<List<ArticleDBO>>>(
            RequestResult.Loading()
        )

        return merge(start, databaseRequest)
            .map { request ->
                request.map { result ->
                    result.map { it.toArticle() }
                }
            }
    }

    private fun getAllFromServer(query: String): Flow<RequestResult<List<Article>>> {
        val serverRequest = flow { emit(api.everything(query)) }
            .onEach { request ->
                if (request.isSuccess) {
                    saveRemoteArticlesToCache(checkNotNull(request.getOrThrow().articles))
                }
            }
            .onEach { result ->
                if (result.isFailure) {
                    logger.e(
                        LOG_TAG,
                        "Error getting from server: ${result.exceptionOrNull()}"
                    )
                }
            }
            .map { it.toRequestResult() }

        val start = flowOf<RequestResult<ResponseDTO<ArticleDTO>>>(
            RequestResult.Loading()
        )

        return merge(start, serverRequest)
            .map { requestResult ->
                requestResult.map { response ->
                    response.articles.map { it.toArticle() }
                }
            }
    }

    private suspend fun saveRemoteArticlesToCache(data: List<ArticleDTO>) {
        val databaseObjects = data.map { it.toArticleDbo() }
        database.articleDao.insert(databaseObjects)
    }

    companion object {
        private const val LOG_TAG = "ArticleRepository"
    }
}
