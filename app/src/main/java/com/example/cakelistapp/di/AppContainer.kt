package com.example.cakelistapp.di

import com.example.cakelistapp.data.RemoteCakeRepository
import com.example.cakelistapp.data.remote.CakeApi
import com.example.cakelistapp.domain.repository.CakeRepository
import com.example.cakelistapp.domain.usecase.GetCakesUseCase
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Consider Hilt or Koin for automatic injection and dependency scoping as the future improvement.
class AppContainer {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    private val cakeApi: CakeApi = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CakeApi::class.java)

    private val cakeRepository: CakeRepository = RemoteCakeRepository(cakeApi)

    val getCakesUseCase: GetCakesUseCase = GetCakesUseCase(cakeRepository)
}
