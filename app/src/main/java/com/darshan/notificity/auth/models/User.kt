package com.darshan.notificity.auth.models

import androidx.annotation.Keep
import com.darshan.notificity.auth.AuthType

@Keep
data class User(
    val id: String = "",
    val email: String? = null,
    val name: String? = null,
    val profilePicUrl: String? = null,
    val authType: AuthType = AuthType.ANONYMOUS,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)