package mp.verif_ai.di

import com.aallam.openai.client.OpenAI
import com.google.ai.client.generativeai.GenerativeModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mp.verif_ai.BuildConfig
import mp.verif_ai.data.service.AIServiceFactory
import mp.verif_ai.data.service.GeminiService
import mp.verif_ai.data.service.OpenAIService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AIServiceModule {
    @Provides
    @Singleton
    fun provideGeminiModel(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-1.5-pro",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideGeminiService(geminiModel: GenerativeModel): GeminiService {
        return GeminiService(geminiModel)
    }

    @Provides
    @Singleton
    fun provideOpenAI(): OpenAI {
        return OpenAI(BuildConfig.OPENAI_API_KEY)
    }

    @Provides
    @Singleton
    fun provideOpenAIService(openAI: OpenAI): OpenAIService {
        return OpenAIService(openAI)
    }

    @Provides
    @Singleton
    fun provideAIServiceFactory(
        openAIService: OpenAIService,
        geminiService: GeminiService
    ): AIServiceFactory {
        return AIServiceFactory(openAIService, geminiService)
    }
}