package com.tlabscloud.duni.data

import com.google.gson.annotations.SerializedName

data class JolocomTokenDto(
    @SerializedName("credential_request") val token: String
)
data class JolocomTokenRequestDto(
    @SerializedName("client_id") val clientId: String = "r2b-app",
    @SerializedName("redirect_uri") val redirectUri: String,
    @SerializedName("scope") val scope: String = "openid",
    @SerializedName("response_type") val responseType: String = "code",
    @SerializedName("credential_request") val credentialRequest: String,
    @SerializedName("credential_response") val credentialResponse: String
)
data class KeycloakCodePostTokenResponse(
    val code: String
)

data class JolocomPaymentResponseDto(
    val txHash: String?
)
