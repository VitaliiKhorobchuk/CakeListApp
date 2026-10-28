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
        // TODO: Introduce an app theme following system dark mode and matching system-bar icon
        // contrast. The current Compose palette and status-bar styling intentionally stay light.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )

        val getCakes = (application as CakeListApplication).container.getCakesUseCase

        setContent {
            MaterialTheme {
                // TODO: If more screens need injected ViewModels, use Hilt or Koin to remove
                // manual dependency lookup while keeping ViewModels scoped to their owner.
                val cakeViewModel: CakeListViewModel = viewModel {
                    CakeListViewModel(getCakes)
                }
                CakeListRoute(viewModel = cakeViewModel)
            }
        }
    }
}
