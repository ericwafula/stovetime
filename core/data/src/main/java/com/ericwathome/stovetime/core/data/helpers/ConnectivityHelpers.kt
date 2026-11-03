package com.ericwathome.stovetime.core.data.helpers

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

fun interface ObserveConnectivity {
    operator fun invoke(): Flow<Boolean>
}

internal class ObserveConnectivityImpl(
    private val context: Context,
) : ObserveConnectivity {
    override fun invoke(): Flow<Boolean> =
        callbackFlow {
            val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
            val callback =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities,
                    ) {
                        trySend(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
                    }

                    override fun onLost(network: Network) {
                        trySend(false)
                    }
                }
            trySend(connectivityManager.isOnline())
            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
        }.distinctUntilChanged()

    private fun ConnectivityManager.isOnline(): Boolean =
        getNetworkCapabilities(activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}
