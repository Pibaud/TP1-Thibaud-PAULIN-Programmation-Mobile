package com.example.trainapp.model

import okhttp3.Interceptor
import okhttp3.Response
import android.util.Base64

class AuthInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val credentials = "$apiKey:"
        val basicAuth = "Basic " + Base64.encodeToString(
            credentials.toByteArray(),
            Base64.NO_WRAP
        )

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", basicAuth)
            .build()

        return chain.proceed(newRequest)
    }
}