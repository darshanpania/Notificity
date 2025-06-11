package com.darshan.notificity.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase.JournalMode
import androidx.room.migration.Migration
import com.darshan.notificity.database.AppDatabase
import com.darshan.notificity.database.AppDatabase.Companion.MIGRATION_1_2
import com.darshan.notificity.database.AppDatabase.Companion.MIGRATION_2_3
import com.darshan.notificity.utils.Constants
import com.darshan.notificity.database.NotificationDao
import com.darshan.notificity.data.NotificationRepository
import com.darshan.notificity.data.NotificationRepositoryImpl
import com.darshan.notificity.ui.theme.ThemePreferenceManager
import com.darshan.notificity.utils.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    companion object {

        @Provides
        @Singleton
        fun providesPreferenceManager(): PreferenceManager {
            return PreferenceManager()
        }

        @Provides
        @Singleton
        fun providesThemePreferenceManager(preferenceManager: PreferenceManager): ThemePreferenceManager {
            return ThemePreferenceManager(preferenceManager)
        }

        @Provides
        @Singleton
        fun provideNotificationDao(database: AppDatabase): NotificationDao {
            return database.notificationDao()
        }

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context,
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                Constants.DB_NAME
            ).addMigrations(*provideMigrations())
                .setJournalMode(JournalMode.AUTOMATIC)
                .fallbackToDestructiveMigration()
                .enableMultiInstanceInvalidation()
                .build()
        }

        @Provides
        @Singleton
        fun provideMigrations(): Array<Migration> {
            return arrayOf(
                MIGRATION_1_2,
                MIGRATION_2_3,
            )
        }
    }

    @Binds
    @Singleton
    abstract fun providesNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository
}