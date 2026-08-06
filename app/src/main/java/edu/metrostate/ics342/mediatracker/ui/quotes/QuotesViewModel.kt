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

sealed interface QuotesUiState {

    data object Loading : QuotesUiState

    data class Error(
        val message: String
    ) : QuotesUiState

    data class Success(
        val quotes: List<Quote>,
        val isLoadingMore: Boolean = false
    ) : QuotesUiState
}

class QuotesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        DefaultMediaRepository(
            DefaultSessionRepository(
                application.applicationContext
            )
        )

    private val _uiState =
        MutableStateFlow<QuotesUiState>(
            QuotesUiState.Loading
        )

    val uiState: StateFlow<QuotesUiState> =
        _uiState.asStateFlow()

    private var nextCursor: String? = null
    private var hasMore = false
    private var loadingMore = false

    init {
        loadQuotes()
    }

    fun loadQuotes() {
        _uiState.value = QuotesUiState.Loading
        nextCursor = null
        hasMore = false

        viewModelScope.launch {
            try {
                val page = repository.getQuotes()

                nextCursor = page.nextCursor
                hasMore = page.hasMore

                _uiState.value =
                    QuotesUiState.Success(
                        quotes = page.items
                    )
            } catch (exception: Exception) {
                _uiState.value =
                    QuotesUiState.Error(
                        exception.message
                            ?: "Unable to load quotes."
                    )
            }
        }
    }

    fun loadNextPageIfNeeded() {
        if (loadingMore || !hasMore || nextCursor == null) {
            return
        }

        loadingMore = true

        val current =
            _uiState.value as? QuotesUiState.Success
                ?: return

        _uiState.value =
            current.copy(
                isLoadingMore = true
            )

        viewModelScope.launch {
            try {
                val page =
                    repository.getQuotes(
                        after = nextCursor
                    )

                nextCursor = page.nextCursor
                hasMore = page.hasMore

                _uiState.value =
                    current.copy(
                        quotes = current.quotes + page.items,
                        isLoadingMore = false
                    )
            } catch (_: Exception) {
                _uiState.value =
                    current.copy(
                        isLoadingMore = false
                    )
            } finally {
                loadingMore = false
            }
        }
    }
}