package mp.verif_ai.domain.usecase.prompt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mp.verif_ai.domain.model.chat.PromptImport
import mp.verif_ai.domain.repository.PromptRepository
import mp.verif_ai.domain.util.Resource
import javax.inject.Inject

class ImportPromptUseCase @Inject constructor(
    private val promptRepository: PromptRepository
) {
    suspend operator fun invoke(promptText: String): Flow<Resource<PromptImport>> = flow {
        emit(Resource.Loading())
        try {
            val result = promptRepository.importPrompt(promptText)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to import prompt"))
        }
    }
}