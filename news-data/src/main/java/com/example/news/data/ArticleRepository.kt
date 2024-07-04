package com.example.news.data

import com.example.news.data.models.Article
import com.example.news.database.NewsDatabase
import com.example.news.database.models.ArticleDBO
import com.example.newsapi.NewsApi
import com.example.newsapi.models.ArticleDTO
import com.example.newsapi.models.ResponseDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
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
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAll(
        mergeStrategy: MergeStrategy<RequestResult<List<Article>>> = RequestResponseMergeStrategy(),
    ): Flow<RequestResult<List<Article>>> {
        val cachedArticles = getAllFromDatabase()
        val remoteArticles = getAllFromServer()

        return cachedArticles
            .combine(remoteArticles, mergeStrategy::merge)
            .flatMapLatest { result ->
                if (result is RequestResult.Success) {
                    database.articleDao.observeAll()
                        .map { dbos -> dbos.map { it.toArticle() } }
                        .map { RequestResult.Success(it) }
                } else {
                    flowOf(result)
                }

            }
    }

    private fun getAllFromDatabase(): Flow<RequestResult<List<Article>>> {
        val databaseRequest = database.articleDao::getAll.asFlow()
            .map { RequestResult.Success(it) }
        val start = flowOf<RequestResult<List<ArticleDBO>>>(RequestResult.Loading())

        return merge(start, databaseRequest)
            .map { request ->
                request.map { result ->
                    result.map { it.toArticle() }
                }
            }
    }


    private fun getAllFromServer(): Flow<RequestResult<List<Article>>> {
        val serverRequest = flow { emit(api.everything()) }
            .onEach { request ->
                if (request.isSuccess) {
                    saveRemoteArticlesToCache(checkNotNull(request.getOrThrow().articles))
                }
            }
            .map { it.toRequestResult() }

        val start = flowOf<RequestResult<ResponseDTO<ArticleDTO>>>(RequestResult.Loading())

        return merge(start, serverRequest)
            .map { requestResult ->
                requestResult.map { response ->
                    response.articles.map { it.toArticle() }
                }
            }
    }

    private suspend fun saveRemoteArticlesToCache(data: List<ArticleDTO>) {
        val dbos = data.map { it.toArticleDbo() }
        database.articleDao.insert(dbos)
    }
}