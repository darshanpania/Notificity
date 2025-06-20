package com.darshan.notificity.auth.models

import androidx.annotation.Keep
import com.darshan.notificity.auth.AuthType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

@Keep
data class User(
    val id: String? = null,
    val email: String? = null,
    val name: String? = null,
    val profilePicUrl: String? = null,
    val authType: AuthType = AuthType.ANONYMOUS,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val lastLoginAt: Timestamp? = null
)