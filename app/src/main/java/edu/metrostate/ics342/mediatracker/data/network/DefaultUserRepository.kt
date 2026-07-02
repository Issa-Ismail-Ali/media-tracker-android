package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.LoginResult
import edu.metrostate.ics342.mediatracker.data.RegisterResult
import edu.metrostate.ics342.mediatracker.data.UserRepository
import java.io.IOException

class DefaultUserRepository(
    private val service: UserApiService = RetrofitInstance.userApiService
) : UserRepository {

    override suspend fun register(
        email: String,
        password: String,
        username: String,
        displayName: String
    ): RegisterResult {
        return try {
            val response = service.createUser(
                RegisterRequest(
                    email = email,
                    password = password,
                    username = username,
                    displayName = displayName,
                    clientId = ApiConstants.CLIENT_ID,
                    clientSecret = ApiConstants.CLIENT_SECRET
                )
            )

            when (response.code()) {
                201 -> RegisterResult.Success
                409 -> RegisterResult.Conflict
                else -> {
                    println("REGISTER ERROR CODE: ${response.code()}")
                    println("REGISTER ERROR BODY: ${response.errorBody()?.string()}")
                    RegisterResult.UnknownError
                }
            }

        } catch (e: IOException) {
            println("REGISTER NETWORK ERROR: ${e.message}")
            RegisterResult.NetworkError
        } catch (e: Exception) {
            println("REGISTER EXCEPTION: ${e.message}")
            RegisterResult.UnknownError
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): LoginResult {
        return try {

            val request = LoginRequest(
                grantType = "password",
                email = email,
                password = password,
                clientId = ApiConstants.CLIENT_ID,
                clientSecret = ApiConstants.CLIENT_SECRET
            )

            println("LOGIN REQUEST = $request")

            val response = service.login(request)

            when (response.code()) {

                200 -> {
                    val body = response.body()

                    if (body != null) {
                        LoginResult.Success(
                            accessToken = body.accessToken,
                            refreshToken = body.refreshToken,
                            user = body.user
                        )
                    } else {
                        println("LOGIN ERROR: Response body is null")
                        LoginResult.UnknownError
                    }
                }

                401 -> LoginResult.InvalidCredentials

                else -> {
                    println("LOGIN ERROR CODE: ${response.code()}")
                    println("LOGIN ERROR BODY: ${response.errorBody()?.string()}")
                    LoginResult.UnknownError
                }
            }

        } catch (e: IOException) {
            println("LOGIN NETWORK ERROR: ${e.message}")
            LoginResult.NetworkError
        } catch (e: Exception) {
            println("LOGIN EXCEPTION: ${e.message}")
            LoginResult.UnknownError
        }
    }
}