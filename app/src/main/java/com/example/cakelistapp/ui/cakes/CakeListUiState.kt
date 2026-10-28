package com.example.cakelistapp.ui.cakes

import com.example.cakelistapp.domain.model.Cake

sealed interface CakeListUiState {
    data object Loading : CakeListUiState

    data class Content(val cakes: List<Cake>) : CakeListUiState

    data object Error : CakeListUiState
}
