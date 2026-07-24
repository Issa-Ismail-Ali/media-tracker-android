package edu.metrostate.ics342.mediatracker.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.SessionRepository
import edu.metrostate.ics342.mediatracker.data.datastore.DefaultSessionRepository
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.model.MediaDetail
import edu.metrostate.ics342.mediatracker.data.network.AddLibraryRequest
import edu.metrostate.ics342.mediatracker.data.network.MediaApiService
import edu.metrostate.ics342.mediatracker.data.network.RetrofitInstance
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
        val isAddingToLibrary: Boolean = false
    ) : MediaDetailUiState
}

class MediaDetailViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val sessionRepository: SessionRepository =
        DefaultSessionRepository(
            application.applicationContext
        )

    private val api: MediaApiService =
        RetrofitInstance.createMediaApiService(
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
                    "The media ID is missing."
                )
            return
        }

        viewModelScope.launch {
            _uiState.value =
                MediaDetailUiState.Loading

            try {
                val accessToken =
                    sessionRepository.getAccessToken()

                if (accessToken.isNullOrBlank()) {
                    _uiState.value =
                        MediaDetailUiState.Error(
                            "Your login session is missing. Please log in again."
                        )
                    return@launch
                }

                val mediaResponse =
                    api.getMediaDetail(mediaId)

                if (!mediaResponse.isSuccessful) {
                    val errorBody =
                        mediaResponse.errorBody()?.string()

                    val message =
                        when (mediaResponse.code()) {
                            401 -> {
                                "Your login session is missing or expired."
                            }

                            403 -> {
                                "You are not allowed to view this media."
                            }

                            404 -> {
                                "Media not found. ID: $mediaId"
                            }

                            else -> {
                                errorBody
                                    ?: "Unable to load media details."
                            }
                        }

                    _uiState.value =
                        MediaDetailUiState.Error(message)

                    return@launch
                }

                val media =
                    mediaResponse.body()

                if (media == null) {
                    _uiState.value =
                        MediaDetailUiState.Error(
                            "Media details were empty."
                        )
                    return@launch
                }

                val libraryResponse =
                    api.getLibraryItem(mediaId)

                val isInLibrary =
                    when {
                        libraryResponse.isSuccessful -> {
                            true
                        }

                        libraryResponse.code() == 404 -> {
                            false
                        }

                        libraryResponse.code() == 401 -> {
                            _uiState.value =
                                MediaDetailUiState.Error(
                                    "Your login session is missing or expired."
                                )
                            return@launch
                        }

                        else -> {
                            false
                        }
                    }

                _uiState.value =
                    MediaDetailUiState.Success(
                        media = media,
                        isInLibrary = isInLibrary
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
        val currentState =
            _uiState.value as? MediaDetailUiState.Success
                ?: return

        if (
            currentState.isInLibrary ||
            currentState.isAddingToLibrary ||
            currentMediaId <= 0
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.value =
                currentState.copy(
                    isAddingToLibrary = true
                )

            try {
                val response =
                    api.addToLibrary(
                        AddLibraryRequest(
                            mediaId = currentMediaId,
                            status = LibraryStatus.WANT_TO
                        )
                    )

                if (response.isSuccessful) {
                    _uiState.value =
                        currentState.copy(
                            isInLibrary = true,
                            isAddingToLibrary = false
                        )
                } else {
                    _uiState.value =
                        currentState.copy(
                            isAddingToLibrary = false
                        )
                }
            } catch (exception: Exception) {
                exception.printStackTrace()

                _uiState.value =
                    currentState.copy(
                        isAddingToLibrary = false
                    )
            }
        }
    }
}