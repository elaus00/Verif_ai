package mp.verif_ai.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aallam.openai.client.OpenAI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mp.verif_ai.data.local.dao.AppDatabase
import mp.verif_ai.data.local.dao.ConversationDao
import mp.verif_ai.data.local.dao.MessageDao
import mp.verif_ai.data.local.dao.ParticipantDao
import mp.verif_ai.data.room.dao.NotificationDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        callback: AppDatabase.Callback
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addCallback(callback)
            .fallbackToDestructiveMigration() // 실제 프로덕션에서는 마이그레이션 전략 필요
            .build()
    }

    @Provides
    fun provideConversationDao(appDatabase: AppDatabase): ConversationDao {
        return appDatabase.conversationDao()
    }

    @Provides
    fun provideMessageDao(appDatabase: AppDatabase): MessageDao {
        return appDatabase.messageDao()
    }

    @Provides
    fun provideParticipantDao(appDatabase: AppDatabase): ParticipantDao {
        return appDatabase.participantDao()
    }

    @Provides
    fun provideNotificationDao(appDatabase: AppDatabase): NotificationDao {
        return appDatabase.notificationDao()
    }

    @Singleton
    @Provides
    fun provideAppDatabaseCallback(): AppDatabase.Callback {
        return object : AppDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // 필요한 경우 초기 데이터 삽입
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                // 데이터베이스 재생성 시 필요한 작업
            }
        }
    }
}