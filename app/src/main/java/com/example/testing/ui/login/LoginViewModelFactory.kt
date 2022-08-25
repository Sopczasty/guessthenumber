package com.example.testing.ui.login

import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testing.data.LoginDataSource
import com.example.testing.data.LoginRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory (private var activity: LoginActivity, private var view: ProgressBar) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) return LoginViewModel(
            loginRepository = LoginRepository(
                dataSource = LoginDataSource(activity, view), activity
        )
        ) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}