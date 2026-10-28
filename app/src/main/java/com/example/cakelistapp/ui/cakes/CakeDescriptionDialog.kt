package com.example.cakelistapp.ui.cakes

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.cakelistapp.R
import com.example.cakelistapp.domain.model.Cake

@Composable
internal fun CakeDescriptionDialog(cake: Cake, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = cake.title) },
        text = {
            Text(
                text = cake.description,
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.close))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun CakeDescriptionDialogPreview() {
    MaterialTheme {
        CakeDescriptionDialog(
            cake = Cake("Lemon Cheesecake", "A cheesecake made of lemon", ""),
            onDismiss = {},
        )
    }
}
