package com.example.orderfood

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.orderfood.ViewModel.LoginViewModel
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        setUpView()
        connectUI()
    }

    private fun setUpView() {
        act_sign_up_btnSignUp.setOnClickListener {
            val email = act_sign_up_etEmail.text.toString().trim()
            val password = act_sign_up_edPassword.text.toString().trim()
            val passwordConfirm = act_sign_up_edPasswordAgain.text.toString().trim()
            if (email == "" || password == "") {
                Toast.makeText(this, "Email and password cannot be blank", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (passwordConfirm == "") {
                    Toast.makeText(this, "Confirm password cannot be blank", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    var checkEmail = loginViewModel.checkEmailInput(email)
                    var checkPassword = loginViewModel.checkPasswordInput(password)
                    if (checkEmail) {
                        act_sign_up_tvCheckEmail.visibility = View.GONE
                        if (checkPassword) {
                            act_sign_up_tvCheckPassword.visibility = View.GONE
                            var match = loginViewModel.checkPasswordMatch(password, passwordConfirm)
                            if (match) {
                                loginViewModel.onSignUp(email, password, this)
                                act_sign_up_tvNotMatch.visibility = View.GONE
                            } else {
                                act_sign_up_tvNotMatch.visibility = View.VISIBLE
                            }
                        } else {
                            act_sign_up_tvCheckPassword.visibility = View.VISIBLE
                        }
                    } else {
                        act_sign_up_tvCheckEmail.visibility = View.VISIBLE
                    }
                }
            }
        }
        act_sign_up_tvLoginAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }


    private fun connectUI() {
        loginViewModel.isMatches.observe(this, Observer {
            if (!it) {
                act_sign_up_tvNotMatch.visibility = View.VISIBLE
            } else {
                act_sign_up_tvNotMatch.visibility = View.GONE
            }
        })
        loginViewModel.isSuccess.observe(this, Observer {
            if (it) {
                showDialog()
            }
        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_success)

        val window: Window = dialog.window ?: return
        val gravity = Gravity.CENTER

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttributes: WindowManager.LayoutParams = window.attributes
        windowAttributes.gravity = gravity
        window.attributes = windowAttributes

        if (Gravity.BOTTOM == gravity) dialog.setCancelable(true) else dialog.setCancelable(false)

        val tvTitle = dialog.findViewById<TextView>(R.id.dialog_success_tvTitle)
        val tvContent = dialog.findViewById<TextView>(R.id.dialog_success_tvContent)
        val btnOk = dialog.findViewById<Button>(R.id.dialog_success_btnOk)
        tvTitle.text = "Register"
        tvContent.text = "Register in successfully\nThanks for your register!"
        btnOk?.setOnClickListener {
            dialog.dismiss()
            MainActivity.mMainViewModel.getFoodFavorite()
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        dialog.show()
    }
}