package edu.metrostate.ics342.mediatracker.data.model

import androidx.annotation.StringRes
import edu.metrostate.ics342.mediatracker.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LibraryItem(
    @SerialName("userId")
    val userId: String,

    @SerialName("mediaId")
    val mediaId: Int,

    @SerialName("status")
    val status: LibraryStatus,

    @SerialName("addedAt")
    val addedAt: String,

    @SerialName("updatedAt")
    val updatedAt: String,

    @SerialName("media")
    val media: Media
)

@Serializable
enum class LibraryStatus(
    @StringRes val labelRes: Int
) {
    @SerialName("want_to")
    WANT_TO(R.string.status_want_to),

    @SerialName("in_progress")
    IN_PROGRESS(R.string.status_in_progress),

    @SerialName("finished")
    FINISHED(R.string.status_finished);

    fun toApiString(): String {
        return when (this) {
            WANT_TO -> "want_to"
            IN_PROGRESS -> "in_progress"
            FINISHED -> "finished"
        }
    }

    companion object {
        fun fromString(
            value: String
        ): LibraryStatus {
            return when (value) {
                "want_to" -> WANT_TO
                "in_progress" -> IN_PROGRESS
                "finished" -> FINISHED
                else -> WANT_TO
            }
        }
    }
}