package mp.verif_ai.domain.usecase.auth

import mp.verif_ai.domain.model.User
import mp.verif_ai.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        nickname: String
    ): Result<User> {
        // 입력값 검증
        if (!isValidEmail(email)) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }
        if (!isValidPassword(password)) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }
        if (!isValidNickname(nickname)) {
            return Result.failure(IllegalArgumentException("Nickname must be 2-20 characters"))
        }

        return authRepository.signUp(email, password, nickname)
    }

    private fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidPassword(password: String): Boolean =
        password.length >= 6

    private fun isValidNickname(nickname: String): Boolean =
        nickname.length in 2..20
}