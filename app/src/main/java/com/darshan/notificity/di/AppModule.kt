package com.darshan.notificity.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase.JournalMode
import androidx.room.migration.Migration
import com.darshan.notificity.AppDatabase
import com.darshan.notificity.AppDatabase.Companion.MIGRATION_1_2
import com.darshan.notificity.AppDatabase.Companion.MIGRATION_2_3
import com.darshan.notificity.Constants
import com.darshan.notificity.NotificationDao
import com.darshan.notificity.ui.theme.ThemePreferenceManager
import com.darshan.notificity.utils.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun getAppPreferenceDataStore(): PreferenceManager {
        return PreferenceManager()
    }

    @Provides
    @Singleton
    fun getThemePreferenceManager(preferenceManager: PreferenceManager): ThemePreferenceManager {
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