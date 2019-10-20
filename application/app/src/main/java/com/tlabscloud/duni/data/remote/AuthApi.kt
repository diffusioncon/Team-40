package com.tlabscloud.duni.data.remote

import com.tlabscloud.duni.BuildConfig
import com.tlabscloud.duni.data.JolocomTokenDto
import com.tlabscloud.duni.data.JolocomTokenRequestDto
import com.tlabscloud.duni.data.KeycloakCodePostTokenResponse
import com.tlabscloud.duni.data.remote.dto.AccessTokenResponseDto
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

interface AuthApi {
    @GET(
        "/auth/realms/stax/broker/jolocom/endpoint/auth"
    )
    suspend fun getJolocomToken(
        @Query("state") state: String = "123",
        @Query("client_id") clientId: String = "r2b-app",
        @Query("redirect_uri") jolocomCallback: String
    ): JolocomTokenDto

    @POST(
        "/auth/realms/stax/broker/jolocom/endpoint/auth"
    )
    suspend fun postJolocomToken(@Body requestBody: JolocomTokenRequestDto): KeycloakCodePostTokenResponse

    @FormUrlEncoded
    @POST(
        "/auth/realms/stax/protocol/openid-connect/token"
    )
    suspend fun getAccessToken(
        @Field("code") code: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("client_id") clientId: String = "r2b-app",
        @Field("redirect_uri") redirectUri: String
    ): AccessTokenResponseDto

    @FormUrlEncoded
    @POST(
        "/auth/realms/stax/protocol/openid-connect/token"
    )
    suspend fun getAccessTokenForUser(
        @Field("username") username: String,
        @Field("password") password: String = BuildConfig.KEYCLOAK_USER_PASS,
        @Field("client_secret") clientSecret: String = BuildConfig.KEYCLOAK_CLIENT_SECRET,
        @Field("grant_type") grantType: String = "password",
        @Field("client_id") clientId: String = BuildConfig.KEYCLOAK_CLIENT
    ): AccessTokenResponseDto

    @FormUrlEncoded
    @POST(
        "/auth/realms/stax/protocol/openid-connect/logout"
    )
    suspend fun invalidateAccessToken(
        @Field("client_id") clientId: String = "r2b-app",
        @Field("refresh_token") refreshToken: String,
        @Field("Host") host: String
    ): Response<Void>

    @FormUrlEncoded
    @POST(
        "/auth/realms/stax/protocol/openid-connect/token"
    )
    suspend fun refreshAccessToken(
        @Field("refresh_token") refresh_token: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("client_id") clientId: String = "r2b-app"
    ): AccessTokenResponseDto

    @FormUrlEncoded
    @POST(
        "/auth/realms/stax/protocol/openid-connect/token"
    )
    suspend fun refreshMockAccessToken(
        @Field("refresh_token") refresh_token: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("client_secret") clientSecret: String = BuildConfig.KEYCLOAK_CLIENT_SECRET,
        @Field("client_id") clientId: String = BuildConfig.KEYCLOAK_CLIENT
    ): AccessTokenResponseDto
}

val authApiModule = Kodein.Module("AuthApi") {
    bind<AuthApi>() with singleton {
        val retrofit: Retrofit = instance("authRetrofit")
        retrofit.create(AuthApi::class.java)
    }
}
