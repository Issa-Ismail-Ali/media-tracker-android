package edu.metrostate.ics342.mediatracker.ui.search

import androidx.lifecycle.ViewModel
import edu.metrostate.ics342.mediatracker.data.fakeSearchResults
import edu.metrostate.ics342.mediatracker.data.model.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType.asStateFlow()

    private val _results = MutableStateFlow(fakeSearchResults.take(20))
    val results: StateFlow<List<Media>> = _results.asStateFlow()

    private var nextStart = 20
    private var hasMore = true
    private var isLoading = false

    fun onQueryChange(value: String) {
        _query.value = value
        resetFakeSearch()
    }

    fun onTypeSelected(type: String?) {
        _selectedType.value = type
        resetFakeSearch()
    }

    fun loadNextPageIfNeeded() {
        if (!hasMore || isLoading) return

        isLoading = true

        val filteredResults = getFilteredResults()
        val nextPage = filteredResults.drop(nextStart).take(20)

        _results.value = _results.value + nextPage

        nextStart += 20
        hasMore = nextStart < filteredResults.size
        isLoading = false
    }

    private fun resetFakeSearch() {
        nextStart = 20
        hasMore = true

        val filteredResults = getFilteredResults()
        _results.value = filteredResults.take(20)

        hasMore = filteredResults.size > 20
    }

    private fun getFilteredResults(): List<Media> {
        val queryText = _query.value.trim()
        val type = _selectedType.value

        return fakeSearchResults.filter { media ->
            val matchesQuery =
                queryText.isBlank() ||
                        media.title.contains(queryText, ignoreCase = true) ||
                        media.author?.contains(queryText, ignoreCase = true) == true ||
                        media.director?.contains(queryText, ignoreCase = true) == true ||
                        media.creator?.contains(queryText, ignoreCase = true) == true

            val matchesType =
                type == null || media.mediaType == type

            matchesQuery && matchesType
        }
    }
}