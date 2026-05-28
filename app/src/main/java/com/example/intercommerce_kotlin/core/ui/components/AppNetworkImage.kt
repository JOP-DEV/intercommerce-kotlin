package com.example.intercommerce_kotlin.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.intercommerce_kotlin.R

@Composable
fun AppNetworkImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    showMessage: Boolean = true
) {
    if (model == null || (model is String && model.isBlank())) {
        PlaceholderState(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = Color(0xFF8B909B)
                )
            },
            text = stringResource(id = R.string.image_unavailable),
            showMessage = showMessage
        )
        return
    }

    SubcomposeAsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        loading = {},
        error = {
            PlaceholderState(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.BrokenImage,
                        contentDescription = null,
                        tint = Color(0xFF8B909B)
                    )
                },
                text = stringResource(id = R.string.image_error),
                showMessage = showMessage
            )
        },
        success = { SubcomposeAsyncImageContent() }
    )
}

@Composable
private fun PlaceholderState(
    icon: @Composable () -> Unit,
    text: String,
    showMessage: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F3F7)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon()
            if (showMessage) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF585F6B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 10.dp, end = 10.dp)
                )
            }
        }
    }
}
