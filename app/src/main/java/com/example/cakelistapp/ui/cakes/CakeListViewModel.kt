package com.example.cakelistapp.ui.cakes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakelistapp.domain.model.CakeLoadError
import com.example.cakelistapp.domain.model.CakeLoadException
import com.example.cakelistapp.domain.usecase.GetCakesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CakeListViewModel(
    private val getCakes: GetCakesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<CakeListUiState>(CakeListUiState.Loading)
    val uiState: StateFlow<CakeListUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        if (loadJob?.isActive == true) return

        val previousContent = _uiState.value as? CakeListUiState.Content
        _uiState.value = previousContent?.copy(isRefreshing = true)
            ?: CakeListUiState.Loading

        loadJob = viewModelScope.launch {
            try {
                _uiState.value = CakeListUiState.Content(getCakes())
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: CakeLoadException) {
                _uiState.value = CakeListUiState.Error(exception.error)
            } catch (_: Exception) {
                _uiState.value = CakeListUiState.Error(CakeLoadError.UNKNOWN)
            }
        }
    }
}
