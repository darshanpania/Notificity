package com.darshan.notificity.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

/**
 * Implementation of [AnalyticsTracker] using Firebase Analytics.
 *
 * Logs events, screen views, user IDs, and user properties to Firebase Analytics.
 **/
class FirebaseAnalyticsTracker() : AnalyticsTracker {

    private val firebaseAnalytics = Firebase.analytics

    override fun logEvent(event: AnalyticsEvent) {
        val bundle = Bundle().apply {
            event.properties?.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putInt(key, if (value) 1 else 0)
                }
            }
        }
        firebaseAnalytics.logEvent(event.name, bundle)
    }

    override fun logScreen(screenName: String, properties: Map<String, Any>?) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
            properties?.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putInt(key, if (value) 1 else 0)
                }
            }
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    override fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun setUserProperty(key: String, value: String) {
        firebaseAnalytics.setUserProperty(key, value)
    }
}