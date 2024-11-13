package mp.verif_ai.domain.usecase.auth

import mp.verif_ai.domain.model.User
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.UserRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository  // UserRepository 제거
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.signIn(email, password)
    }
}