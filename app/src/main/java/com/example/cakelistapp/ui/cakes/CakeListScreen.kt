package com.example.cakelistapp.ui.cakes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cakelistapp.R
import com.example.cakelistapp.domain.model.Cake

@Composable
fun CakeListRoute(viewModel: CakeListViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CakeListScreen(uiState = uiState, modifier = modifier)
}

@Composable
fun CakeListScreen(uiState: CakeListUiState, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
            )
            when (uiState) {
                CakeListUiState.Loading -> {
                    CircularProgressIndicator()
                    Text(text = stringResource(R.string.loading_cakes))
                }
                is CakeListUiState.Content -> {
                    // TODO: Replace the count with cake rows and a description dialog.
                    Text(
                        text = pluralStringResource(
                            R.plurals.cakes_loaded,
                            uiState.cakes.size,
                            uiState.cakes.size,
                        ),
                    )
                }
                CakeListUiState.Error -> {
                    Text(text = stringResource(R.string.cakes_load_error))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    MaterialTheme { CakeListScreen(CakeListUiState.Loading) }
}

@Preview(showBackground = true)
@Composable
private fun ContentPreview() {
    MaterialTheme {
        CakeListScreen(
            CakeListUiState.Content(
                listOf(Cake(title = "Banana Cake", description = "A banana cake", imageUrl = "")),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    MaterialTheme { CakeListScreen(CakeListUiState.Error) }
}
