package mp.verif_ai.data.repository.conversation

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import mp.verif_ai.data.util.FirestoreErrorHandler
import mp.verif_ai.domain.model.attachment.FileInfo
import mp.verif_ai.domain.model.attachment.ImageInfo
import mp.verif_ai.domain.repository.MediaRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val errorHandler: FirestoreErrorHandler
) : MediaRepository {

    companion object {
        private const val TAG = "MediaRepo"
        private const val MAX_RETRIES = 3
    }

    override suspend fun uploadFile(uri: Uri, fileName: String): Result<FileInfo> =
        errorHandler.runWithRetry {
            Log.d(TAG, "Uploading file: $fileName")

            val fileRef = storage.reference.child("files/$fileName")
            val uploadTask = fileRef.putFile(uri).await()

            FileInfo(
                id = fileRef.name,
                name = fileName,
                mimeType = uploadTask.metadata?.contentType ?: "",
                size = uploadTask.metadata?.sizeBytes ?: 0
            )
        }

    override suspend fun uploadImage(uri: Uri): Result<ImageInfo> =
        errorHandler.runWithRetry {
            Log.d(TAG, "Uploading image")

            val fileName = "images/${UUID.randomUUID()}"
            val imageRef = storage.reference.child(fileName)
            val uploadTask = imageRef.putFile(uri).await()
            val downloadUrl = imageRef.downloadUrl.await()

            ImageInfo(
                id = imageRef.name,
                url = downloadUrl.toString(),
                width = 0,  // TODO: Get from metadata
                height = 0  // TODO: Get from metadata
            )
        }

    override suspend fun downloadFile(fileId: String): Result<ByteArray> =
        errorHandler.runWithRetry {
            Log.d(TAG, "Downloading file: $fileId")
            storage.reference.child(fileId).getBytes(Long.MAX_VALUE).await()
        }

    override suspend fun getFileInfo(fileId: String): Result<FileInfo> =
        errorHandler.runWithRetry {
            Log.d(TAG, "Getting file info: $fileId")
            val ref = storage.reference.child(fileId)
            val metadata = ref.metadata.await()

            FileInfo(
                id = ref.name,
                name = metadata.name ?: "",
                mimeType = metadata.contentType ?: "",
                size = metadata.sizeBytes
            )
        }
}