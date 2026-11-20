package com.example.financeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

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
internal fun FinanceAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}
