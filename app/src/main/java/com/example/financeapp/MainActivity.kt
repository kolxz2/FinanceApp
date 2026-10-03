package com.example.financeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceAppTheme {
                FinanceAppScreen()
            }
        }
    }
}

@Composable
@Suppress("FunctionNaming")
private fun FinanceAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}

@Composable
@Suppress("FunctionNaming")
private fun FinanceAppScreen() {
    var checkSucceeded by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 24.dp,
                alignment = Alignment.CenterVertically,
            ),
        ) {
            Text(
                text = stringResource(
                    if (checkSucceeded) {
                        R.string.check_success_message
                    } else {
                        R.string.test_screen_message
                    },
                ),
                style = MaterialTheme.typography.headlineSmall,
            )
            Button(onClick = { checkSucceeded = true }) {
                Text(text = stringResource(R.string.check_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("FunctionNaming", "UnusedPrivateFunction")
private fun FinanceAppScreenPreview() {
    FinanceAppTheme {
        FinanceAppScreen()
    }
}
