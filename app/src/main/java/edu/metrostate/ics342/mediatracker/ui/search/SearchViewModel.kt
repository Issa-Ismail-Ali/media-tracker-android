package edu.metrostate.ics342.mediatracker.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.datastore.DefaultSessionRepository
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val sessionRepository =
        DefaultSessionRepository(
            application.applicationContext
        )

    private val repository =
        DefaultMediaRepository(
            sessionRepository
        )

    private val _query =
        MutableStateFlow("")

    val query: StateFlow<String> =
        _query.asStateFlow()

    private val _selectedType =
        MutableStateFlow<String?>(null)

    val selectedType: StateFlow<String?> =
        _selectedType.asStateFlow()

    private val _results =
        MutableStateFlow<List<Media>>(
            emptyList()
        )

    val results: StateFlow<List<Media>> =
        _results.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    private var searchJob: Job? = null
    private var nextCursor: String? = null
    private var hasMore = false
    private var isLoadingNextPage = false

    init {
        searchMedia()
    }

    fun onQueryChange(
        value: String
    ) {
        _query.value = value
        scheduleSearch()
    }

    fun onTypeSelected(
        type: String?
    ) {
        _selectedType.value = type
        scheduleSearch()
    }

    private fun scheduleSearch() {
        searchJob?.cancel()

        searchJob =
            viewModelScope.launch {
                delay(300)
                searchMedia()
            }
    }

    private fun searchMedia() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            nextCursor = null
            hasMore = false

            try {
                val page =
                    repository.search(
                        query = _query.value,
                        type = _selectedType.value,
                        after = null
                    )

                _results.value = page.items
                nextCursor = page.nextCursor
                hasMore = page.hasMore

                page.items.forEach { media ->
                    println(
                        "REAL SEARCH RESULT: " +
                                "id=${media.id}, " +
                                "title=${media.title}"
                    )
                }

            } catch (exception: Exception) {
                exception.printStackTrace()

                _results.value = emptyList()

                _errorMessage.value =
                    exception.message
                        ?: "Unable to search media."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPageIfNeeded() {
        if (
            isLoadingNextPage ||
            !hasMore ||
            nextCursor == null
        ) {
            return
        }

        viewModelScope.launch {
            isLoadingNextPage = true

            try {
                val page =
                    repository.search(
                        query = _query.value,
                        type = _selectedType.value,
                        after = nextCursor
                    )

                _results.value =
                    _results.value + page.items

                nextCursor = page.nextCursor
                hasMore = page.hasMore

            } catch (exception: Exception) {
                exception.printStackTrace()

                _errorMessage.value =
                    exception.message
                        ?: "Unable to load more results."
            } finally {
                isLoadingNextPage = false
            }
        }
    }
}