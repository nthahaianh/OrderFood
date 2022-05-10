package com.example.orderfood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.orderfood.View.PersonalFragment
import com.example.orderfood.ViewModel.ProfileViewModel

class PersonalActivity : AppCompatActivity() {
    lateinit var profileViewModel: ProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        replaceFragment(PersonalFragment())
    }
    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.act_personal_container, fragment)
        transaction.commit()
    }
}