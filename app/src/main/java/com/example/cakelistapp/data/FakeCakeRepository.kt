package com.example.cakelistapp.data

import com.example.cakelistapp.domain.model.Cake
import com.example.cakelistapp.domain.repository.CakeRepository
import java.io.IOException
import kotlinx.coroutines.delay

class FakeCakeRepository(
    private val cakes: List<Cake> = SampleCakes.cakes,
    private val shouldFail: Boolean = false,
    private val delayMillis: Long = 500L,
) : CakeRepository {
    override suspend fun getCakes(): List<Cake> {
        delay(delayMillis)
        if (shouldFail) {
            throw IOException("Simulated cake loading failure")
        }
        return cakes.toList()
    }
}
