package mp.verif_ai.domain.repository

import mp.verif_ai.presentation.viewmodel.prompt.PromptSettingsViewModel

interface PromptSettingsRepository {
    suspend fun getPromptSettings(): PromptSettingsViewModel.PromptSettings
    suspend fun savePromptSettings(settings: PromptSettingsViewModel.PromptSettings)
}