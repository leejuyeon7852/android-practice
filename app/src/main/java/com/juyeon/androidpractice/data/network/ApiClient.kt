package com.juyeon.androidpractice.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 에뮬레이터에서 호스트 PC localhost를 가리키는 주소
    private const val BASE_URL = "http://10.0.2.2:8000/"

    fun imageUrl(path: String?): String? {
        if (path == null) return null
        return if (path.startsWith("http")) path else BASE_URL + path.trimStart('/')
    }

    private var tokenManager: TokenManager? = null

    fun init(tokenManager: TokenManager) {
        ApiClient.tokenManager = tokenManager
    }

    private val okhttp = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val token = tokenManager?.get()
            val request = if (token != null) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else chain.request()
            chain.proceed(request)
        }
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
