package edu.metrostate.ics342.mediatracker.ui.detail

import androidx.lifecycle.ViewModel
import edu.metrostate.ics342.mediatracker.data.FakeMediaRepository
import edu.metrostate.ics342.mediatracker.data.model.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaDetailViewModel : ViewModel() {

    private val _media = MutableStateFlow<Media?>(null)
    val media: StateFlow<Media?> = _media.asStateFlow()

    fun setMediaId(id: Int) {
        _media.value = FakeMediaRepository.mediaList.firstOrNull { media ->
            media.id == id
        } ?: FakeMediaRepository.mediaList.first()
    }
}