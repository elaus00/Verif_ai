package mp.verif_ai.domain.usecase.answer

import mp.verif_ai.domain.model.answer.Answer
import mp.verif_ai.domain.model.answer.AnswerStatus
import mp.verif_ai.domain.repository.AnswerRepository
import javax.inject.Inject

class CreateAnswerUseCase @Inject constructor(
    private val answerRepository: AnswerRepository
) {
    suspend operator fun invoke(answer: Answer): Result<String> {
        return answerRepository.createAnswer(answer)
    }
}

class GetAnswerUseCase @Inject constructor(
    private val answerRepository: AnswerRepository
) {
    suspend operator fun invoke(answerId: String): Result<Answer> {
        return answerRepository.getAnswer(answerId)
    }
}

class GetAnswersForQuestionUseCase @Inject constructor(
    private val answerRepository: AnswerRepository
) {
    suspend operator fun invoke(questionId: String) =
        answerRepository.getAnswersForQuestion(questionId)
}

class GetExpertAnswersUseCase @Inject constructor(
    private val answerRepository: AnswerRepository
) {
    suspend operator fun invoke(expertId: String, limit: Int = 10) =
        answerRepository.getExpertAnswers(expertId, limit)
}

class UpdateAnswerUseCase @Inject constructor(
    private val answerRepository: AnswerRepository
) {
    suspend operator fun invoke(answer: Answer): Result<Unit> {
        return answerRepository.updateAnswer(answer)
    }
}

class UpdateAnswerStatusUseCase @Inject constructor(
    private val answerRepository: AnswerRepository
) {
    suspend operator fun invoke(answerId: String, status: AnswerStatus): Result<Unit> {
        return answerRepository.updateAnswerStatus(answerId, status)
    }
}

class AdoptAnswerUseCase @Inject constructor(
    private val answerRepository: AnswerRepository
) {
    suspend operator fun invoke(answerId: String, questionId: String): Result<Unit> {
        return answerRepository.adoptAnswer(answerId, questionId)
    }
}