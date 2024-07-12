package com.example.news.main.models

sealed class State(open val articles: List<ArticleUI>?) {

    data object None : State(articles = null)

    class Loading(articles: List<ArticleUI>? = null) : State(articles)

    class Error(articles: List<ArticleUI>? = null) : State(articles)

    class Success(override val articles: List<ArticleUI>) : State(articles)
}
