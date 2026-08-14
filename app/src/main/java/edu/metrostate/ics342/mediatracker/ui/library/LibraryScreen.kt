package edu.metrostate.ics342.mediatracker.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.metrostate.ics342.mediatracker.R
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.model.creatorCredit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onMediaClick: (Int) -> Unit,
    viewModel: LibraryViewModel = viewModel()
) {
    val uiState by
    viewModel.uiState.collectAsState()

    val selectedStatus by
    viewModel.selectedStatus.collectAsState()

    var selectedType by remember {
        mutableStateOf("all")
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(
                        R.string.library_title
                    )
                )
            }
        )

        /*
         * Media-type filters are local screen filters.
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "all" to R.string.filter_all,
                "book" to R.string.filter_books,
                "movie" to R.string.filter_movies,
                "show" to R.string.filter_shows
            ).forEach { (type, labelRes) ->
                FilterChip(
                    selected =
                        selectedType == type,
                    onClick = {
                        selectedType = type
                    },
                    label = {
                        Text(
                            text = stringResource(
                                labelRes
                            )
                        )
                    }
                )
            }
        }

        /*
         * These buttons reload GET /library using
         * the selected status query.
         */
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                )
        ) {
            LibraryStatus.entries
                .forEachIndexed { index, status ->
                    SegmentedButton(
                        shape =
                            SegmentedButtonDefaults.itemShape(
                                index = index,
                                count =
                                    LibraryStatus.entries.size
                            ),
                        selected =
                            selectedStatus == status,
                        onClick = {
                            viewModel.loadLibrary(
                                status
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(
                                    status.labelRes
                                )
                            )
                        }
                    )
                }
        }

        HorizontalDivider(
            modifier = Modifier.padding(
                top = 8.dp
            )
        )

        when (val state = uiState) {
            LibraryUiState.Loading -> {
                LibraryLoadingContent()
            }

            is LibraryUiState.Error -> {
                LibraryErrorContent(
                    message = state.message,
                    onRetry = viewModel::retry
                )
            }

            is LibraryUiState.Success -> {
                val filteredItems =
                    state.items.filter { item ->
                        selectedType == "all" ||
                                item.media.mediaType ==
                                selectedType
                    }

                /*
                 * Shows rollback/action failures without
                 * replacing the whole list.
                 */
                state.actionError?.let { message ->
                    Text(
                        text = message,
                        color =
                            MaterialTheme.colorScheme.error,
                        style =
                            MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    )
                }

                if (filteredItems.isEmpty()) {
                    LibraryEmptyContent(
                        selectedStatus =
                            selectedStatus
                    )
                } else {
                    LibrarySuccessContent(
                        items = filteredItems,
                        onMediaClick = onMediaClick,
                        onRemove = viewModel::removeItem,
                        onStatusChange =
                            viewModel::updateStatus
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryLoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LibraryErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {
        Text(
            text = message,
            style =
                MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = onRetry
        ) {
            Text(
                text = stringResource(
                    R.string.detail_retry
                )
            )
        }
    }
}

@Composable
private fun LibraryEmptyContent(
    selectedStatus: LibraryStatus
) {
    val emptyMessage =
        when (selectedStatus) {
            LibraryStatus.WANT_TO ->
                "Nothing in 'Want To' yet."

            LibraryStatus.IN_PROGRESS ->
                "Nothing in 'In Progress' yet."

            LibraryStatus.FINISHED ->
                "Nothing in 'Finished' yet."
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emptyMessage,
            style =
                MaterialTheme.typography.bodyLarge,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LibrarySuccessContent(
    items: List<LibraryItem>,
    onMediaClick: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onStatusChange:
        (Int, LibraryStatus) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text =
                if (items.size == 1) {
                    stringResource(
                        R.string.library_item_count,
                        items.size
                    )
                } else {
                    stringResource(
                        R.string.library_items_count,
                        items.size
                    )
                },
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            style =
                MaterialTheme.typography.labelMedium,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 4.dp
            ),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = items,
                key = { item ->
                    item.mediaId
                }
            ) { item ->
                LibraryItemCard(
                    item = item,
                    onClick = {
                        onMediaClick(
                            item.mediaId
                        )
                    },
                    onRemove = {
                        onRemove(
                            item.mediaId
                        )
                    },
                    onStatusChange = { newStatus ->
                        onStatusChange(
                            item.mediaId,
                            newStatus
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun LibraryItemCard(
    item: LibraryItem,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onStatusChange: (LibraryStatus) -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    var statusDialogVisible by remember {
        mutableStateOf(false)
    }

    if (statusDialogVisible) {
        StatusChangeDialog(
            currentStatus = item.status,
            onDismiss = {
                statusDialogVisible = false
            },
            onStatusSelected = { newStatus ->
                statusDialogVisible = false

                if (newStatus != item.status) {
                    onStatusChange(newStatus)
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            LibraryCover(item)

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.media.title,
                    style =
                        MaterialTheme.typography.titleSmall,
                    fontWeight =
                        FontWeight.SemiBold,
                    maxLines = 2
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text =
                        item.media.creatorCredit(
                            LocalContext.current
                        ),
                    style =
                        MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                SuggestionChip(
                    onClick = {
                        statusDialogVisible = true
                    },
                    label = {
                        Text(
                            text = stringResource(
                                item.status.labelRes
                            ),
                            style =
                                MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }

            Box {
                IconButton(
                    onClick = {
                        menuExpanded = true
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

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = {
                        menuExpanded = false
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(
                                    R.string.action_change_status
                                )
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            statusDialogVisible = true
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(
                                    R.string.action_remove_from_library
                                ),
                                color =
                                    MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onRemove()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryCover(
    item: LibraryItem
) {
    Box(
        modifier = Modifier
            .size(
                width = 64.dp,
                height = 90.dp
            )
            .clip(
                RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!item.media.coverUrl.isNullOrBlank()) {
            AsyncImage(
                model = item.media.coverUrl,
                contentDescription =
                    item.media.title,
                contentScale =
                    ContentScale.Crop,
                modifier =
                    Modifier.fillMaxSize()
            )
        } else {
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
                                "book" -> "📖"
                                "movie" -> "🎬"
                                "show" -> "📺"
                                else -> "?"
                            },
                        style =
                            MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChangeDialog(
    currentStatus: LibraryStatus,
    onDismiss: () -> Unit,
    onStatusSelected:
        (LibraryStatus) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(
                    R.string.action_change_status
                )
            )
        },
        text = {
            Column {
                LibraryStatus.entries.forEach { status ->
                    TextButton(
                        onClick = {
                            onStatusSelected(status)
                        },
                        enabled =
                            status != currentStatus,
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(
                                status.labelRes
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(
                        R.string.settings_cancel_button
                    )
                )
            }
        }
    )
}