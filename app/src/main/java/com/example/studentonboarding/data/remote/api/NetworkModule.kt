package com.example.studentonboarding.data.remote.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    // IMPORTANT: When running the Node.js server locally on your machine,
    // the Android Emulator cannot reach it via "localhost".
    // You MUST use "10.0.2.2" which is the emulator's alias for your host machine.
    // If testing on a physical device, use your computer's actual IPv4 address on the same Wi-Fi network.
    private const val BASE_URL = "https://perspectively-postpuberty-carry.ngrok-free.dev/api/v1/"

    val authInterceptor = AuthInterceptor()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // This will print the exact JSON request and response bodies to your Logcat.
        // It is incredibly helpful for debugging API crashes.
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()) // Converts JSON to our Data Classes
        .build()

    // This is the actual usable API instance that we will pass to the Repository
    val onboardingApi: OnboardingApi = retrofit.create(OnboardingApi::class.java)
}