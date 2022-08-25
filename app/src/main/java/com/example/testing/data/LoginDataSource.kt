package com.example.testing.data

import android.view.View
import android.widget.ProgressBar
import com.example.testing.data.model.LoggedInUser
import com.example.testing.ui.login.LoginActivity
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource (private val activity: LoginActivity, private val view : ProgressBar){

    private val shared = activity.getSharedPreferences("com.example.testing.shared", 0)
    private val db = PostgresHandler()

    fun login(username: String, password: String): Result<LoggedInUser> {
        var result : Result<LoggedInUser>? = null
        Thread {
            run {
                try {
                    val check = db.checkCredentials(username, password)
                    if (check == 1) {
                        shared.edit()
                            .putString("username", username)
                            .putString("password", password)
                            .apply()
                        result = Result.Success(LoggedInUser(displayName = username))
                    } else if (check == 0) result = Result.Error(IOException("Error logging in, h"))
                    else result = Result.Error(IOException("Error logging in /w Thread"))
                } catch (e: Throwable) {
                    Result.Error(IOException("Error logging in, e", e))
                }
            }
            activity.runOnUiThread {
                view.visibility = View.VISIBLE
            }
        }.apply {
            start()
            join()
        }
        return if(result != null)
            result!!
        else Result.Error(IOException("Error logging in /w Thread"))
    }

    fun register(username: String, password: String): Result<LoggedInUser> {
        var result : Result<LoggedInUser>? = null
        Thread {
            run {
                result = try {
                    if (db.checkCredentials(username, password) == -1)
                        Result.Error(IOException("Error registering in"))
                    else {
                        db.registerCredentials(username, password)
                        Result.Success(LoggedInUser(displayName = username))
                    }
                } catch (e: Throwable) {
                    Result.Error(IOException("Error registering in", e))
                }
            }
            activity.runOnUiThread {
                view.visibility = View.VISIBLE
            }
        }.apply {
            start()
            join()
        }
        return if(result != null)
            result!!
        else Result.Error(IOException("Error logging in /w Thread"))
    }

    fun logout() {
        shared.edit()
            .remove("username")
            .remove("password")
            .apply()
    }
}