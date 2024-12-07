package mp.verif_ai.data.util

import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.delay
import mp.verif_ai.data.util.CustomExceptions.NetworkException
import mp.verif_ai.data.util.CustomExceptions.NotFoundException
import mp.verif_ai.data.util.CustomExceptions.PermissionDeniedException
import javax.inject.Inject

class FirestoreErrorHandler @Inject constructor() {
    fun handleFirestoreError(error: Exception): Exception {
        return when (error) {
            is FirebaseFirestoreException -> when (error.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE -> NetworkException()
                FirebaseFirestoreException.Code.NOT_FOUND -> NotFoundException()
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> PermissionDeniedException()
                else -> error
            }
            else -> error
        }
    }

    suspend fun <T> runWithRetry(
        times: Int = 3,
        initialDelay: Long = 1000,
        operation: suspend () -> T
    ): Result<T> = runCatching {
        var lastError: Exception? = null
        repeat(times) { attempt ->
            try {
                return@runCatching operation()
            } catch (e: Exception) {
                lastError = e
                if (attempt < times - 1) {
                    delay(initialDelay * (attempt + 1))
                }
            }
        }
        throw lastError ?: Exception("Operation failed after $times attempts")
    }
}