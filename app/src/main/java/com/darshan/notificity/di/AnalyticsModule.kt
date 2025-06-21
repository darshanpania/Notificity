package com.darshan.notificity.di

import android.content.Context
import com.darshan.notificity.analytics.domain.AppAnalytics
import com.darshan.notificity.analytics.domain.AuthAnalytics
import com.darshan.notificity.analytics.domain.PermissionAnalytics
import com.darshan.notificity.analytics.providers.firebase.FirebaseAppAnalytics
import com.darshan.notificity.analytics.providers.firebase.FirebaseAuthAnalytics
import com.darshan.notificity.analytics.providers.firebase.FirebasePermissionAnalytics
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    abstract fun bindAuthAnalytics(
        firebaseAuthAnalytics: FirebaseAuthAnalytics
    ): AuthAnalytics

    @Binds
    abstract fun bindAppAnalytics(
        firebaseAppAnalytics: FirebaseAppAnalytics
    ): AppAnalytics

    @Binds
    abstract fun bindPermissionAnalytics(
        firebasePermissionAnalytics: FirebasePermissionAnalytics
    ): PermissionAnalytics

    companion object {

        @Provides
        @Singleton
        fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
            return FirebaseAnalytics.getInstance(context)
        }
    }
}