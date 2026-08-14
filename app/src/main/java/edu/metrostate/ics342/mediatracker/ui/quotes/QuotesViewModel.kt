package edu.metrostate.ics342.mediatracker.ui.quotes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.datastore.DefaultSessionRepository
import edu.metrostate.ics342.mediatracker.data.model.Quote
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class QuotesMode {
    MY_QUOTES,
    PUBLIC
}

sealed interface QuotesUiState {

    data object Loading : QuotesUiState

    data class Error(
        val message: String
    ) : QuotesUiState

    data class Success(
        val quotes: List<Quote>,
        val isLoadingMore: Boolean = false,
        val actionError: String? = null,
        val likedQuoteIds: Set<Int> = emptySet(),
        val likeBusyIds: Set<Int> = emptySet()
    ) : QuotesUiState
}

class QuotesViewModel(
    application: Application,
    private val repository: DefaultMediaRepository
) : AndroidViewModel(application) {

    constructor(
        application: Application
    ) : this(
        application = application,
        repository =
            DefaultMediaRepository(
                DefaultSessionRepository(
                    application.applicationContext
                )
            )
    )

    private val _uiState =
        MutableStateFlow<QuotesUiState>(
            QuotesUiState.Loading
        )

    val uiState: StateFlow<QuotesUiState> =
        _uiState.asStateFlow()

    private val _mode =
        MutableStateFlow(
            QuotesMode.MY_QUOTES
        )

    val mode: StateFlow<QuotesMode> =
        _mode.asStateFlow()

    private var nextCursor: String? = null
    private var hasMore = false
    private var loadingMore = false

    init {
        loadQuotes(
            QuotesMode.MY_QUOTES
        )
    }

    fun loadQuotes(
        mode: QuotesMode = _mode.value
    ) {
        _mode.value = mode

        _uiState.value =
            QuotesUiState.Loading

        nextCursor = null
        hasMore = false
        loadingMore = false

        viewModelScope.launch {
            try {
                val page =
                    repository.getQuotes(
                        publicOnly =
                            if (
                                mode ==
                                QuotesMode.PUBLIC
                            ) {
                                true
                            } else {
                                null
                            },
                        after = null
                    )

                nextCursor =
                    page.nextCursor

                hasMore =
                    page.hasMore

                _uiState.value =
                    QuotesUiState.Success(
                        quotes = page.items
                    )

            } catch (exception: Exception) {
                exception.printStackTrace()

                _uiState.value =
                    QuotesUiState.Error(
                        exception.message
                            ?: "Unable to load quotes."
                    )
            }
        }
    }

    fun retry() {
        loadQuotes(
            _mode.value
        )
    }

    fun loadNextPageIfNeeded() {
        if (
            loadingMore ||
            !hasMore ||
            nextCursor == null
        ) {
            return
        }

        val current =
            _uiState.value
                    as? QuotesUiState.Success
                ?: return

        loadingMore = true

        _uiState.value =
            current.copy(
                isLoadingMore = true
            )

        viewModelScope.launch {
            try {
                val page =
                    repository.getQuotes(
                        publicOnly =
                            if (
                                _mode.value ==
                                QuotesMode.PUBLIC
                            ) {
                                true
                            } else {
                                null
                            },
                        after = nextCursor
                    )

                nextCursor =
                    page.nextCursor

                hasMore =
                    page.hasMore

                val latest =
                    _uiState.value
                            as? QuotesUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        quotes =
                            latest.quotes +
                                    page.items,
                        isLoadingMore = false
                    )

            } catch (exception: Exception) {
                exception.printStackTrace()

                val latest =
                    _uiState.value
                            as? QuotesUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        isLoadingMore = false,
                        actionError =
                            "Unable to load more quotes."
                    )

            } finally {
                loadingMore = false
            }
        }
    }

    fun deleteQuote(
        quoteId: Int
    ) {
        val current =
            _uiState.value
                    as? QuotesUiState.Success
                ?: return

        val backup =
            current.quotes
                .firstOrNull {
                    it.id == quoteId
                } ?: return

        _uiState.value =
            current.copy(
                quotes =
                    current.quotes.filter {
                        it.id != quoteId
                    },
                actionError = null
            )

        viewModelScope.launch {
            try {
                repository.deleteQuote(
                    quoteId
                )

            } catch (exception: Exception) {
                exception.printStackTrace()

                val latest =
                    _uiState.value
                            as? QuotesUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        quotes =
                            latest.quotes +
                                    backup,
                        actionError =
                            "Couldn't delete quote. Try again."
                    )
            }
        }
    }

    fun updateQuote(
        quoteId: Int,
        quoteText: String,
        pageNumber: Int?,
        isPublic: Boolean
    ) {
        val current =
            _uiState.value
                    as? QuotesUiState.Success
                ?: return

        val oldQuote =
            current.quotes
                .firstOrNull {
                    it.id == quoteId
                } ?: return

        val optimisticQuote =
            oldQuote.copy(
                quoteText = quoteText,
                pageNumber = pageNumber,
                isPublic = isPublic
            )

        _uiState.value =
            current.copy(
                quotes =
                    current.quotes.map {
                        if (
                            it.id == quoteId
                        ) {
                            optimisticQuote
                        } else {
                            it
                        }
                    },
                actionError = null
            )

        viewModelScope.launch {
            try {
                val saved =
                    repository.updateQuote(
                        quoteId = quoteId,
                        quoteText = quoteText,
                        pageNumber = pageNumber,
                        isPublic = isPublic
                    )

                val latest =
                    _uiState.value
                            as? QuotesUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        quotes =
                            latest.quotes.map {
                                if (
                                    it.id == quoteId
                                ) {
                                    saved
                                } else {
                                    it
                                }
                            }
                    )

            } catch (exception: Exception) {
                exception.printStackTrace()

                val latest =
                    _uiState.value
                            as? QuotesUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        quotes =
                            latest.quotes.map {
                                if (
                                    it.id == quoteId
                                ) {
                                    oldQuote
                                } else {
                                    it
                                }
                            },
                        actionError =
                            "Couldn't update quote. Try again."
                    )
            }
        }
    }

    /*
     * Optimistic Like / Unlike
     *
     * Not liked -> POST /quotes/{id}/likes
     * Liked     -> DELETE /quotes/{id}/likes
     */
    fun toggleLike(
        quoteId: Int
    ) {
        if (
            _mode.value !=
            QuotesMode.PUBLIC
        ) {
            return
        }

        val current =
            _uiState.value
                    as? QuotesUiState.Success
                ?: return

        /*
         * Prevent double taps while the
         * network call is still running.
         */
        if (
            quoteId in
            current.likeBusyIds
        ) {
            return
        }

        val oldQuote =
            current.quotes
                .firstOrNull {
                    it.id == quoteId
                } ?: return

        val wasLiked =
            quoteId in
                    current.likedQuoteIds

        val newLikedIds =
            if (wasLiked) {
                current.likedQuoteIds -
                        quoteId
            } else {
                current.likedQuoteIds +
                        quoteId
            }

        val optimisticQuote =
            oldQuote.copy(
                likeCount =
                    if (wasLiked) {
                        (oldQuote.likeCount - 1)
                            .coerceAtLeast(0)
                    } else {
                        oldQuote.likeCount + 1
                    }
            )

        /*
         * Update the button and count first.
         */
        _uiState.value =
            current.copy(
                quotes =
                    current.quotes.map {
                        if (
                            it.id == quoteId
                        ) {
                            optimisticQuote
                        } else {
                            it
                        }
                    },
                likedQuoteIds =
                    newLikedIds,
                likeBusyIds =
                    current.likeBusyIds +
                            quoteId,
                actionError = null
            )

        viewModelScope.launch {
            try {
                if (wasLiked) {
                    repository.unlikeQuote(
                        quoteId
                    )
                } else {
                    /*
                     * Repository already treats
                     * 409 "already liked" as okay.
                     */
                    repository.likeQuote(
                        quoteId
                    )
                }

                val latest =
                    _uiState.value
                            as? QuotesUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        likeBusyIds =
                            latest.likeBusyIds -
                                    quoteId
                    )

            } catch (exception: Exception) {
                exception.printStackTrace()

                val latest =
                    _uiState.value
                            as? QuotesUiState.Success
                        ?: return@launch

                /*
                 * Genuine failure:
                 * roll everything back.
                 */
                _uiState.value =
                    latest.copy(
                        quotes =
                            latest.quotes.map {
                                if (
                                    it.id == quoteId
                                ) {
                                    oldQuote
                                } else {
                                    it
                                }
                            },
                        likedQuoteIds =
                            if (wasLiked) {
                                latest.likedQuoteIds +
                                        quoteId
                            } else {
                                latest.likedQuoteIds -
                                        quoteId
                            },
                        likeBusyIds =
                            latest.likeBusyIds -
                                    quoteId,
                        actionError =
                            "Couldn't update like. Try again."
                    )
            }
        }
    }

    fun clearActionError() {
        val current =
            _uiState.value
                    as? QuotesUiState.Success
                ?: return

        _uiState.value =
            current.copy(
                actionError = null
            )
    }
}