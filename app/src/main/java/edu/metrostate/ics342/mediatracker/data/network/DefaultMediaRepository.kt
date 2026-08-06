package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.SessionRepository
import edu.metrostate.ics342.mediatracker.data.model.DuplicateFavoriteException
import edu.metrostate.ics342.mediatracker.data.model.ErrorResponse
import edu.metrostate.ics342.mediatracker.data.model.Favorite
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.model.MediaDetail
import edu.metrostate.ics342.mediatracker.data.model.MediaNotFoundException
import edu.metrostate.ics342.mediatracker.data.model.Review
import kotlinx.serialization.decodeFromString
import retrofit2.Response

data class MediaPage(
    val items: List<Media>,
    val nextCursor: String?,
    val hasMore: Boolean
)

data class LibraryPage(
    val items: List<LibraryItem>,
    val nextCursor: String?,
    val hasMore: Boolean
)

class DefaultMediaRepository(
    sessionRepository: SessionRepository
) {
    private val api: MediaApiService =
        RetrofitInstance.createMediaApiService(
            sessionRepository
        )

    private fun parseErrorMessage(
        response: Response<*>
    ): String? {
        return try {
            response.errorBody()
                ?.string()
                ?.let { errorBody ->
                    RetrofitInstance.json
                        .decodeFromString<ErrorResponse>(
                            errorBody
                        )
                        .message
                }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun search(
        query: String,
        type: String?,
        after: String?
    ): MediaPage {
        val response = api.searchMedia(
            query = query.ifBlank { null },
            type = type?.ifBlank { null },
            after = after
        )

        if (!response.isSuccessful) {
            error(
                parseErrorMessage(response)
                    ?: "Failed to search media (${response.code()})"
            )
        }

        return MediaPage(
            items = response.body().orEmpty(),
            nextCursor =
                response.headers()["X-Next-Cursor"],
            hasMore =
                response.headers()["X-Has-More"] == "true"
        )
    }

    suspend fun getMediaDetail(
        mediaId: Int
    ): MediaDetail {
        val response =
            api.getMediaDetail(mediaId)

        if (response.code() == 404) {
            throw MediaNotFoundException(
                parseErrorMessage(response)
                    ?: "Media item not found."
            )
        }

        if (!response.isSuccessful) {
            error(
                parseErrorMessage(response)
                    ?: "Failed to load media (${response.code()})"
            )
        }

        return response.body()
            ?: error(
                "Empty media detail response for ID $mediaId"
            )
    }

    suspend fun getLibraryItem(
        mediaId: Int
    ): LibraryItem? {
        val response =
            api.getLibraryItem(mediaId)

        // Normal result: it has not been added.
        if (response.code() == 404) {
            return null
        }

        if (!response.isSuccessful) {
            error(
                parseErrorMessage(response)
                    ?: "Failed to check library (${response.code()})"
            )
        }

        return response.body()
    }

    suspend fun addToLibrary(
        mediaId: Int,
        status: LibraryStatus
    ): LibraryItem {
        val response =
            api.addToLibrary(
                AddToLibraryRequest(
                    mediaId = mediaId,
                    status = status
                )
            )

        if (!response.isSuccessful) {
            error(
                parseErrorMessage(response)
                    ?: "Failed to add to library (${response.code()})"
            )
        }

        return response.body()
            ?: error(
                "Empty response adding media ID $mediaId"
            )
    }

    suspend fun getLibrary(
        status: LibraryStatus?,
        after: String? = null
    ): LibraryPage {
        val response =
            api.getLibrary(
                status = status?.toApiString(),
                after = after
            )

        if (!response.isSuccessful) {
            error(
                parseErrorMessage(response)
                    ?: "Failed to load library (${response.code()})"
            )
        }

        return LibraryPage(
            items = response.body().orEmpty(),
            nextCursor =
                response.headers()["X-Next-Cursor"],
            hasMore =
                response.headers()["X-Has-More"] == "true"
        )
    }

    suspend fun getFavorite(
        mediaId: Int
    ): Favorite? {
        val response =
            api.getFavorite(mediaId)

        // Normal result: it has not been saved.
        if (response.code() == 404) {
            return null
        }

        if (!response.isSuccessful) {
            error(
                parseErrorMessage(response)
                    ?: "Failed to check favorite (${response.code()})"
            )
        }

        return response.body()
    }

    suspend fun addFavorite(
        mediaId: Int
    ): Favorite {
        val response =
            api.addFavorite(
                AddToFavoritesRequest(
                    mediaId = mediaId
                )
            )

        if (response.code() == 409) {
            throw DuplicateFavoriteException()
        }

        if (!response.isSuccessful) {
            error(
                parseErrorMessage(response)
                    ?: "Failed to save favorite (${response.code()})"
            )
        }

        return response.body()
            ?: error(
                "Empty response saving media ID $mediaId"
            )
    }

    suspend fun getReviews(
        mediaId: Int
    ): List<Review> {
        val response =
            api.getReviews(mediaId)

        if (!response.isSuccessful) {
            return emptyList()
        }

        return response.body().orEmpty()
    }

    suspend fun updateLibraryStatus(
        mediaId: Int,
        status: LibraryStatus
    ): LibraryItem {
        val response = api.updateLibraryStatus(
            mediaId = mediaId,
            body = UpdateLibraryStatusRequest(
                status = status
            )
        )

        if (!response.isSuccessful) {
            error(
                parseErrorMessage(response)
                    ?: "Failed to update library status (${response.code()})"
            )
        }

        return response.body()
            ?: error("Empty response updating media ID $mediaId")
    }

    suspend fun removeFromLibrary(
        mediaId: Int
    ) {
        val response =
            api.removeFromLibrary(mediaId)

        if (!response.isSuccessful && response.code() != 404) {
            error(
                parseErrorMessage(response)
                    ?: "Failed to remove library item (${response.code()})"
            )
        }
    }

    suspend fun removeFavorite(
        mediaId: Int
    ) {
        val response =
            api.removeFavorite(mediaId)

        if (!response.isSuccessful && response.code() != 404) {
            error(
                parseErrorMessage(response)
                    ?: "Failed to remove favorite (${response.code()})"
            )
        }
    }
}