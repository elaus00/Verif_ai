package mp.verif_ai.domain.usecase.auth

import mp.verif_ai.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, code: String): Result<Unit> {
        return try {
            // 1. 이메일 인증 코드 검증
            authRepository.verifyEmailCode(code)
                .onSuccess {
                    // 2. 이메일 인증 상태 확인
                    if (!authRepository.isEmailVerified()) {
                        throw IllegalStateException("Email verification failed")
                    }
                }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}