package com.example.rocketreserver

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

private var instance: ApolloClient? = null

fun apolloClient(context: Context): ApolloClient {
    return instance ?: run {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(context))
            .build()

        return ApolloClient.Builder()
            .serverUrl("https://lo6tpfrstzb5fizs2i5jubwkb4.appsync-api.us-east-1.amazonaws.com/graphql")
            .okHttpClient(okHttpClient)
            .build()
            .also { instance = it }
    }
}

private class AuthorizationInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-API-Key", "da2-ux2shxaqibgq3lf3yu72fq5ft4")
            .build()

        return chain.proceed(request)
    }

}