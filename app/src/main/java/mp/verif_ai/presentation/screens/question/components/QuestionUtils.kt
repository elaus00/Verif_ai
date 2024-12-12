package mp.verif_ai.presentation.screens.question.components

import mp.verif_ai.domain.model.question.QuestionStatus

data object QuestionUtils {
    fun getQuestionStatusText(status: QuestionStatus): String = when (status) {
        QuestionStatus.OPEN -> "답변 대기중"
        QuestionStatus.CLOSED -> "답변 완료"
        QuestionStatus.IN_PROGRESS -> "답변 진행중"
        QuestionStatus.EXPIRED -> "기간 만료"
        QuestionStatus.DELETED -> "삭제됨"
    }
}