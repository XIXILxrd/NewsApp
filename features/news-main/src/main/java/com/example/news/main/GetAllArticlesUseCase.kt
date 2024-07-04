package com.example.news.main

import com.example.news.data.ArticleRepository
import com.example.news.data.map
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllArticlesUseCase @Inject constructor(
    private val repository: ArticleRepository,
) {
    operator fun invoke() = repository.getAll()
        .map { requestResult -> requestResult.map { articles -> articles.map { it.toArticleUI() } } }
}