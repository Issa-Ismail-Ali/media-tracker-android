package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.model.Favorite
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.model.MediaDetail
import edu.metrostate.ics342.mediatracker.data.model.Review
import edu.metrostate.ics342.mediatracker.data.model.Quote
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.DELETE
import retrofit2.http.PUT

interface MediaApiService {

    @GET("media")
    suspend fun searchMedia(
        @Query("query") query: String? = null,
        @Query("type") type: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("after") after: String? = null
    ): Response<List<Media>>

    @GET("media/{id}")
    suspend fun getMediaDetail(
        @Path("id") mediaId: Int
    ): Response<MediaDetail>

    @GET("library")
    suspend fun getLibrary(
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("after") after: String? = null
    ): Response<List<LibraryItem>>

    @GET("library/{mediaId}")
    suspend fun getLibraryItem(
        @Path("mediaId") mediaId: Int
    ): Response<LibraryItem>

    @POST("library")
    suspend fun addToLibrary(
        @Body body: AddToLibraryRequest
    ): Response<LibraryItem>

    @GET("favorites/{mediaId}")
    suspend fun getFavorite(
        @Path("mediaId") mediaId: Int
    ): Response<Favorite>

    @POST("favorites")
    suspend fun addFavorite(
        @Body body: AddToFavoritesRequest
    ): Response<Favorite>

    @GET("reviews")
    suspend fun getReviews(
        @Query("mediaId") mediaId: Int
    ): Response<List<Review>>

    @GET("quotes")
    suspend fun getQuotes(
        @Query("public") publicOnly: Boolean? = null,
        @Query("limit") limit: Int = 20,
        @Query("after") after: String? = null
    ): Response<List<Quote>>

    @POST("quotes")
    suspend fun addQuote(
        @Body body: AddQuoteRequest
    ): Response<Quote>

    @PUT("library/{mediaId}")
    suspend fun updateLibraryStatus(
        @Path("mediaId") mediaId: Int,
        @Body body: UpdateLibraryStatusRequest
    ): Response<LibraryItem>

    @DELETE("library/{mediaId}")
    suspend fun removeFromLibrary(
        @Path("mediaId") mediaId: Int
    ): Response<Unit>

    @DELETE("favorites/{mediaId}")
    suspend fun removeFavorite(
        @Path("mediaId") mediaId: Int
    ): Response<Unit>
}