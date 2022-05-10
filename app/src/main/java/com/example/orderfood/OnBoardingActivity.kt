package com.example.orderfood

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.orderfood.View.OnBoarding1Fragment
import com.example.orderfood.View.OnBoarding2Fragment
import com.example.orderfood.View.OnBoarding3Fragment
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var mPager: ViewPager
    var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        var sharedPreferences: SharedPreferences = this.getSharedPreferences(
            NAME_SHARE_PREFERENCES,
            Context.MODE_PRIVATE
        )
        if (sharedPreferences.getBoolean("isDoneIntroduce", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        setUpView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpView() {
        mPager = act_on_boarding_viewPager
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter
        act_on_boarding_circleIndicator.setViewPager(mPager)

        act_on_boarding_btnStart.setOnClickListener {
            if (mPager.currentItem == 2) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                mPager.currentItem = mPager.currentItem + 1
                handler.post(MainThread())
            }
        }
        onBoarding_tvSkip.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        act_on_boarding_viewPager.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    handler.post(MainThread())
                }
            }
            false
        }
    }

    inner class MainThread : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            if (mPager.currentItem == 2)
                act_on_boarding_btnStart.text = "START"
            else
                act_on_boarding_btnStart.text = "NEXT"
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return OnBoarding1Fragment()
                1 -> return OnBoarding2Fragment()
                else -> return OnBoarding3Fragment()
            }
        }
    }

    companion object {
        const val NUM_PAGES = 3
        const val NAME_SHARE_PREFERENCES = "SharePreferences"
    }
}