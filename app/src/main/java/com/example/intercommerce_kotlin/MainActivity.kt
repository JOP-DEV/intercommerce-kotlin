package com.example.intercommerce_kotlin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.intercommerce_kotlin.core.presentation.AppConnectivityUiEffect
import com.example.intercommerce_kotlin.core.presentation.AppConnectivityViewModel
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
    val context = LocalContext.current
    val connectivityViewModel: AppConnectivityViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        delay(1400)
        showSplash = false
    }

    LaunchedEffect(Unit) {
        connectivityViewModel.uiEffect.collect { effect ->
            when (effect) {
                is AppConnectivityUiEffect.ShowToast -> {
                    Toast.makeText(context, context.getString(effect.messageRes), Toast.LENGTH_SHORT).show()
                }
            }
        }
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
