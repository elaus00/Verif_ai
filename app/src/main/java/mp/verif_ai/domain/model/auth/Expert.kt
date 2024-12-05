package mp.verif_ai.domain.model.auth

data class ExpertProfile(
    val expertId: String,
    val specialties: List<String>,
    val certification: String?,
    val status: ExpertStatus,
    val rating: Double,
    val answersCount: Int,
    val adoptionRate: Int
)

enum class ExpertStatus { PENDING, VERIFIED, REJECTED }
