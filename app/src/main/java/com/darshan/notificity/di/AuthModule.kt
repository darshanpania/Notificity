package com.darshan.notificity.di

import android.content.Context
import com.darshan.notificity.auth.AuthType
import com.darshan.notificity.auth.manager.AuthManager
import com.darshan.notificity.auth.providers.base.BaseAuthProvider
import com.darshan.notificity.auth.providers.firebase.FirebaseAnonymousAuthProvider
import com.darshan.notificity.auth.providers.firebase.FirebaseGoogleAuthProvider
import com.darshan.notificity.auth.repository.AuthRepository
import com.darshan.notificity.auth.repository.FirestoreUserRepository
import com.darshan.notificity.auth.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        firestoreUserRepository: FirestoreUserRepository
    ): UserRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseGoogleAuthProvider(
            @ApplicationContext application: Context,
            firebaseAuth: FirebaseAuth
        ): FirebaseGoogleAuthProvider {
            return FirebaseGoogleAuthProvider(
                activityContext = application,
                firebaseAuth = firebaseAuth
            )
        }

        @Provides
        @Singleton
        fun provideFirebaseAnonymousAuthProvider(
            firebaseAuth: FirebaseAuth
        ): FirebaseAnonymousAuthProvider {
            return FirebaseAnonymousAuthProvider(
                firebaseAuth = firebaseAuth
            )
        }

        @Provides
        @Singleton
        fun provideAuthManager(
            userRepository: UserRepository,
            providers: Map<AuthType, @JvmSuppressWildcards BaseAuthProvider>
        ): AuthManager {
            return AuthManager(userRepository, providers)
        }

        @Provides
        @Singleton
        fun provideAuthRepository(
            authManager: AuthManager,
            providers: Map<AuthType, @JvmSuppressWildcards BaseAuthProvider>
        ): AuthRepository {
            return AuthRepository(authManager, providers)
        }

        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        @Provides
        @Singleton
        fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

        @Provides
        @Singleton
        fun provideAuthProvidersMap(
            googleProvider: FirebaseGoogleAuthProvider,
            anonymousProvider: FirebaseAnonymousAuthProvider
        ): Map<AuthType, @JvmSuppressWildcards BaseAuthProvider> {
            return mapOf(
                AuthType.GOOGLE to googleProvider,
                AuthType.ANONYMOUS to anonymousProvider
            )
        }
    }
}