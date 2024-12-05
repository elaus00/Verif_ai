package mp.verif_ai.domain.model

data class Dispute(
    val id: String,
    val answerId: String,
    val userId: String,
    val reason: String,
    val evidences: List<String>,
    val status: DisputeStatus,
    val createdAt: Long,
    val updatedAt: Long
)

enum class DisputeStatus { PENDING, RESOLVED, REJECTED }