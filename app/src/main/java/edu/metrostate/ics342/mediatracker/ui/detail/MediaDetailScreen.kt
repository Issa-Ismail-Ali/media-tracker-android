package edu.metrostate.ics342.mediatracker.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.model.creatorCredit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailScreen(
    mediaId: Int,
    onNavigateBack: () -> Unit,
    onWriteReview: (Int) -> Unit,
    viewModel: MediaDetailViewModel = viewModel()
) {
    LaunchedEffect(mediaId) {
        viewModel.setMediaId(mediaId)
    }

    val media by viewModel.media.collectAsState()
    val currentMedia = media

    if (currentMedia == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CoverPlaceholder(currentMedia.mediaType)

            Spacer(Modifier.height(24.dp))

            Text(
                text = currentMedia.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = currentMedia.creatorCredit(context),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (index < currentMedia.averageRating.toInt())
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "${currentMedia.averageRating} (${currentMedia.ratingCount})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ Want To")
                }

                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text("Save")
                }
            }

            Spacer(Modifier.height(32.dp))

            SectionTitle("About")

            Text(
                text = descriptionFor(currentMedia),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBox(
                    label = "Year",
                    value = currentMedia.publishedYear?.toString() ?: "N/A",
                    modifier = Modifier.weight(1f)
                )

                StatBox(
                    label = middleStatLabel(currentMedia),
                    value = middleStatValue(currentMedia),
                    modifier = Modifier.weight(1f)
                )

                StatBox(
                    label = "Genre",
                    value = currentMedia.genres.firstOrNull() ?: "N/A",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reviews (2)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                TextButton(onClick = { onWriteReview(currentMedia.id) }) {
                    Text("+ Write Review")
                }
            }

            Spacer(Modifier.height(12.dp))

            ReviewCard(
                initials = "IA",
                username = "issaali",
                timestamp = "Today",
                rating = 5,
                reviewText = "This was really good and easy to follow."
            )

            Spacer(Modifier.height(12.dp))

            ReviewCard(
                initials = "JP",
                username = "jpatel",
                timestamp = "2 days ago",
                rating = 4,
                reviewText = "I liked the story and the pacing was solid."
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CoverPlaceholder(mediaType: String) {
    val emoji = when (mediaType) {
        "book" -> "📖"
        "movie" -> "🎬"
        "show" -> "📺"
        else -> "?"
    }

    Box(
        modifier = Modifier
            .size(width = 190.dp, height = 260.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.displayLarge
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ReviewCard(
    initials: String,
    username: String,
    timestamp: String,
    rating: Int,
    reviewText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "★".repeat(rating),
                    color = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    text = reviewText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun descriptionFor(media: Media): String {
    return when (media.mediaType) {
        "book" -> "A story about reading, discovery, and imagination. This detail screen is using FakeMediaRepository data for now until the real media detail API is connected."
        "movie" -> "A film with strong visuals, emotional moments, and a memorable story. This description is hardcoded for the Week 7 layout."
        "show" -> "A series with multiple episodes and an ongoing story. This description is hardcoded for the Week 7 layout."
        else -> "No description available."
    }
}

private fun middleStatLabel(media: Media): String {
    return when (media.mediaType) {
        "book" -> "Pages"
        "movie" -> "Runtime"
        "show" -> "Seasons"
        else -> "Info"
    }
}

private fun middleStatValue(media: Media): String {
    return when (media.mediaType) {
        "book" -> "412"
        "movie" -> "169 min"
        "show" -> "2"
        else -> "N/A"
    }
}