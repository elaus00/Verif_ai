package mp.verif_ai.domain.model.attachment

data class FileInfo(
    val id: String,
    val name: String,
    val mimeType: String,
    val size: Long
)