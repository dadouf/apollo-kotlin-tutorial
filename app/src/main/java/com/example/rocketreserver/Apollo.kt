package com.example.rocketreserver

import android.content.Context
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

private var instance: AWSAppSyncClient? = null

fun apolloClient(context: Context): AWSAppSyncClient {
    return instance ?: run {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(context))
            .build()

        return AWSAppSyncClient.builder()
            .context(context)
            .awsConfiguration(AWSConfiguration(context)) // see res/raw/awsconfiguration.json
            .okHttpClient(okHttpClient)
            .build()
            .also { instance = it }
    }
}

private class AuthorizationInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", User.getToken(context) ?: "")
            .build()

        return chain.proceed(request)
    }

}