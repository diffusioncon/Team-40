package com.tlabscloud.r2b.dflow.data.repository

import com.tlabscloud.duni.BuildConfig
import com.tlabscloud.duni.data.JolocomTokenDto
import com.tlabscloud.duni.data.JolocomTokenRequestDto
import com.tlabscloud.duni.data.model.User
import com.tlabscloud.duni.data.remote.AuthApi
import com.tlabscloud.duni.data.room.dao.UserDao
import com.tlabscloud.duni.utils.AppConfigConst
import com.tlabscloud.duni.utils.Result
import com.tlabscloud.duni.utils.SPHelper
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URL

class UserRepository constructor(
    private val userDao: UserDao,
    private val pref: SPHelper,
    private val authApi: AuthApi
) {
    @Volatile
    private var authToken: String = ""

    fun get() = userDao.load()

    fun getUser() = userDao.getUser()

    fun create(user: User) = userDao.create(user)

    suspend fun wipeUserData() = withContext(IO) {
        userDao.deleteAll()
        val host = URL(BuildConfig.BACKEND_BASE_URL).host
        userDao.getUser()?.let {
            authApi.invalidateAccessToken(refreshToken = it.refreshToken, host = host)
        }
    }

    suspend fun getJolocomToken(): Result<JolocomTokenDto> = try {
        Result.Success(authApi.getJolocomToken(jolocomCallback = AppConfigConst.DFLOW_DEEP_LINK))
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun loginByDid(name: String, did: String): Boolean {
        val token = try {
            authApi.getAccessTokenForUser(did)
        } catch (e: Exception) {
            Timber.e(e)
            return false
        }
        create(User(accessToken = token.accessToken, refreshToken = token.refreshToken,
            name = name, sub = did))
        return true
    }

    fun concurrentRefreshAccessToken(currToken: String?): String? {
        synchronized(authToken) {
            if (currToken != authToken && !authToken.isEmpty()) {
                return authToken
            }
            val user = getUser() ?: return null

            return runBlocking {
                val token = try {
                    if (pref.isLoginMock())
                        authApi.refreshMockAccessToken(refresh_token = user.refreshToken)
                    else
                        authApi.refreshAccessToken(refresh_token = user.refreshToken)
                } catch (e: Exception) {
                    null
                }
                if (token != null) {
                    user.accessToken = token.accessToken
                    user.refreshToken = token.refreshToken
                    userDao.replace(user)
                    authToken = token.accessToken
                    token.accessToken
                } else {
                    wipeUserData()
                    null
                }
            }
        }
    }

    suspend fun postJolocomToken(credentialsRequest: String, credentialsResponse: String) = try {
        Result.Success(
            authApi.postJolocomToken(
                JolocomTokenRequestDto(
                    credentialRequest = credentialsRequest,
                    credentialResponse = credentialsResponse,
                    redirectUri = BuildConfig.BACKEND_BASE_URL
                )
            )
        )
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun getAccessToken(code: String) = try {
        Result.Success(authApi.getAccessToken(code = code, redirectUri = BuildConfig.BACKEND_BASE_URL))
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun getUserDid(): String = withContext(IO) {
        // user always presented in DB after login
        userDao.getUser()!!.sub
    }
}
