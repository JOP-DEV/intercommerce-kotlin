package com.example.intercommerce_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.intercommerce_kotlin.features.splash.presentation.SplashScreen
import com.example.intercommerce_kotlin.navigation.AppNavHost
import com.example.intercommerce_kotlin.ui.theme.IntercommercekotlinTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IntercommercekotlinTheme {
                AppEntryPoint()
            }
        }
    }
}

@Composable
private fun AppEntryPoint() {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1400)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        AppNavHost()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IntercommercekotlinTheme {
        AppEntryPoint()
    }
}
