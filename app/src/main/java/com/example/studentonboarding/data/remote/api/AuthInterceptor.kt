package com.example.studentonboarding.data.remote.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    // TODO: We will inject the actual token from DataStore/Room later.
    // For testing right now, we can hardcode a mock token or leave it blank.
    private var authToken: String? = null

    fun setToken(token: String) {
        authToken = token
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // If we have a token, attach it. If not, proceed without it (e.g., for login)
        val requestBuilder = originalRequest.newBuilder()
        authToken?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}