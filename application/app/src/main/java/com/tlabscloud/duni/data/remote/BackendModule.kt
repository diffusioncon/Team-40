package com.tlabscloud.duni.data.remote

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.tlabscloud.duni.BuildConfig
import com.tlabscloud.duni.data.room.dao.UserDao
import com.tlabscloud.duni.utils.AppConfigConst
import com.tlabscloud.r2b.dflow.data.repository.UserRepository
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.Date
import java.util.concurrent.TimeUnit

val backendModule = Kodein.Module("BackendRetrofit") {
    bind<Retrofit>("backendRetrofit") with singleton {
        val clientBuilder = createClient()
        val userDao: UserDao = instance()

        val authInterceptor = Interceptor { chain ->
            val req = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${userDao.getUser()?.accessToken ?: ""}")
            chain.proceed(req.build())
        }
        clientBuilder.addInterceptor(authInterceptor)
        clientBuilder.authenticator(instance())

        Retrofit.Builder()
            .client(clientBuilder.build())
            .baseUrl(BuildConfig.BACKEND_BASE_URL)
            .addConverterFactory(createConverter())
            .build()
    }

    bind<Retrofit>("authRetrofit") with singleton {
        val client = createClient().build()
        Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.BACKEND_BASE_URL)
            .addConverterFactory(createConverter())
            .build()
    }

    bind<Retrofit>("courseRetrofit") with singleton {
        val client = createClient().build()
        Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.STAX_BASE_URL)
            .addConverterFactory(createConverter())
            .build()
    }

    bind<BackendAuthenticator>() with singleton {
        BackendAuthenticator(instance())
    }
}

private fun createClient(): OkHttpClient.Builder {
    val clientBuilder = OkHttpClient.Builder()
        .connectTimeout(AppConfigConst.HTTP_CLIENT_TIMEOUT_SEC, TimeUnit.SECONDS)
        .readTimeout(AppConfigConst.HTTP_CLIENT_TIMEOUT_SEC, TimeUnit.SECONDS)
        .writeTimeout(AppConfigConst.HTTP_CLIENT_TIMEOUT_SEC, TimeUnit.SECONDS)

    if (BuildConfig.DEBUG) {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(logInterceptor)
    }

    return clientBuilder
}

private fun createConverter(): GsonConverterFactory {
    val gsonBuilder = GsonBuilder()

    gsonBuilder.registerTypeAdapter(Date::class.java, object : JsonDeserializer<Date> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Date {
            return Date(json.asJsonPrimitive.asLong)
        }
    })
    return GsonConverterFactory.create(gsonBuilder.create())
}

private class BackendAuthenticator(private val userRepository: UserRepository) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        var req: Request? = null
        val prevToken = response.request().header("Authorization")?.removePrefix("Bearer ")
        val token = userRepository.concurrentRefreshAccessToken(prevToken)
        if (token != null) {
            req = response.request().newBuilder().removeHeader("Authorization")
                .addHeader("Authorization", "Bearer $token").build()
        }
        return req
    }
}
