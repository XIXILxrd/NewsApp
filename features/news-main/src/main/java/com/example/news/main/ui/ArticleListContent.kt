package com.example.news.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.news.main.models.ArticleUI
import com.example.news.main.models.State
import com.example.news.uikit.NewsTheme

@Composable
internal fun ArticleList(
    articleState: State.Success,
    modifier: Modifier = Modifier
) {
    ArticleList(articles = articleState.articles, modifier)
}

@Preview
@Composable
internal fun ArticleList(
    @PreviewParameter(ArticlesPreviewProvider::class, limit = 1) articles: List<ArticleUI>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(articles) { article ->
            key(article.id) {
                ArticleItem(article)
            }
        }
    }
}

@Preview
@Composable
internal fun ArticleItem(
    @PreviewParameter(ArticlePreviewProvider::class, limit = 1) article: ArticleUI,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(bottom = 4.dp)) {
        article.imageUrl?.let { imageUrl ->
            var isImageVisible by remember { mutableStateOf(true) }

            if (isImageVisible) {
                AsyncImage(
                    modifier = Modifier.size(150.dp),
                    model = imageUrl,
                    onState = { state ->
                        if (state is AsyncImagePainter.State.Error) {
                            isImageVisible = false
                        }
                    },
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.size(4.dp))
        Column(modifier = Modifier.padding(8.dp)) {
            if (article.title != null) {
                Text(
                    text = article.title,
                    style = NewsTheme.typography.headlineMedium,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            if (article.description != null) {
                Text(
                    text = article.description,
                    style = NewsTheme.typography.bodyMedium,
                    maxLines = 3
                )
            }
        }
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
