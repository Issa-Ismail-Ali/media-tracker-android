package edu.metrostate.ics342.mediatracker.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.metrostate.ics342.mediatracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    onEditProfile: () -> Unit,
    onSettingsClick: () -> Unit,
    onQuotesClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val user by
    viewModel.currentUser.collectAsStateWithLifecycle()

    val library by
    viewModel.libraryPreview.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    stringResource(
                        R.string.profile_title
                    )
                )
            },
            actions = {
                IconButton(
                    onClick = onSettingsClick
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.Settings,
                        contentDescription =
                            stringResource(
                                R.string.profile_settings
                            )
                    )
                }
            }
        )

        if (user == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            return@Column
        }

        val u = user!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            /*
             * Profile picture
             */
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape),
                contentAlignment =
                    Alignment.Center
            ) {
                if (u.avatarUrl != null) {
                    AsyncImage(
                        model = u.avatarUrl,
                        contentDescription =
                            u.displayName,
                        contentScale =
                            ContentScale.Crop,
                        modifier =
                            Modifier.fillMaxSize()
                    )
                } else {
                    Surface(
                        color =
                            MaterialTheme.colorScheme.primaryContainer,
                        modifier =
                            Modifier.fillMaxSize()
                    ) {
                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Text(
                                text =
                                    u.displayName
                                        .firstOrNull()
                                        ?.toString()
                                        ?: "?",
                                style =
                                    MaterialTheme.typography.headlineMedium,
                                color =
                                    MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            /*
             * Name
             */
            Text(
                text = u.displayName,
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            /*
             * Username
             */
            Text(
                text = "@${u.username}",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            /*
             * Bio
             */
            if (!u.bio.isNullOrBlank()) {
                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text = u.bio,
                    style =
                        MaterialTheme.typography.bodyMedium,
                    textAlign =
                        TextAlign.Center,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            /*
             * Profile stats
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value =
                        u.followerCount.toString(),
                    labelRes =
                        R.string.profile_followers
                )

                StatItem(
                    value =
                        u.followingCount.toString(),
                    labelRes =
                        R.string.profile_following
                )

                StatItem(
                    value =
                        u.trackedCount.toString(),
                    labelRes =
                        R.string.profile_tracked
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            /*
             * Edit Profile
             */
            OutlinedButton(
                onClick = onEditProfile,
                modifier =
                    Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(
                        R.string.profile_edit_button
                    )
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            /*
             * NEW: My Quotes
             */
            Button(
                onClick = onQuotesClick,
                modifier =
                    Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "My Quotes"
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            /*
             * Recently tracked
             */
            Text(
                text =
                    stringResource(
                        R.string.profile_recently_tracked
                    ),
                style =
                    MaterialTheme.typography.titleMedium,
                modifier =
                    Modifier.align(
                        Alignment.Start
                    )
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            if (library.isEmpty()) {
                Text(
                    text =
                        stringResource(
                            R.string.profile_nothing_tracked
                        ),
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                library.forEach { item ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 6.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(
                                    width = 40.dp,
                                    height = 56.dp
                                )
                                .clip(
                                    RoundedCornerShape(
                                        4.dp
                                    )
                                ),
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Surface(
                                color =
                                    MaterialTheme.colorScheme.surfaceVariant,
                                modifier =
                                    Modifier.fillMaxSize()
                            ) {
                                Box(
                                    contentAlignment =
                                        Alignment.Center
                                ) {
                                    Text(
                                        text =
                                            when (
                                                item.media.mediaType
                                            ) {
                                                "book" ->
                                                    "📖"

                                                "movie" ->
                                                    "🎬"

                                                "show" ->
                                                    "📺"

                                                else ->
                                                    "?"
                                            }
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.width(12.dp)
                        )

                        Column {
                            Text(
                                text =
                                    item.media.title,
                                style =
                                    MaterialTheme.typography.bodyMedium,
                                fontWeight =
                                    FontWeight.Medium
                            )

                            Text(
                                text =
                                    stringResource(
                                        item.status.labelRes
                                    ),
                                style =
                                    MaterialTheme.typography.labelSmall,
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    labelRes: Int
) {
    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight =
                FontWeight.Bold
        )

        Text(
            text =
                stringResource(labelRes),
            style =
                MaterialTheme.typography.labelSmall,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}