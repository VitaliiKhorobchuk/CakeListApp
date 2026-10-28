package com.example.cakelistapp.domain.usecase

import com.example.cakelistapp.domain.model.Cake
import com.example.cakelistapp.domain.repository.CakeRepository

class GetCakesUseCase(
    private val repository: CakeRepository,
) {
    // Cake equality includes the title, description, and image URL.
    // TODO: If the feed grows substantially, move this CPU work to an injected Default dispatcher
    // or delegate sorting/paging to the backend; the current small list is processed on the caller.
    suspend operator fun invoke(): List<Cake> = repository.getCakes()
        .distinct()
        .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
}
