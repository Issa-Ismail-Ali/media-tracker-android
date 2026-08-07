package edu.metrostate.ics342.mediatracker.ui.quotes

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.metrostate.ics342.mediatracker.data.model.Quote

@Composable
fun QuotesScreen(
    viewModel: QuotesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val mode by viewModel.mode.collectAsState()

    var quoteToEdit by remember {
        mutableStateOf<Quote?>(null)
    }

    var quoteToDelete by remember {
        mutableStateOf<Quote?>(null)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "Quotes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(
                start = 24.dp,
                top = 24.dp,
                end = 24.dp
            )
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            if (mode == QuotesMode.MY_QUOTES) {
                Button(
                    onClick = {
                        viewModel.loadQuotes(
                            QuotesMode.MY_QUOTES
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("✓ My Quotes")
                }
            } else {
                OutlinedButton(
                    onClick = {
                        viewModel.loadQuotes(
                            QuotesMode.MY_QUOTES
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("My Quotes")
                }
            }

            if (mode == QuotesMode.PUBLIC) {
                Button(
                    onClick = {
                        viewModel.loadQuotes(
                            QuotesMode.PUBLIC
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("✓ Public")
                }
            } else {
                OutlinedButton(
                    onClick = {
                        viewModel.loadQuotes(
                            QuotesMode.PUBLIC
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Public")
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        when (val state = uiState) {

            QuotesUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is QuotesUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.message,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.loadQuotes(mode)
                        }
                    ) {
                        Text("Retry")
                    }
                }
            }

            is QuotesUiState.Success -> {

                if (state.quotes.isEmpty()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text =
                                if (mode == QuotesMode.MY_QUOTES) {
                                    "No quotes saved yet — add one from a book's detail page."
                                } else {
                                    "No public quotes yet."
                                },
                            textAlign = TextAlign.Center,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding =
                            PaddingValues(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        itemsIndexed(
                            items = state.quotes,
                            key = { _, quote ->
                                quote.id
                            }
                        ) { index, quote ->

                            if (
                                index >=
                                state.quotes.lastIndex - 3
                            ) {
                                viewModel.loadNextPageIfNeeded()
                            }

                            QuoteCard(
                                quote = quote,
                                mode = mode,
                                isLiked =
                                    quote.id in
                                            state.likedQuoteIds,
                                isLikeBusy =
                                    quote.id in
                                            state.likeBusyIds,
                                onLikeClick = {
                                    viewModel.toggleLike(
                                        quote.id
                                    )
                                },
                                onEdit = {
                                    quoteToEdit = quote
                                },
                                onDelete = {
                                    quoteToDelete = quote
                                }
                            )
                        }

                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment =
                                        Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        state.actionError?.let { error ->
                            item {
                                Text(
                                    text = error,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .error,
                                    modifier =
                                        Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    quoteToEdit?.let { quote ->

        EditQuoteDialog(
            quote = quote,
            onDismiss = {
                quoteToEdit = null
            },
            onSave = {
                    quoteText,
                    pageNumber,
                    isPublic ->

                viewModel.updateQuote(
                    quoteId = quote.id,
                    quoteText = quoteText,
                    pageNumber = pageNumber,
                    isPublic = isPublic
                )

                quoteToEdit = null
            }
        )
    }

    quoteToDelete?.let { quote ->

        AlertDialog(
            onDismissRequest = {
                quoteToDelete = null
            },
            title = {
                Text("Delete Quote")
            },
            text = {
                Text(
                    "Are you sure you want to delete this quote?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteQuote(
                            quote.id
                        )

                        quoteToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        quoteToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    mode: QuotesMode,
    isLiked: Boolean,
    isLikeBusy: Boolean,
    onLikeClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "“${quote.quoteText}”",
                style =
                    MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic
            )

            quote.pageNumber?.let { page ->

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Page $page",
                    style =
                        MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = quote.media.title,
                style =
                    MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    if (quote.isPublic) {
                        "Public"
                    } else {
                        "Private"
                    },
                style =
                    MaterialTheme.typography.labelMedium,
                color =
                    MaterialTheme.colorScheme.primary
            )

            /*
             * Only show Edit and Delete
             * for My Quotes.
             */
            if (mode == QuotesMode.MY_QUOTES) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit")
                    }

                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete")
                    }
                }
            }

            /*
             * Public quote likes will go here.
             * This keeps Edit/Delete away from
             * other users' public quotes.
             */
            /*
             * Public feed:
             * Like / Unlike public quotes.
             */
            if (mode == QuotesMode.PUBLIC) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    if (isLiked) {

                        Button(
                            onClick = onLikeClick,
                            enabled = !isLikeBusy
                        ) {
                            if (isLikeBusy) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )

                                Spacer(
                                    modifier = Modifier.width(6.dp)
                                )
                            }

                            Text("♥ Unlike")
                        }

                    } else {

                        OutlinedButton(
                            onClick = onLikeClick,
                            enabled = !isLikeBusy
                        ) {
                            if (isLikeBusy) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )

                                Spacer(
                                    modifier = Modifier.width(6.dp)
                                )
                            }

                            Text("♡ Like")
                        }
                    }

                    Text(
                        text =
                            if (quote.likeCount == 1) {
                                "1 like"
                            } else {
                                "${quote.likeCount} likes"
                            },
                        style = MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EditQuoteDialog(
    quote: Quote,
    onDismiss: () -> Unit,
    onSave: (
        quoteText: String,
        pageNumber: Int?,
        isPublic: Boolean
    ) -> Unit
) {

    var quoteText by remember(quote.id) {
        mutableStateOf(
            quote.quoteText
        )
    }

    var pageNumber by remember(quote.id) {
        mutableStateOf(
            quote.pageNumber?.toString() ?: ""
        )
    }

    /*
     * This starts with the quote's current
     * Public/Private value.
     */
    var isPublic by remember(quote.id) {
        mutableStateOf(
            quote.isPublic
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Edit Quote")
        },

        text = {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = quoteText,
                    onValueChange = {
                        if (it.length <= 500) {
                            quoteText = it
                        }
                    },
                    label = {
                        Text("Quote")
                    },
                    supportingText = {
                        Text(
                            "${quoteText.length}/500"
                        )
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = pageNumber,
                    onValueChange = { value ->

                        if (
                            value.isEmpty() ||
                            value.all {
                                    character ->
                                character.isDigit()
                            }
                        ) {
                            pageNumber = value
                        }
                    },
                    label = {
                        Text(
                            "Page number (optional)"
                        )
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),
                    singleLine = true,
                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
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
                            onClick = {
                                isPublic = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("✓ Private")
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                isPublic = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Private")
                        }
                    }

                    if (isPublic) {
                        Button(
                            onClick = {
                                isPublic = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("✓ Public")
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                isPublic = true
                            },
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
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    onSave(
                        quoteText.trim(),
                        pageNumber.toIntOrNull(),
                        isPublic
                    )
                },
                enabled =
                    quoteText.isNotBlank() &&
                            quoteText.length <= 500
            ) {
                Text("Save")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}