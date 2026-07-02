package edu.metrostate.ics342.mediatracker.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("grantType")
    val grantType: String = "password",

    val email: String,
    val password: String,

    @SerialName("clientId")
    val clientId: String,

    @SerialName("clientSecret")
    val clientSecret: String
)