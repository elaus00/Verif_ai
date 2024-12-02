package mp.verif_ai.di

import android.content.Context
import com.aallam.openai.client.OpenAI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mp.verif_ai.BuildConfig
import java.util.Properties
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OpenAIModule {
    @Provides
    @Singleton
    fun provideOpenAI(): OpenAI {
        return OpenAI(BuildConfig.OPENAI_API_KEY)
    }
}