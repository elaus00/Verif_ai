package mp.verif_ai.domain.usecase.auth

import mp.verif_ai.domain.model.auth.User
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
        // 1. 비밀번호 유효성 검사
        if (!isPasswordValid(password)) {
            return Result.failure(IllegalArgumentException("Invalid password format"))
        }

        // 2. 닉네임 유효성 검사
        if (!isNicknameValid(nickname)) {
            return Result.failure(IllegalArgumentException("Invalid nickname format"))
        }

        // 3. 회원가입 시도
        return authRepository.signUpWithEmail(email, password, nickname)
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8 && // 최소 8자
                password.any { it.isDigit() } && // 숫자 포함
                password.any { it.isLetter() } // 문자 포함
    }

    private fun isNicknameValid(nickname: String): Boolean {
        return nickname.length in 2..20 && // 2-20자 제한
                nickname.all { it.isLetterOrDigit() || it == '_' } // 영숫자와 밑줄만 허용
    }
}