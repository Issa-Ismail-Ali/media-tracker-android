package edu.metrostate.ics342.mediatracker.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.metrostate.ics342.mediatracker.data.model.MediaDetail

@Composable
fun MediaDetailScreen(
    mediaId: Int,
    onNavigateBack: () -> Unit,
    onWriteReview: (Int) -> Unit,
    viewModel: MediaDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mediaId) {
        viewModel.loadMedia(mediaId)
    }

    when (val state = uiState) {
        MediaDetailUiState.Loading -> {
            LoadingContent()
        }

        is MediaDetailUiState.Error -> {
            ErrorContent(
                message = state.message,
                onRetry = viewModel::retry,
                onNavigateBack = onNavigateBack
            )
        }

        is MediaDetailUiState.Success -> {
            MediaDetailSuccessContent(
                media = state.media,
                isInLibrary = state.isInLibrary,
                isAddingToLibrary = state.isAddingToLibrary,
                onNavigateBack = onNavigateBack,
                onAddToLibrary = viewModel::addToLibrary,
                onWriteReview = {
                    onWriteReview(mediaId)
                }
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry
        ) {
            Text(text = "Retry")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateBack
        ) {
            Text(text = "Back")
        }
    }
}

@Composable
private fun MediaDetailSuccessContent(
    media: MediaDetail,
    isInLibrary: Boolean,
    isAddingToLibrary: Boolean,
    onNavigateBack: () -> Unit,
    onAddToLibrary: () -> Unit,
    onWriteReview: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = media.title)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAddToLibrary,
            enabled = !isInLibrary && !isAddingToLibrary
        ) {
            Text(
                text = when {
                    isAddingToLibrary -> "Adding..."
                    isInLibrary -> "In Library"
                    else -> "+ Want To"
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onWriteReview
        ) {
            Text(text = "Write a Review")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateBack
        ) {
            Text(text = "Back")
        }
    }
}