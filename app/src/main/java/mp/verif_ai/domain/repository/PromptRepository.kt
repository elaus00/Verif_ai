package mp.verif_ai.domain.repository

import mp.verif_ai.domain.model.chat.PromptImport

interface PromptRepository {
    suspend fun importPrompt(promptText: String): PromptImport
    suspend fun createQuestionFromPrompt(conversationId: String): String
}