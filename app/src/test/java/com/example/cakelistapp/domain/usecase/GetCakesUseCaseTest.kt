package com.example.cakelistapp.domain.usecase

import com.example.cakelistapp.domain.model.Cake
import com.example.cakelistapp.domain.model.CakeLoadError
import com.example.cakelistapp.domain.model.CakeLoadException
import com.example.cakelistapp.domain.repository.CakeRepository
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetCakesUseCaseTest {
    @Test
    fun `removes repeated records including non-adjacent duplicates`() = runTest {
        val banana = cake("Banana")
        val carrot = cake("Carrot")

        val result = useCaseReturning(listOf(carrot, banana, carrot.copy(), banana.copy()))()

        assertEquals(listOf(banana, carrot), result)
    }

    @Test
    fun `keeps records when any field differs`() = runTest {
        val original = cake("Banana")
        val variants = listOf(
            original,
            original.copy(title = "banana"),
            original.copy(description = "Another recipe"),
            original.copy(imageUrl = "https://example.com/another.jpg"),
        )

        assertEquals(variants, useCaseReturning(variants)())
    }

    @Test
    fun `sorts titles alphabetically ignoring case`() = runTest {
        val cakes = listOf(cake("Victoria"), cake("banana"), cake("Dundee"), cake("Carrot"))

        val result = useCaseReturning(cakes)()

        assertEquals(listOf("banana", "Carrot", "Dundee", "Victoria"), result.map { it.title })
    }

    @Test
    fun `preserves input order when titles compare equally`() = runTest {
        val first = cake("banana").copy(description = "First recipe")
        val second = cake("Banana").copy(description = "Second recipe")

        val result = useCaseReturning(listOf(cake("Carrot"), first, second))()

        assertEquals(listOf(first, second, cake("Carrot")), result)
    }

    @Test
    fun `returns an empty list for an empty repository`() = runTest {
        assertEquals(emptyList(), useCaseReturning(emptyList())())
    }

    @Test
    fun `propagates repository failures`() = runTest {
        val failure = CakeLoadException(CakeLoadError.NETWORK)
        val useCase = GetCakesUseCase(object : CakeRepository {
            override suspend fun getCakes(): List<Cake> = throw failure
        })

        assertSame(failure, assertFailsWith<CakeLoadException> { useCase() })
    }

    @Test
    fun `propagates coroutine cancellation`() = runTest {
        val cancellation = CancellationException("Request cancelled")
        val useCase = GetCakesUseCase(object : CakeRepository {
            override suspend fun getCakes(): List<Cake> = throw cancellation
        })

        assertSame(cancellation, assertFailsWith<CancellationException> { useCase() })
    }

    private fun useCaseReturning(cakes: List<Cake>) = GetCakesUseCase(object : CakeRepository {
        override suspend fun getCakes(): List<Cake> = cakes
    })

    private fun cake(title: String) = Cake(title, "A cake recipe", "https://example.com/cake.jpg")
}
