package com.example.cakelistapp

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cakelistapp.ui.cakes.CakeListRoute
import com.example.cakelistapp.ui.cakes.CakeListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )

        val getCakes = (application as CakeListApplication).container.getCakesUseCase

        setContent {
            MaterialTheme {
                // TODO: Implement Hilt or Koin for ViewModel injection for improvement
                val cakeViewModel: CakeListViewModel = viewModel {
                    CakeListViewModel(getCakes)
                }
                CakeListRoute(viewModel = cakeViewModel)
            }
        }
    }
}
