package mp.verif_ai.domain.usecase.user

import mp.verif_ai.domain.model.auth.User
import mp.verif_ai.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String): Result<User> {
        return userRepository.getUser(id)
    }
}