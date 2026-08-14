package edu.metrostate.ics342.mediatracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaDetail(
    val id: Int,
    val mediaType: MediaType,
    val title: String,
    val description: String? = null,
    val coverUrl: String? = null,
    val publishedYear: Int? = null,
    val runtimeMinutes: Int? = null,
    val pageCount: Int? = null,
    val seasonCount: Int? = null,
    val episodeCount: Int? = null,
    val averageRating: Double,
    val ratingCount: Int? = null,
    val genres: List<String> = emptyList(),
    val author: String? = null,
    val director: String? = null,
    val creator: String? = null
)