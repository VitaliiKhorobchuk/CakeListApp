package com.example.cakelistapp.ui.cakes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakelistapp.domain.usecase.GetCakesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CakeListViewModel(
    private val getCakes: GetCakesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<CakeListUiState>(CakeListUiState.Loading)
    val uiState: StateFlow<CakeListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = try {
                CakeListUiState.Content(getCakes())
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                CakeListUiState.Error
            }
        }
    }
}
