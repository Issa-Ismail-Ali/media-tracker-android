package edu.metrostate.ics342.mediatracker.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.datastore.DefaultSessionRepository
import edu.metrostate.ics342.mediatracker.data.model.DuplicateFavoriteException
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.model.MediaDetail
import edu.metrostate.ics342.mediatracker.data.model.MediaNotFoundException
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MediaDetailUiState {

    data object Loading : MediaDetailUiState

    data class Error(
        val message: String
    ) : MediaDetailUiState

    data class Success(
        val media: MediaDetail,
        val isInLibrary: Boolean,
        val isFavorite: Boolean,
        val isAddingToLibrary: Boolean = false,
        val isAddingFavorite: Boolean = false,

        // Quote state
        val isSavingQuote: Boolean = false,
        val quoteMessage: String? = null
    ) : MediaDetailUiState
}

class MediaDetailViewModel(
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

    private val _uiState =
        MutableStateFlow<MediaDetailUiState>(
            MediaDetailUiState.Loading
        )

    val uiState: StateFlow<MediaDetailUiState> =
        _uiState.asStateFlow()

    private var currentMediaId: Int = -1

    fun loadMedia(mediaId: Int) {
        currentMediaId = mediaId

        if (mediaId <= 0) {
            _uiState.value =
                MediaDetailUiState.Error(
                    message = "The media ID is missing."
                )
            return
        }

        viewModelScope.launch {
            _uiState.value =
                MediaDetailUiState.Loading

            try {
                val media =
                    repository.getMediaDetail(mediaId)

                val libraryItem =
                    runCatching {
                        repository.getLibraryItem(mediaId)
                    }.getOrNull()

                val favorite =
                    runCatching {
                        repository.getFavorite(mediaId)
                    }.getOrNull()

                _uiState.value =
                    MediaDetailUiState.Success(
                        media = media,
                        isInLibrary = libraryItem != null,
                        isFavorite = favorite != null
                    )

            } catch (exception: MediaNotFoundException) {
                _uiState.value =
                    MediaDetailUiState.Error(
                        message = exception.message
                            ?: "Media item not found."
                    )

            } catch (exception: Exception) {
                exception.printStackTrace()

                _uiState.value =
                    MediaDetailUiState.Error(
                        message = exception.message
                            ?: "Unable to load media details."
                    )
            }
        }
    }

    fun retry() {
        if (currentMediaId > 0) {
            loadMedia(currentMediaId)
        }
    }

    fun addToLibrary() {
        val current =
            _uiState.value as? MediaDetailUiState.Success
                ?: return

        if (
            current.isInLibrary ||
            current.isAddingToLibrary ||
            currentMediaId <= 0
        ) {
            return
        }

        _uiState.value =
            current.copy(
                isInLibrary = true,
                isAddingToLibrary = true
            )

        viewModelScope.launch {
            try {
                repository.addToLibrary(
                    mediaId = currentMediaId,
                    status = LibraryStatus.WANT_TO
                )

                val latest =
                    _uiState.value as? MediaDetailUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        isInLibrary = true,
                        isAddingToLibrary = false
                    )

            } catch (exception: Exception) {
                exception.printStackTrace()

                val latest =
                    _uiState.value as? MediaDetailUiState.Success
                        ?: return@launch

                val alreadyAdded =
                    exception.message
                        ?.contains("409") == true ||
                            exception.message
                                ?.contains(
                                    "already",
                                    ignoreCase = true
                                ) == true

                _uiState.value =
                    if (alreadyAdded) {
                        latest.copy(
                            isInLibrary = true,
                            isAddingToLibrary = false
                        )
                    } else {
                        latest.copy(
                            isInLibrary = false,
                            isAddingToLibrary = false
                        )
                    }
            }
        }
    }

    fun addFavorite() {
        val current =
            _uiState.value as? MediaDetailUiState.Success
                ?: return

        if (
            current.isAddingFavorite ||
            currentMediaId <= 0
        ) {
            return
        }

        val wasFavorite =
            current.isFavorite

        _uiState.value =
            current.copy(
                isFavorite = !wasFavorite,
                isAddingFavorite = true
            )

        viewModelScope.launch {
            try {
                if (wasFavorite) {
                    repository.removeFavorite(
                        mediaId = currentMediaId
                    )
                } else {
                    repository.addFavorite(
                        mediaId = currentMediaId
                    )
                }

                val latest =
                    _uiState.value as? MediaDetailUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        isAddingFavorite = false
                    )

            } catch (
                exception: DuplicateFavoriteException
            ) {
                val latest =
                    _uiState.value as? MediaDetailUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        isFavorite = true,
                        isAddingFavorite = false
                    )

            } catch (exception: Exception) {
                exception.printStackTrace()

                val latest =
                    _uiState.value as? MediaDetailUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        isFavorite = wasFavorite,
                        isAddingFavorite = false
                    )
            }
        }
    }

    fun saveQuote(
        quoteText: String,
        pageNumberText: String,
        isPublic: Boolean
    ) {
        val current =
            _uiState.value as? MediaDetailUiState.Success
                ?: return

        if (currentMediaId <= 0) {
            return
        }

        val cleanedText =
            quoteText.trim()

        if (cleanedText.isBlank()) {
            _uiState.value =
                current.copy(
                    quoteMessage =
                        "Quote text is required."
                )
            return
        }

        if (cleanedText.length > 500) {
            _uiState.value =
                current.copy(
                    quoteMessage =
                        "Quote must be 500 characters or less."
                )
            return
        }

        val pageNumber =
            if (pageNumberText.isBlank()) {
                null
            } else {
                pageNumberText.toIntOrNull()
            }

        if (
            pageNumberText.isNotBlank() &&
            pageNumber == null
        ) {
            _uiState.value =
                current.copy(
                    quoteMessage =
                        "Page number must be a number."
                )
            return
        }

        _uiState.value =
            current.copy(
                isSavingQuote = true,
                quoteMessage = null
            )

        viewModelScope.launch {
            try {
                repository.addQuote(
                    mediaId = currentMediaId,
                    quoteText = cleanedText,
                    pageNumber = pageNumber,
                    isPublic = isPublic
                )

                val latest =
                    _uiState.value as? MediaDetailUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        isSavingQuote = false,
                        quoteMessage =
                            "Quote saved."
                    )

            } catch (exception: Exception) {
                exception.printStackTrace()

                val latest =
                    _uiState.value as? MediaDetailUiState.Success
                        ?: return@launch

                _uiState.value =
                    latest.copy(
                        isSavingQuote = false,
                        quoteMessage =
                            exception.message
                                ?: "Unable to save quote."
                    )
            }
        }
    }
}