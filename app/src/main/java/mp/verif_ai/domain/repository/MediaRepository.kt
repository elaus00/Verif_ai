package mp.verif_ai.domain.repository

import android.net.Uri
import mp.verif_ai.domain.model.attachment.FileInfo
import mp.verif_ai.domain.model.attachment.ImageInfo

/**
 * Repository interface for handling media file operations.
 * Supports file upload, download, and metadata retrieval operations.
 */
interface MediaRepository {
    /**
     * Uploads a file from the given URI.
     * @param uri URI of the file to upload
     * @param fileName Name to assign to the uploaded file
     * @return Result containing file information if successful
     */
    suspend fun uploadFile(uri: Uri, fileName: String): Result<FileInfo>

    /**
     * Uploads an image file from the given URI.
     * @param uri URI of the image to upload
     * @return Result containing image information if successful
     */
    suspend fun uploadImage(uri: Uri): Result<ImageInfo>

    /**
     * Downloads a file by its ID.
     * @param fileId Unique identifier of the file to download
     * @return Result containing file bytes if successful
     */
    suspend fun downloadFile(fileId: String): Result<ByteArray>

    /**
     * Retrieves metadata information about a specific file.
     * @param fileId Unique identifier of the file
     * @return Result containing file metadata if successful
     */
    suspend fun getFileInfo(fileId: String): Result<FileInfo>
}