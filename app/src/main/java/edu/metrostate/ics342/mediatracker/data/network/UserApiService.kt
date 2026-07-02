package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.model.Media
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
}