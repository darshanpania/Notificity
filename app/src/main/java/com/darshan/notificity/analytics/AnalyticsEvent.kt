package com.darshan.notificity.analytics

/**
 * Represents a generic analytics event.
 */
interface AnalyticsEvent {

    /** Name of the event to be logged. */
    val name: String

    /** Optional key-value data associated with the event. */
    val properties: Map<String, Any>?
}