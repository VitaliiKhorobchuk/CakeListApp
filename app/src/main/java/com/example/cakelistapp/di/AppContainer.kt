package com.example.cakelistapp.di

import com.example.cakelistapp.data.FakeCakeRepository
import com.example.cakelistapp.domain.repository.CakeRepository
import com.example.cakelistapp.domain.usecase.GetCakesUseCase

// TODO: Consider Hilt or Koin for automatic injection and dependency scoping as the future improvement.
class AppContainer {
    // TODO: Supply the HTTP repository when live data is implemented.
    private val cakeRepository: CakeRepository = FakeCakeRepository()

    val getCakesUseCase: GetCakesUseCase = GetCakesUseCase(cakeRepository)
}
