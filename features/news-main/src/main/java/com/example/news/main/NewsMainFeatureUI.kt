package com.example.news.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.news.main.models.ArticleUI
import com.example.news.main.models.State

@Composable
fun NewsMainScreen() {
    NewsMain(mainViewModel = viewModel())
}

@Composable
internal fun NewsMain(mainViewModel: NewsMainViewModel = viewModel()) {
    val state by mainViewModel.state.collectAsState()

    when (val currentState = state) {
        is State.Error -> TODO()
        is State.Loading -> TODO()
        is State.Success -> ArticlesColumn(articles = currentState.articles)
        State.None -> TODO()
    }
}

@Preview
@Composable
private fun ArticlesColumn(
    modifier: Modifier = Modifier,
    @PreviewParameter(ArticlePreviewProvider::class) articles: List<ArticleUI>,
) {
    LazyColumn {
        items(articles) { article ->
            key(article.id) {
                ArticleItem(article = article)
            }
        }
    }
}

@Preview
@Composable
private fun ArticleItem(
    modifier: Modifier = Modifier,
    @PreviewParameter(ArticlePreviewProvider::class) article: ArticleUI
) {
    Column {
        Text(
            text = article.title ?: "",
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1
        )
        Text(
            text = article.description ?: "",
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3
        )
    }
}

class ArticlePreviewProvider : PreviewParameterProvider<ArticleUI> {
    override val values = sequenceOf(
        ArticleUI(1, "Pixel 8", "Google Pixel 8", null, null),
        ArticleUI(1, "Pixel 9", "Google Pixel 9", null, null),
        ArticleUI(1, "Telegram", "Telegram messenger", null, null),
        ArticleUI(1, "Youtube", "Video-hosting", null, null)
    )

}

class ArticlesPreviewProvider : PreviewParameterProvider<List<ArticleUI>> {
    private val articles = ArticlePreviewProvider()

    override val values = sequenceOf(articles.values.toList())
}