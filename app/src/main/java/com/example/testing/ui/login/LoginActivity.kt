package com.example.testing.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.testing.MainScreenActivity
import com.example.testing.R
import com.example.testing.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var shared: SharedPreferences
    private lateinit var builder: AlertDialog.Builder
    private lateinit var loading: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        builder = AlertDialog.Builder(this)
        builder.setTitle("Failed to load data")
        builder.setMessage("Try later or check your internet connection.")
        builder.setPositiveButton("OK") {_,_->}
        shared = getSharedPreferences("com.example.testing.shared", 0)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val register = binding.register
        loading = binding.loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(this@LoginActivity, loading))
            .get(LoginViewModel::class.java)

        if (shared.contains("username") && shared.contains("password")) {
            loginViewModel.login(
                shared.getString("username", "DEFAULT")!!,
                shared.getString("password", "DEFAULT")!!
            )
        }

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer
            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid
            register.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString(),
                        )
                    }
                }
                false
            }

            login.setOnClickListener {
                runOnUiThread {loading.visibility = View.VISIBLE}
                loginViewModel.login(username.text.toString(), password.text.toString())
            }

            register.setOnClickListener {
                runOnUiThread {loading.visibility = View.VISIBLE}
                loginViewModel.register(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Thread {
            runOnUiThread {
                val intent = Intent(this, MainScreenActivity::class.java)
                    .putExtra("user", model.displayName)
                startActivity(intent)
            }
        }.start()

        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onResume() {
        super.onResume()
        loading.visibility = View.GONE
        binding.username.setText("")
        binding.password.setText("")
    }

    private fun showLoginFailed(errorString: String) {
        Thread {
            runOnUiThread {
                loading.visibility = View.GONE
                binding.login
                if (errorString == "Error logging in /w Thread") {
                    builder.show()
                } else {
                    Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
                }
            }
        }.apply {
            start()
            join()
        }
    }
}
/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}