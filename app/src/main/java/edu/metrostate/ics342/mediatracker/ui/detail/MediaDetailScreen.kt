package edu.metrostate.ics342.mediatracker.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.metrostate.ics342.mediatracker.R
import edu.metrostate.ics342.mediatracker.data.model.MediaDetail

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription =
                                stringResource(
                                    R.string.action_back
                                )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // More options can be added later.
                        }
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined.MoreVert,
                            contentDescription =
                                stringResource(
                                    R.string.action_more_options
                                )
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        when (val state = uiState) {

            MediaDetailUiState.Loading -> {
                LoadingContent(
                    modifier =
                        Modifier.padding(
                            innerPadding
                        )
                )
            }

            is MediaDetailUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry =
                        viewModel::retry,
                    onNavigateBack =
                        onNavigateBack,
                    modifier =
                        Modifier.padding(
                            innerPadding
                        )
                )
            }

            is MediaDetailUiState.Success -> {
                MediaDetailSuccessContent(
                    state = state,
                    onAddToLibrary =
                        viewModel::addToLibrary,
                    onSaveFavorite =
                        viewModel::addFavorite,
                    onSaveQuote =
                        viewModel::saveQuote,
                    onWriteReview = {
                        onWriteReview(
                            mediaId
                        )
                    },
                    modifier =
                        Modifier.padding(
                            innerPadding
                        )
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {
        Text(
            text = message,
            textAlign =
                TextAlign.Center,
            style =
                MaterialTheme
                    .typography
                    .bodyLarge
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        Button(
            onClick = onRetry
        ) {
            Text(
                text =
                    stringResource(
                        R.string.detail_retry
                    )
            )
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        OutlinedButton(
            onClick =
                onNavigateBack
        ) {
            Text(
                text =
                    stringResource(
                        R.string.action_back
                    )
            )
        }
    }
}

@Composable
private fun MediaDetailSuccessContent(
    state: MediaDetailUiState.Success,
    onAddToLibrary: () -> Unit,
    onSaveFavorite: () -> Unit,
    onSaveQuote:
        (
        String,
        String,
        Boolean
    ) -> Unit,
    onWriteReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val media = state.media

    var showQuoteDialog by remember {
        mutableStateOf(false)
    }

    if (showQuoteDialog) {
        AddQuoteDialog(
            isSaving =
                state.isSavingQuote,
            message =
                state.quoteMessage,
            onDismiss = {
                if (!state.isSavingQuote) {
                    showQuoteDialog =
                        false
                }
            },
            onSave = {
                    quoteText,
                    pageNumber,
                    isPublic ->

                onSaveQuote(
                    quoteText,
                    pageNumber,
                    isPublic
                )
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 20.dp,
                vertical = 8.dp
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        CoverImage(media)

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Text(
            text = media.title,
            style =
                MaterialTheme
                    .typography
                    .headlineSmall,
            fontWeight =
                FontWeight.Bold,
            textAlign =
                TextAlign.Center
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        Text(
            text =
                creatorCredit(
                    media
                ),
            style =
                MaterialTheme
                    .typography
                    .bodyLarge,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,
            textAlign =
                TextAlign.Center
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        RatingRow(media)

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        ActionButtons(
            state = state,
            onAddToLibrary =
                onAddToLibrary,
            onSaveFavorite =
                onSaveFavorite
        )

        /*
         * NEW: Add Quote button
         */
        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedButton(
            onClick = {
                showQuoteDialog = true
            },
            modifier =
                Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Add Quote"
            )
        }

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        AboutSection(media)

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        StatGrid(media)

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        ReviewsHeader(
            reviewCount =
                media.ratingCount ?: 0,
            onWriteReview =
                onWriteReview
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        ReviewCard(
            username =
                "alex_reader",
            timestamp =
                "2 days ago",
            rating = 5,
            reviewText =
                "I really enjoyed this one. The story kept me interested from beginning to end."
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        ReviewCard(
            username =
                "media_fan",
            timestamp =
                "1 week ago",
            rating = 4,
            reviewText =
                "A strong recommendation overall. The characters and pacing were especially good."
        )

        Spacer(
            modifier =
                Modifier.height(32.dp)
        )
    }
}

/*
 * NEW: Quote Dialog
 */
@Composable
private fun AddQuoteDialog(
    isSaving: Boolean,
    message: String?,
    onDismiss: () -> Unit,
    onSave: (
        String,
        String,
        Boolean
    ) -> Unit
) {
    var quoteText by remember {
        mutableStateOf("")
    }

    var pageNumber by remember {
        mutableStateOf("")
    }

    var isPublic by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onDismiss()
            }
        },
        title = {
            Text(text = "Add Quote")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = quoteText,
                    onValueChange = { newText ->
                        if (newText.length <= 500) {
                            quoteText = newText
                        }
                    },
                    label = { Text("Quote") },
                    supportingText = {
                        Text("${quoteText.length}/500")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    enabled = !isSaving
                )

                OutlinedTextField(
                    value = pageNumber,
                    onValueChange = { newValue ->
                        if (
                            newValue.isBlank() ||
                            newValue.all { it.isDigit() }
                        ) {
                            pageNumber = newValue
                        }
                    },
                    label = {
                        Text("Page number (optional)")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving
                )

                Text(
                    text = "Visibility",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isPublic) {
                        Button(
                            onClick = { isPublic = false },
                            enabled = !isSaving,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("✓ Private")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { isPublic = false },
                            enabled = !isSaving,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Private")
                        }
                    }

                    if (isPublic) {
                        Button(
                            onClick = { isPublic = true },
                            enabled = !isSaving,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("✓ Public")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { isPublic = true },
                            enabled = !isSaving,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Public")
                        }
                    }
                }

                Text(
                    text =
                        if (isPublic) {
                            "Everyone can see this quote."
                        } else {
                            "Only you can see this quote."
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!message.isNullOrBlank()) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color =
                            if (message == "Quote saved.") {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        quoteText,
                        pageNumber,
                        isPublic
                    )
                },
                enabled =
                    quoteText.isNotBlank() &&
                            quoteText.length <= 500 &&
                            !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text("Saving...")
                } else {
                    Text("Save Quote")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CoverImage(
    media: MediaDetail
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.55f)
            .aspectRatio(0.7f),
        shape =
            RoundedCornerShape(
                14.dp
            ),
        tonalElevation = 3.dp
    ) {
        if (
            !media.coverUrl
                .isNullOrBlank()
        ) {
            AsyncImage(
                model =
                    media.coverUrl,
                contentDescription =
                    media.title,
                contentScale =
                    ContentScale.Crop,
                modifier =
                    Modifier
                        .fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                    ),
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text =
                        mediaTypeEmoji(
                            media
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .displayMedium
                )
            }
        }
    }
}

@Composable
private fun RatingRow(
    media: MediaDetail
) {
    Row(
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.Center
    ) {
        if (
            media.averageRating >
            0.0
        ) {
            Icon(
                imageVector =
                    Icons.Filled.Star,
                contentDescription =
                    null,
                tint =
                    MaterialTheme
                        .colorScheme
                        .tertiary,
                modifier =
                    Modifier.size(
                        22.dp
                    )
            )

            Spacer(
                modifier =
                    Modifier.width(
                        4.dp
                    )
            )

            Text(
                text =
                    String.format(
                        "%.1f",
                        media.averageRating
                    ),
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.width(
                        6.dp
                    )
            )

            Text(
                text =
                    "(${media.ratingCount ?: 0})",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        } else {
            Text(
                text =
                    stringResource(
                        R.string
                            .detail_not_yet_rated
                    ),
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActionButtons(
    state:
    MediaDetailUiState.Success,
    onAddToLibrary:
        () -> Unit,
    onSaveFavorite:
        () -> Unit
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(
                12.dp
            )
    ) {

        Button(
            onClick =
                onAddToLibrary,
            enabled =
                !state.isInLibrary &&
                        !state.isAddingToLibrary,
            modifier =
                Modifier.weight(
                    1f
                ),
            contentPadding =
                PaddingValues(
                    horizontal = 8.dp
                )
        ) {

            if (
                state.isAddingToLibrary
            ) {
                CircularProgressIndicator(
                    modifier =
                        Modifier.size(
                            18.dp
                        ),
                    strokeWidth = 2.dp
                )

                Spacer(
                    modifier =
                        Modifier.width(
                            6.dp
                        )
                )

                Text(
                    text =
                        stringResource(
                            R.string
                                .detail_adding
                        )
                )
            } else {
                Text(
                    text =
                        if (
                            state.isInLibrary
                        ) {
                            stringResource(
                                R.string
                                    .detail_in_library
                            )
                        } else {
                            stringResource(
                                R.string
                                    .detail_add_want_to
                            )
                        }
                )
            }
        }

        if (state.isFavorite) {

            FilledTonalButton(
                onClick =
                    onSaveFavorite,
                enabled =
                    !state.isAddingFavorite,
                modifier =
                    Modifier.weight(
                        1f
                    ),
                contentPadding =
                    PaddingValues(
                        horizontal =
                            8.dp
                    )
            ) {
                Icon(
                    imageVector =
                        Icons.Filled
                            .Favorite,
                    contentDescription =
                        null,
                    modifier =
                        Modifier.size(
                            18.dp
                        )
                )

                Spacer(
                    modifier =
                        Modifier.width(
                            6.dp
                        )
                )

                Text(
                    text =
                        stringResource(
                            R.string
                                .detail_saved
                        )
                )
            }

        } else {

            OutlinedButton(
                onClick =
                    onSaveFavorite,
                enabled =
                    !state.isAddingFavorite,
                modifier =
                    Modifier.weight(
                        1f
                    ),
                contentPadding =
                    PaddingValues(
                        horizontal =
                            8.dp
                    )
            ) {

                if (
                    state.isAddingFavorite
                ) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(
                                18.dp
                            ),
                        strokeWidth =
                            2.dp
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                6.dp
                            )
                    )

                    Text(
                        text =
                            stringResource(
                                R.string
                                    .detail_saving
                            )
                    )

                } else {

                    Icon(
                        imageVector =
                            Icons.Outlined
                                .FavoriteBorder,
                        contentDescription =
                            null,
                        modifier =
                            Modifier.size(
                                18.dp
                            )
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                6.dp
                            )
                    )

                    Text(
                        text =
                            stringResource(
                                R.string
                                    .detail_save
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutSection(
    media: MediaDetail
) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {
        Text(
            text =
                stringResource(
                    R.string.detail_about
                ),
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            fontWeight =
                FontWeight.SemiBold
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                media.description
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: "No description available.",
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )
    }
}

@Composable
private fun StatGrid(
    media: MediaDetail
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(
                10.dp
            )
    ) {

        StatBox(
            label = "Year",
            value =
                media.publishedYear
                    ?.toString()
                    ?: "—",
            modifier =
                Modifier.weight(
                    1f
                )
        )

        val mediaType =
            media.mediaType
                .toString()
                .lowercase()

        val middleLabel:
                String

        val middleValue:
                String

        when {
            mediaType.contains(
                "book"
            ) -> {
                middleLabel =
                    "Pages"

                middleValue =
                    media.pageCount
                        ?.toString()
                        ?: "—"
            }

            mediaType.contains(
                "movie"
            ) -> {
                middleLabel =
                    "Runtime"

                middleValue =
                    media.runtimeMinutes
                        ?.let {
                            "$it min"
                        }
                        ?: "—"
            }

            mediaType.contains(
                "show"
            ) -> {
                middleLabel =
                    "Episodes"

                middleValue =
                    when {
                        media.seasonCount !=
                                null &&
                                media.episodeCount !=
                                null ->

                            "${media.seasonCount} seasons\n${media.episodeCount} episodes"

                        media.seasonCount !=
                                null ->

                            "${media.seasonCount} seasons"

                        media.episodeCount !=
                                null ->

                            "${media.episodeCount} episodes"

                        else ->
                            "—"
                    }
            }

            else -> {
                middleLabel =
                    "Details"

                middleValue =
                    "—"
            }
        }

        StatBox(
            label =
                middleLabel,
            value =
                middleValue,
            modifier =
                Modifier.weight(
                    1f
                )
        )

        StatBox(
            label = "Genre",
            value =
                media.genres
                    .firstOrNull()
                    ?: "—",
            modifier =
                Modifier.weight(
                    1f
                )
        )
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    modifier:
    Modifier = Modifier
) {
    Card(
        modifier =
            modifier,
        shape =
            RoundedCornerShape(
                12.dp
            ),
        colors =
            CardDefaults
                .cardColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                )
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal =
                            8.dp,
                        vertical =
                            14.dp
                    ),
            horizontalAlignment =
                Alignment
                    .CenterHorizontally
        ) {

            Text(
                text =
                    label,
                style =
                    MaterialTheme
                        .typography
                        .labelMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )

            Text(
                text =
                    value,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                fontWeight =
                    FontWeight.SemiBold,
                textAlign =
                    TextAlign.Center
            )
        }
    }
}

@Composable
private fun ReviewsHeader(
    reviewCount: Int,
    onWriteReview:
        () -> Unit
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text =
                stringResource(
                    R.string
                        .detail_reviews_count,
                    reviewCount
                ),
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            fontWeight =
                FontWeight.SemiBold
        )

        TextButton(
            onClick =
                onWriteReview
        ) {
            Text(
                text =
                    stringResource(
                        R.string
                            .detail_write_review
                    )
            )
        }
    }
}

@Composable
private fun ReviewCard(
    username: String,
    timestamp: String,
    rating: Int,
    reviewText: String
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                12.dp
            ),
        elevation =
            CardDefaults
                .cardElevation(
                    defaultElevation =
                        1.dp
                )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    16.dp
                )
        ) {

            Row(
                verticalAlignment =
                    Alignment
                        .CenterVertically
            ) {

                Box(
                    modifier =
                        Modifier
                            .size(
                                40.dp
                            )
                            .clip(
                                CircleShape
                            )
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            username
                                .firstOrNull()
                                ?.uppercase()
                                ?: "?",
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(
                            10.dp
                        )
                )

                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        text =
                            username,
                        fontWeight =
                            FontWeight
                                .SemiBold
                    )

                    Text(
                        text =
                            timestamp,
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }

                Row {
                    repeat(
                        rating
                    ) {
                        Icon(
                            imageVector =
                                Icons.Filled
                                    .Star,
                            contentDescription =
                                null,
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .tertiary,
                            modifier =
                                Modifier.size(
                                    16.dp
                                )
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )

            Text(
                text =
                    reviewText,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }
    }
}

private fun creatorCredit(
    media: MediaDetail
): String {

    val type =
        media.mediaType
            .toString()
            .lowercase()

    return when {

        type.contains(
            "book"
        ) ->
            media.author
                ?: "Unknown author"

        type.contains(
            "movie"
        ) ->
            media.director
                ?: "Unknown director"

        type.contains(
            "show"
        ) ->
            media.creator
                ?: "Unknown creator"

        else ->
            media.author
                ?: media.director
                ?: media.creator
                ?: ""
    }
}

private fun mediaTypeEmoji(
    media: MediaDetail
): String {

    val type =
        media.mediaType
            .toString()
            .lowercase()

    return when {

        type.contains(
            "book"
        ) -> "📖"

        type.contains(
            "movie"
        ) -> "🎬"

        type.contains(
            "show"
        ) -> "📺"

        else -> "?"
    }
}