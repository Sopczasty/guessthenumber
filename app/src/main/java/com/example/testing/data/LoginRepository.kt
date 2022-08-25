package com.example.testing.data

import com.example.testing.data.model.LoggedInUser
import com.example.testing.ui.login.LoginActivity

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource, private val activity: LoginActivity) {

    // in-memory cache of the loggedInUser object
    private var user: LoggedInUser? = null

    private val shared = activity.getSharedPreferences("com.example.testing.shared", 0)

    private val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    fun register(username: String, password: String): Result<LoggedInUser> {
        val result = dataSource.register(username, password)
       if (result is Result.Success) {
           this.login(username, password)
       }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}