package mp.verif_ai.data.util

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    fun isOnline(): Boolean
    fun observeNetworkStatus(): Flow<NetworkStatus>
}

enum class NetworkStatus {
    Available, Unavailable
}