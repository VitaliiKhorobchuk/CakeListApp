package com.example.cakelistapp.ui.cakes

import androidx.lifecycle.ViewModelStore
import com.example.cakelistapp.domain.model.Cake
import com.example.cakelistapp.domain.model.CakeLoadError
import com.example.cakelistapp.domain.model.CakeLoadException
import com.example.cakelistapp.domain.repository.CakeRepository
import com.example.cakelistapp.domain.usecase.GetCakesUseCase
import com.example.cakelistapp.testing.MainDispatcherRule
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CakeListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val store = ViewModelStore()
    private val banana = Cake("Banana", "Banana recipe", "https://example.com/banana.jpg")
    private val carrot = Cake("Carrot", "Carrot recipe", "https://example.com/carrot.jpg")

    @After
    fun tearDown() {
        store.clear()
    }

    @Test
    fun `starts loading and publishes content when the initial request completes`() = runTest {
        val response = CompletableDeferred<List<Cake>>()
        val repository = ControllableCakeRepository { response.await() }
        val viewModel = createViewModel(repository)

        assertEquals(CakeListUiState.Loading, viewModel.uiState.value)
        runCurrent()
        assertEquals(1, repository.calls)
        assertEquals(CakeListUiState.Loading, viewModel.uiState.value)

        response.complete(listOf(carrot, banana, banana.copy()))
        runCurrent()

        assertEquals(CakeListUiState.Content(listOf(banana, carrot)), viewModel.uiState.value)
    }

    @Test
    fun `empty results produce empty content`() = runTest {
        val viewModel = createViewModel(ControllableCakeRepository { emptyList() })

        runCurrent()

        assertEquals(CakeListUiState.Content(emptyList()), viewModel.uiState.value)
    }

    @Test
    fun `preserves each domain error category in the error state`() = runTest {
        for (error in CakeLoadError.entries) {
            val viewModel = createViewModel(ControllableCakeRepository { throw CakeLoadException(error) })

            runCurrent()

            assertEquals(CakeListUiState.Error(error), viewModel.uiState.value)
        }
    }

    @Test
    fun `unexpected failures produce an unknown error`() = runTest {
        val viewModel = createViewModel(ControllableCakeRepository { error("Unexpected failure") })

        runCurrent()

        assertEquals(CakeListUiState.Error(CakeLoadError.UNKNOWN), viewModel.uiState.value)
    }

    @Test
    fun `refresh keeps existing content visible then replaces it`() = runTest {
        val repository = ControllableCakeRepository { listOf(banana) }
        val viewModel = createViewModel(repository)
        runCurrent()
        val response = CompletableDeferred<List<Cake>>()
        repository.response = { response.await() }

        viewModel.refresh()
        runCurrent()

        assertEquals(CakeListUiState.Content(listOf(banana), isRefreshing = true), viewModel.uiState.value)
        assertEquals(2, repository.calls)

        response.complete(listOf(carrot))
        runCurrent()

        assertEquals(CakeListUiState.Content(listOf(carrot)), viewModel.uiState.value)
    }

    @Test
    fun `failed refresh replaces content with the error state`() = runTest {
        val repository = ControllableCakeRepository { listOf(banana) }
        val viewModel = createViewModel(repository)
        runCurrent()
        repository.response = { throw CakeLoadException(CakeLoadError.NETWORK) }

        viewModel.refresh()
        runCurrent()

        assertEquals(CakeListUiState.Error(CakeLoadError.NETWORK), viewModel.uiState.value)
    }

    @Test
    fun `retry after an initial failure goes through loading and recovers`() = runTest {
        val repository = ControllableCakeRepository { throw CakeLoadException(CakeLoadError.SERVER) }
        val viewModel = createViewModel(repository)
        runCurrent()
        assertEquals(CakeListUiState.Error(CakeLoadError.SERVER), viewModel.uiState.value)
        repository.response = { listOf(banana) }

        viewModel.refresh()
        assertEquals(CakeListUiState.Loading, viewModel.uiState.value)
        runCurrent()

        assertEquals(CakeListUiState.Content(listOf(banana)), viewModel.uiState.value)
        assertEquals(2, repository.calls)
    }

    @Test
    fun `retry after a failed refresh can load fresh content`() = runTest {
        val repository = ControllableCakeRepository { listOf(banana) }
        val viewModel = createViewModel(repository)
        runCurrent()
        repository.response = { throw CakeLoadException(CakeLoadError.NETWORK) }
        viewModel.refresh()
        runCurrent()
        assertEquals(CakeListUiState.Error(CakeLoadError.NETWORK), viewModel.uiState.value)
        repository.response = { listOf(carrot) }

        viewModel.refresh()
        assertEquals(CakeListUiState.Loading, viewModel.uiState.value)
        runCurrent()

        assertEquals(CakeListUiState.Content(listOf(carrot)), viewModel.uiState.value)
        assertEquals(3, repository.calls)
    }

    @Test
    fun `ignores repeated refresh taps during the initial request`() = runTest {
        val response = CompletableDeferred<List<Cake>>()
        val repository = ControllableCakeRepository { response.await() }
        val viewModel = createViewModel(repository)
        viewModel.refresh()
        runCurrent()
        viewModel.refresh()
        runCurrent()

        assertEquals(1, repository.calls)

        response.complete(listOf(banana))
        runCurrent()
        assertEquals(CakeListUiState.Content(listOf(banana)), viewModel.uiState.value)
    }

    @Test
    fun `ignores overlapping refresh requests after content has loaded`() = runTest {
        val repository = ControllableCakeRepository { listOf(banana) }
        val viewModel = createViewModel(repository)
        runCurrent()
        val response = CompletableDeferred<List<Cake>>()
        repository.response = { response.await() }

        viewModel.refresh()
        viewModel.refresh()
        runCurrent()
        viewModel.refresh()
        runCurrent()

        assertEquals(2, repository.calls)

        response.complete(listOf(carrot))
        runCurrent()
        assertEquals(CakeListUiState.Content(listOf(carrot)), viewModel.uiState.value)
    }

    @Test
    fun `clearing the ViewModel cancels pending work without displaying an error`() = runTest {
        var requestCancelled = false
        val repository = ControllableCakeRepository {
            try {
                awaitCancellation()
            } finally {
                requestCancelled = true
            }
        }
        val viewModel = createViewModel(repository)
        runCurrent()
        assertEquals(1, repository.calls)
        assertFalse(requestCancelled)

        store.clear()
        runCurrent()

        assertTrue(requestCancelled)
        assertEquals(CakeListUiState.Loading, viewModel.uiState.value)
    }

    private fun createViewModel(repository: CakeRepository): CakeListViewModel {
        return CakeListViewModel(GetCakesUseCase(repository)).also { store.put("cakes", it) }
    }

    private class ControllableCakeRepository(var response: suspend () -> List<Cake>) : CakeRepository {
        var calls = 0
            private set

        override suspend fun getCakes(): List<Cake> {
            calls++
            return response()
        }
    }
}
