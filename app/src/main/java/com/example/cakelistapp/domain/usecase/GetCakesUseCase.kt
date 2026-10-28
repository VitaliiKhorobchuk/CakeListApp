package com.example.cakelistapp.domain.usecase

import com.example.cakelistapp.domain.model.Cake
import com.example.cakelistapp.domain.repository.CakeRepository

class GetCakesUseCase(
    private val repository: CakeRepository,
) {
    // Cake equality includes the title, description, and image URL.
    suspend operator fun invoke(): List<Cake> = repository.getCakes()
        .distinct()
        .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
}
