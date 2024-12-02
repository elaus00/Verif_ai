package mp.verif_ai.data.repository.chat

import mp.verif_ai.domain.repository.PromptSettingsRepository
import mp.verif_ai.presentation.viewmodel.prompt.PromptSettingsViewModel.PromptSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptSettingsRepositoryImpl @Inject constructor() : PromptSettingsRepository {

    override suspend fun getPromptSettings(): PromptSettings {
        // TODO: Implement
        return PromptSettings(
            model = "GPT-4",
            temperature = 0.7f,
            maxTokens = 2000,
            apiKey = ""
        )
    }

    override suspend fun savePromptSettings(settings: PromptSettings) {
        // TODO: Implement saving settings
    }
}