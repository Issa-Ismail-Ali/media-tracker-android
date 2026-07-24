package edu.metrostate.ics342.mediatracker.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import edu.metrostate.ics342.mediatracker.data.SessionRepository
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitInstance {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    /*
     * This client is used for login and registration.
     * Those requests do not require an access token.
     */
    private val publicClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    private val publicRetrofit =
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(publicClient)
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json; charset=utf-8"
                        .toMediaType()
                )
            )
            .build()

    val userApiService: UserApiService =
        publicRetrofit.create(
            UserApiService::class.java
        )

    /*
     * This creates a MediaApiService that includes the
     * logged-in user's access token.
     */
    fun createMediaApiService(
        sessionRepository: SessionRepository
    ): MediaApiService {

        val authenticatedClient =
            OkHttpClient.Builder()
                .addInterceptor { chain ->

                    val accessToken =
                        runBlocking {
                            sessionRepository.getAccessToken()
                        }

                    val requestBuilder =
                        chain.request()
                            .newBuilder()

                    if (!accessToken.isNullOrBlank()) {
                        requestBuilder.header(
                            "Authorization",
                            "Bearer $accessToken"
                        )
                    }

                    chain.proceed(
                        requestBuilder.build()
                    )
                }
                .addInterceptor(loggingInterceptor)
                .build()

        val authenticatedRetrofit =
            Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(authenticatedClient)
                .addConverterFactory(
                    json.asConverterFactory(
                        "application/json; charset=utf-8"
                            .toMediaType()
                    )
                )
                .build()

        return authenticatedRetrofit.create(
            MediaApiService::class.java
        )
    }
}