package com.darshan.notificity.main.di

import com.darshan.notificity.main.data.NotificationRepository
import com.darshan.notificity.main.data.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MainModule {
    @Binds
    @Singleton
    abstract fun providesNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository
}