package com.example.orderfood

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.orderfood.View.DashboardFragment
import com.example.orderfood.ViewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var mMainViewModel: MainViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mMainViewModel.getFoodsData()
        mMainViewModel.getFoodFavorite()
        var sharedPreferences: SharedPreferences = this.getSharedPreferences(
            OnBoardingActivity.NAME_SHARE_PREFERENCES,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        if (!sharedPreferences.getBoolean("isDoneIntroduce", false)) {
            editor.putBoolean("isDoneIntroduce", true)
            editor.apply()
            mMainViewModel.signInWithAnonymously(this)
        } else {
            if (!mMainViewModel.isLogin()) {
                mMainViewModel.signInWithAnonymously(this)
            }
        }
        setUpView()
    }

    private fun setUpView() {
        replaceFragment(DashboardFragment())
        val personal = act_main_toolbar.toolbar_personal
        personal.setOnClickListener {
            val check = mMainViewModel.isLoginEmail()
            Log.e("check","$check")
            if (!check) {
                startActivity(Intent(this, SignInActivity::class.java))
            } else {
                Log.e("check 2","$check")
//                startActivity(Intent(this,PersonalActivity::class.java))
                startActivity(Intent(this, ProfileActivity::class.java))
            }
        }
        mMainViewModel.isShowCart.observe(this, Observer {
            if (it) {
                act_main_fabCart.visibility = View.VISIBLE
            } else {
                act_main_fabCart.visibility = View.GONE
            }
        })
        act_main_fabCart.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }
        mMainViewModel.isDisplayToolbar.observe(this, Observer {
            if (it) {
                act_main_appbar.visibility = View.VISIBLE
            } else {
                act_main_appbar.visibility = View.GONE
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.act_main_container, fragment)
        transaction.commit()
    }
}