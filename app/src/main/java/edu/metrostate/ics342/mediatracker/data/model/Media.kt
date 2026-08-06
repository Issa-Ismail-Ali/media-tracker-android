package edu.metrostate.ics342.mediatracker.data.model

import android.content.Context
import edu.metrostate.ics342.mediatracker.R
import kotlinx.serialization.Serializable

@Serializable
data class Media(
    val id: Int,
    val mediaType: String,      // "book", "movie", or "show"
    val title: String,
    val author: String? = null,
    val director: String? = null,
    val creator: String? = null,
    val network: String? = null,
    val coverUrl: String? = null,
    val publishedYear: Int? = null,
    val averageRating: Float = 0f,
    val ratingCount: Int = 0,
    val genres: List<String> = emptyList()
)

/** Returns a human-readable credit line appropriate for the media type. */
fun Media.creatorCredit(context: Context): String = when (mediaType) {
    "book"  -> author ?: context.getString(R.string.media_unknown_author)
    "movie" -> director ?: context.getString(R.string.media_unknown_director)
    "show"  -> creator ?: context.getString(R.string.media_unknown_creator)
    else -> ""
}