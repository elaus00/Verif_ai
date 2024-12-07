package mp.verif_ai.data.util

import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mp.verif_ai.di.IoDispatcher
import javax.inject.Inject

class SyncManager @Inject constructor(
    private val scope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    private val activeListeners = mutableSetOf<ListenerRegistration>()
    private val lock = Mutex()

    fun startSync() {
        // Initialize sync process
    }

    fun stopSync() = runBlocking {
        lock.withLock {
            activeListeners.forEach { it.remove() }
            activeListeners.clear()
        }
    }

    suspend fun registerListener(listener: ListenerRegistration) {
        lock.withLock {
            activeListeners.add(listener)
        }
    }

    suspend fun removeListener(listener: ListenerRegistration) {
        lock.withLock {
            listener.remove()
            activeListeners.remove(listener)
        }
    }
}
