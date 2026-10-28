package com.example.cakelistapp.ui.cakes

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cakelistapp.R
import com.example.cakelistapp.domain.model.Cake
import com.example.cakelistapp.domain.model.CakeLoadError

@Composable
fun CakeListRoute(viewModel: CakeListViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CakeListScreen(uiState = uiState, onRefresh = viewModel::refresh, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CakeListScreen(
    uiState: CakeListUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Save a snapshot so a refresh cannot switch an open dialog to another list entry.
    var selectedCake by rememberSaveable(stateSaver = selectedCakeSaver) {
        mutableStateOf(null)
    }
    val isBusy = uiState is CakeListUiState.Loading ||
        (uiState is CakeListUiState.Content && uiState.isRefreshing)
    val requestRefresh: () -> Unit = {
        selectedCake = null
        onRefresh()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cakes_title)) },
                actions = {
                    TextButton(onClick = requestRefresh, enabled = !isBusy) {
                        Text(stringResource(R.string.refresh))
                    }
                },
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier.fillMaxSize().padding(innerPadding)
        when (uiState) {
            CakeListUiState.Loading -> {
                CakeListContent(modifier = contentModifier) {
                    CircularProgressIndicator()
                    Text(text = stringResource(R.string.loading_cakes))
                }
            }
            is CakeListUiState.Content -> {
                Column(modifier = contentModifier) {
                    if (uiState.isRefreshing) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    if (uiState.cakes.isEmpty()) {
                        CakeListContent(modifier = Modifier.fillMaxSize()) {
                            Text(text = stringResource(R.string.empty_cake_list))
                        }
                    } else {
                        CakeList(
                            cakes = uiState.cakes,
                            onCakeClick = { cake -> selectedCake = cake },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            is CakeListUiState.Error -> {
                CakeListContent(modifier = contentModifier) {
                    Text(text = stringResource(uiState.error.messageResource()))
                    Button(onClick = requestRefresh) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }
    }

    if (uiState is CakeListUiState.Content) {
        selectedCake?.let { cake ->
            CakeDescriptionDialog(cake = cake, onDismiss = { selectedCake = null })
        }
    }
}

private val selectedCakeSaver = listSaver<Cake?, String>(
    save = { cake -> cake?.let { listOf(it.title, it.description, it.imageUrl) } ?: emptyList() },
    restore = { fields ->
        fields.takeIf { it.size == 3 }?.let { Cake(it[0], it[1], it[2]) }
    },
)

@Composable
private fun CakeList(
    cakes: List<Cake>,
    onCakeClick: (Cake) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(vertical = 8.dp)) {
        itemsIndexed(cakes) { index, cake ->
            CakeListItem(cake = cake, onClick = { onCakeClick(cake) })
            if (index < cakes.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(start = 104.dp, end = 16.dp))
            }
        }
    }
}

@StringRes
private fun CakeLoadError.messageResource(): Int = when (this) {
    CakeLoadError.NETWORK -> R.string.cakes_network_error
    CakeLoadError.SERVER -> R.string.cakes_server_error
    CakeLoadError.INVALID_DATA -> R.string.cakes_data_error
    CakeLoadError.UNKNOWN -> R.string.cakes_load_error
}

@Composable
private fun CakeListContent(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

private val previewCakes = listOf(
    Cake("Banana Cake", "A banana cake", ""),
    Cake("Dundee Cake", "A staple with Dundonians", ""),
    Cake("Lemon Cheesecake", "A cheesecake made of lemon", ""),
)

@Preview(showBackground = true)
@Composable
private fun ContentPreview() {
    MaterialTheme { CakeListScreen(CakeListUiState.Content(previewCakes), onRefresh = {}) }
}

@Preview(showBackground = true)
@Composable
private fun RefreshingPreview() {
    MaterialTheme {
        CakeListScreen(CakeListUiState.Content(previewCakes, isRefreshing = true), onRefresh = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    MaterialTheme { CakeListScreen(CakeListUiState.Loading, onRefresh = {}) }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    MaterialTheme { CakeListScreen(CakeListUiState.Error(CakeLoadError.NETWORK), onRefresh = {}) }
}

@Preview(showBackground = true)
@Composable
private fun EmptyPreview() {
    MaterialTheme { CakeListScreen(CakeListUiState.Content(emptyList()), onRefresh = {}) }
}
