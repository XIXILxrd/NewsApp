package com.example.news.main.models

sealed class State {
    object None : State()
    class Error(val articles: List<ArticleUI>? = null) : State()
    class Loading(val articles: List<ArticleUI>?) : State()
    class Success(val articles: List<ArticleUI>) : State()
}

