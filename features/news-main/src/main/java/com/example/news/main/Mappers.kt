package com.example.news.main

import com.example.news.data.RequestResult
import com.example.news.data.models.Article
import com.example.news.main.models.ArticleUI
import com.example.news.main.models.State

internal fun RequestResult<List<ArticleUI>>.toState(): State {
    return when (this) {
        is RequestResult.Error -> State.Error()
        is RequestResult.Loading -> State.Loading(data)
        is RequestResult.Success -> State.Success(data)
    }
}

internal fun Article.toArticleUI(): ArticleUI = ArticleUI(
    id = cacheId,
    title = title,
    description = description,
    imageUrl = urlToImage,
    url = url,
)
