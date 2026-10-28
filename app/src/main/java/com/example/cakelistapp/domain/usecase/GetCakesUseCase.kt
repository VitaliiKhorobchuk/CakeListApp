package com.example.cakelistapp.domain.usecase

import com.example.cakelistapp.domain.model.Cake
import com.example.cakelistapp.domain.repository.CakeRepository

class GetCakesUseCase(
    private val repository: CakeRepository,
) {
    // TODO: Apply duplicate removal and title sorting when business rules are implemented.
    suspend operator fun invoke(): List<Cake> = repository.getCakes()
}
