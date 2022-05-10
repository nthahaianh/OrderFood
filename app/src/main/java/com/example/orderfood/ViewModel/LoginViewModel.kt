package com.example.orderfood.ViewModel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private val mainRepository = MainRepository()
    var username: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()
    var isError: MutableLiveData<Boolean> = MutableLiveData()
    var isMatches: MutableLiveData<Boolean> = MutableLiveData()
    var isSuccess: MutableLiveData<Boolean> = MutableLiveData()

    init {
        username.value = ""
        password.value = ""
        isError.value = false
        isMatches.value = true
        isSuccess.value = false
    }

//    fun signInWithAnonymously(activity: Activity) {
//        mainRepository.signInAnonymously(activity)
//    }

    fun checkEmailInput(email: String): Boolean {
        val pattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-zA-Z.]{2,35}".toRegex()
        return pattern.matches(email)
    }

    fun checkPasswordInput(password: String): Boolean {
        val pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,30}$".toRegex()
        return pattern.matches(password)
    }

    fun checkPasswordMatch(password: String, passwordAgain: String): Boolean {
        return password.equals(passwordAgain)
    }

    fun onSignIn(email: String, password: String, activity: Activity) {
        mainRepository.signInWithEmail(email, password, activity, isSuccess,isError)
    }

    fun onSignUp(email: String, password: String, activity: Activity) {
        mainRepository.signUpWithEmail(email, password, activity, isSuccess)
    }
}