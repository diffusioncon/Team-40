package com.tlabscloud.duni.ui.login

import com.tlabscloud.duni.data.JolocomTokenDto
import com.auth0.android.jwt.JWT
import com.tlabscloud.duni.data.model.User
import com.tlabscloud.duni.data.remote.dto.UserInfoDto
import com.tlabscloud.duni.utils.Result
import com.tlabscloud.duni.utils.SPHelper
import com.tlabscloud.duni.utils.ScopedViewModel
import com.tlabscloud.r2b.dflow.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class LoginViewModel(private val userRepository: UserRepository,
                     private val preferences: SPHelper
) : ScopedViewModel() {

    private lateinit var credentialsRequest: String

    suspend fun buildJolocomLink(): Result<JolocomTokenDto> = withContext(Dispatchers.IO) {
        userRepository.getJolocomToken()
    }

    fun storeCredentialRequest(credentialsRequest: String) {
        this.credentialsRequest = credentialsRequest
    }


    suspend fun postToken(token: String): Result<String> = withContext(Dispatchers.IO) {
        if (credentialsRequest != null) {
            when (val postResult = userRepository.postJolocomToken(credentialsRequest!!, token)) {
                is Result.Error -> {
                    Timber.e(postResult.exception, "Post Jolocom token to Keycloak failed!")
                    Result.Error(RuntimeException("Post Jolocom token to Keycloak failed!"))
                }
                is Result.Success -> {
                    preferences.setLoginMock(false)
                    val code = postResult.data.code
                    when (val accessTokenResponse = userRepository.getAccessToken(code)) {
                        is Result.Success -> {
                            val accessToken = JWT(accessTokenResponse.data.accessToken)
                            createUser(
                                UserInfoDto(
                                    accessToken.getClaim("email").asString() ?: "",
                                    accessToken.getClaim("family_name").asString() ?: "",
                                    accessToken.getClaim("given_name").asString() ?: "",
                                    accessToken.getClaim("name").asString() ?: "",
                                    accessToken.subject ?: ""
                                ),
                                accessTokenResponse.data.accessToken,
                                accessTokenResponse.data.refreshToken
                            )
                            Result.Success("Got Access token")
                        }
                        is Result.Error -> {
                            Timber.e(accessTokenResponse.exception, "Access token request falied: ")
                            Result.Error(RuntimeException(accessTokenResponse.exception.localizedMessage))
                        }
                    }
                }
            }
        } else {
            Timber.e("Credentials Request is empty!")
            Result.Error(RuntimeException("Credentials Request is empty!"))
        }
    }

    private fun createUser(userInfo: UserInfoDto, token: String, refreshToken: String) {
        val usr = User(
            accessToken = token,
            refreshToken = refreshToken,
            name = userInfo.name,
            familyName = userInfo.familyName,
            givenName = userInfo.givenName,
            email = userInfo.email,
            sub = userInfo.sub,
            lastLogin = Date()
        )
        userRepository.create(usr)
    }
}
