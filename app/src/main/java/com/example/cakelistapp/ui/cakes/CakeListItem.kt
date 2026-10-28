package com.example.cakelistapp.ui.cakes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cakelistapp.R
import com.example.cakelistapp.domain.model.Cake

@Composable
internal fun CakeListItem(cake: Cake, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier.fillMaxWidth().clickable(
            role = Role.Button,
            onClickLabel = stringResource(R.string.show_cake_description),
            onClick = onClick,
        ),
        headlineContent = {
            Text(
                text = cake.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingContent = { CakeImagePlaceholder() },
    )
}

@Composable
private fun CakeImagePlaceholder() {
    // TODO: Replace the placeholder with remote image loading when live data is implemented.
    Surface(
        modifier = Modifier.size(72.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.ic_image_placeholder),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CakeListItemPreview() {
    MaterialTheme {
        CakeListItem(
            cake = Cake("Lemon Cheesecake", "A cheesecake made of lemon", ""),
            onClick = {},
        )
    }
}
