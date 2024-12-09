package mp.verif_ai.data.repository.inbox

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import mp.verif_ai.data.util.NetworkMonitor
import mp.verif_ai.data.util.NetworkStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitorImpl @Inject constructor(
    private val context: Context
) : NetworkMonitor {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkStatus = MutableStateFlow(getCurrentNetworkStatus())

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerNetworkCallback()
        }
    }

    override fun isOnline(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }

    override fun observeNetworkStatus(): Flow<NetworkStatus> = _networkStatus.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.N)
    private fun registerNetworkCallback() {
        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _networkStatus.value = NetworkStatus.Available
                }

                override fun onLost(network: Network) {
                    _networkStatus.value = NetworkStatus.Unavailable
                }
            }
        )
    }

    private fun getCurrentNetworkStatus(): NetworkStatus {
        return if (isOnline()) NetworkStatus.Available else NetworkStatus.Unavailable
    }
}