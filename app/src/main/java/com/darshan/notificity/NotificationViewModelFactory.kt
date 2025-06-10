package com.darshan.notificity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.darshan.notificity.viewmodel.MainViewModel

class NotificationViewModelFactory(
    private val application: Application,
    val repository: NotificationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application = application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
