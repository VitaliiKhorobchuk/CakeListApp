package com.example.cakelistapp.ui.cakes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

@Composable
fun CakeListRoute(viewModel: CakeListViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CakeListScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CakeListScreen(
    uiState: CakeListUiState,
    modifier: Modifier = Modifier,
) {
    var selectedCakeTitle by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.cakes_title)) }) },
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        when (uiState) {
            CakeListUiState.Loading -> {
                CakeListContent(modifier = contentModifier) {
                    CircularProgressIndicator()
                    Text(text = stringResource(R.string.loading_cakes))
                }
            }
            is CakeListUiState.Content -> {
                if (uiState.cakes.isEmpty()) {
                    CakeListContent(modifier = contentModifier) {
                        Text(text = stringResource(R.string.empty_cake_list))
                    }
                } else {
                    CakeList(
                        cakes = uiState.cakes,
                        onCakeClick = { cake -> selectedCakeTitle = cake.title },
                        modifier = contentModifier,
                    )
                }
            }
            CakeListUiState.Error -> {
                // TODO: Error display logic should be improved
                CakeListContent(modifier = contentModifier) {
                    Text(text = stringResource(R.string.cakes_load_error))
                }
            }
        }
    }

    if (uiState is CakeListUiState.Content) {
        uiState.cakes.firstOrNull { it.title == selectedCakeTitle }?.let { cake ->
            CakeDescriptionDialog(
                cake = cake,
                onDismiss = { selectedCakeTitle = null },
            )
        }
    }
}

@Composable
private fun CakeList(
    cakes: List<Cake>,
    onCakeClick: (Cake) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        itemsIndexed(cakes, key = { _, cake -> cake.title }) { index, cake ->
            CakeListItem(cake = cake, onClick = { onCakeClick(cake) })
            if (index < cakes.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(start = 104.dp, end = 16.dp))
            }
        }
    }
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
    Cake("Lemon Cheesecake", "A cheesecake made of lemon", ""),
    Cake("Banana Cake", "A banana cake", ""),
    Cake("Dundee Cake", "A staple with Dundonians", ""),
)

@Preview(showBackground = true)
@Composable
private fun ContentPreview() {
    MaterialTheme {
        CakeListScreen(uiState = CakeListUiState.Content(previewCakes))
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    MaterialTheme {
        CakeListScreen(uiState = CakeListUiState.Loading)
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    MaterialTheme {
        CakeListScreen(uiState = CakeListUiState.Error)
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyPreview() {
    MaterialTheme {
        CakeListScreen(uiState = CakeListUiState.Content(emptyList()))
    }
}
