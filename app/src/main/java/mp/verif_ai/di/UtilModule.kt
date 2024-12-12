package mp.verif_ai.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mp.verif_ai.domain.util.ClipboardManager

// di/UtilModule.kt
@Module
@InstallIn(SingletonComponent::class)
object UtilModule {
    @Provides
    fun provideClipboardManager(@ApplicationContext context: Context): ClipboardManager {
        return ClipboardManager(context)
    }
}