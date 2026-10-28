package com.example.cakelistapp.ui.cakes

import com.example.cakelistapp.domain.model.Cake
import com.example.cakelistapp.domain.model.CakeLoadError

sealed interface CakeListUiState {
    data object Loading : CakeListUiState

    data class Content(
        val cakes: List<Cake>,
        val isRefreshing: Boolean = false,
    ) : CakeListUiState

    data class Error(val error: CakeLoadError) : CakeListUiState
}
