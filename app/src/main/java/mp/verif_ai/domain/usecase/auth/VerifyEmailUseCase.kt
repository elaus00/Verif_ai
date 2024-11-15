package mp.verif_ai.domain.usecase.auth

import mp.verif_ai.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return authRepository.sendVerificationEmail(email)
    }
}