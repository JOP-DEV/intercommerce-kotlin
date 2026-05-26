package com.example.intercommerce_kotlin.core.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class NetworkConnectivityObserver @Inject constructor(
    @ApplicationContext private val context: Context
) : ConnectivityObserver {

    override val status: Flow<ConnectionStatus> = callbackFlow {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(currentStatus())
            }
        }

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                trySend(currentStatus())
            }

            override fun onLost(network: android.net.Network) {
                trySend(currentStatus())
            }

            override fun onCapabilitiesChanged(
                network: android.net.Network,
                networkCapabilities: NetworkCapabilities
            ) {
                trySend(currentStatus())
            }
        }

        @Suppress("DEPRECATION")
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        trySend(currentStatus())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            context.unregisterReceiver(receiver)
        }
    }

    override fun isConnected(): Boolean = currentStatus() == ConnectionStatus.Available

    private fun currentStatus(): ConnectionStatus {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return ConnectionStatus.Unavailable
        val capabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return ConnectionStatus.Unavailable

        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return if (hasInternet && isValidated) {
            ConnectionStatus.Available
        } else {
            ConnectionStatus.Unavailable
        }
    }
}
