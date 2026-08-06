package edu.metrostate.ics342.mediatracker.ui.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.datastore.DefaultSessionRepository
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LibraryUiState {

    data object Loading : LibraryUiState

    data class Error(
        val message: String
    ) : LibraryUiState

    data class Success(
        val items: List<LibraryItem>,
        val actionError: String? = null
    ) : LibraryUiState
}

class LibraryViewModel(
    application: Application,
    private val repository: DefaultMediaRepository
) : AndroidViewModel(application) {

    constructor(
        application: Application
    ) : this(
        application = application,
        repository = DefaultMediaRepository(
            DefaultSessionRepository(
                application.applicationContext
            )
        )
    )

    private val _uiState =
        MutableStateFlow<LibraryUiState>(
            LibraryUiState.Loading
        )

    val uiState: StateFlow<LibraryUiState> =
        _uiState.asStateFlow()

    private val _selectedStatus =
        MutableStateFlow(
            LibraryStatus.WANT_TO
        )

    val selectedStatus: StateFlow<LibraryStatus> =
        _selectedStatus.asStateFlow()

    init {
        loadLibrary(
            LibraryStatus.WANT_TO
        )
    }

    fun loadLibrary(
        status: LibraryStatus
    ) {
        _selectedStatus.value = status
        _uiState.value =
            LibraryUiState.Loading

        viewModelScope.launch {
            try {
                val page =
                    repository.getLibrary(
                        status = status
                    )

                _uiState.value =
                    LibraryUiState.Success(
                        items = page.items
                    )
            } catch (exception: Exception) {
                _uiState.value =
                    LibraryUiState.Error(
                        exception.message
                            ?: "Unable to load your library."
                    )
            }
        }
    }

    fun retry() {
        loadLibrary(
            _selectedStatus.value
        )
    }

    fun removeItem(
        mediaId: Int
    ) {
        val current =
            _uiState.value as? LibraryUiState.Success
                ?: return

        val backup =
            current.items.firstOrNull {
                it.mediaId == mediaId
            } ?: return

        // Optimistic update: remove first.
        _uiState.value =
            current.copy(
                items = current.items.filter {
                    it.mediaId != mediaId
                },
                actionError = null
            )

        viewModelScope.launch {
            try {
                repository.removeFromLibrary(
                    mediaId
                )
            } catch (exception: Exception) {
                val latest =
                    _uiState.value as? LibraryUiState.Success
                        ?: return@launch

                // Roll back if the network request fails.
                _uiState.value =
                    latest.copy(
                        items = latest.items + backup,
                        actionError =
                            "Couldn't remove item. Try again."
                    )
            }
        }
    }

    fun updateStatus(
        mediaId: Int,
        newStatus: LibraryStatus
    ) {
        val current =
            _uiState.value as? LibraryUiState.Success
                ?: return

        val originalItem =
            current.items.firstOrNull {
                it.mediaId == mediaId
            } ?: return

        // Remove immediately from the current status tab.
        _uiState.value =
            current.copy(
                items = current.items.filter {
                    it.mediaId != mediaId
                },
                actionError = null
            )

        viewModelScope.launch {
            try {
                repository.updateLibraryStatus(
                    mediaId = mediaId,
                    status = newStatus
                )
            } catch (exception: Exception) {
                val latest =
                    _uiState.value as? LibraryUiState.Success
                        ?: return@launch

                // Restore the item if the network call fails.
                _uiState.value =
                    latest.copy(
                        items = latest.items + originalItem,
                        actionError =
                            "Couldn't change status. Try again."
                    )
            }
        }
    }

    fun clearActionError() {
        val current =
            _uiState.value as? LibraryUiState.Success
                ?: return

        _uiState.value =
            current.copy(
                actionError = null
            )
    }
}