package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.model.MediaDetail
import edu.metrostate.ics342.mediatracker.data.model.Review
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {

    @POST("users")
    suspend fun createUser(
        @Body body: RegisterRequest
    ): Response<Unit>

    @POST("tokens")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<AuthResponse>

    @GET("media")
    suspend fun searchMedia(
        @Query("query") query: String,
        @Query("type") type: String? = null,
        @Query("start") start: Int = 0
    ): Response<List<Media>>

    @GET("media/{id}")
    suspend fun getMediaDetail(
        @Path("id") mediaId: Int
    ): Response<MediaDetail>

    @GET("library/{mediaId}")
    suspend fun getLibraryItem(
        @Path("mediaId") mediaId: Int
    ): Response<LibraryItem>

    @POST("library")
    suspend fun addToLibrary(
        @Body body: AddToLibraryRequest
    ): Response<LibraryItem>

    @GET("reviews")
    suspend fun getReviews(
        @Query("mediaId") mediaId: Int
    ): Response<List<Review>>
}