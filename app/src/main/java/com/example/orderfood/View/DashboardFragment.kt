package com.example.orderfood.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.orderfood.OrderActivity
import com.example.orderfood.R
import com.example.orderfood.ViewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_fragment_dashboard.*

class DashboardFragment : Fragment() {
    lateinit var mMainViewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.activity_main_fragment_dashboard, act_main_container, false)
        mMainViewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    private fun setUpView() {
        mMainViewModel.isShowCart.value = false
        fragment_dashboard_tvHome.setOnClickListener {
            replaceFragment(HomeFragment())
        }
        fragment_dashboard_ivHome.setOnClickListener {
            replaceFragment(HomeFragment())
        }
        fragment_dashboard_ivFavorite.setOnClickListener {
            goToFavorite()
        }
        fragment_dashboard_tvFavorite.setOnClickListener {
            goToFavorite()
        }
        fragment_dashboard_ivCart.setOnClickListener {
            goToCart()
        }
        fragment_dashboard_tvCart.setOnClickListener {
            goToCart()
        }
        fragment_dashboard_ivReceipts.setOnClickListener {
            goToReceipts()
        }
        fragment_dashboard_tvReceipts.setOnClickListener {
            goToReceipts()
        }
    }

    private fun goToFavorite() {
        if (mMainViewModel.isLogin()) {
            Log.e("dashboard", "Da dang nhap")
            mMainViewModel.vmTypeFoodSelect.value = "-1"
        } else {
            Log.e("dashboard", "Chua dang nhap")
            activity?.let { it1 -> mMainViewModel.signInWithAnonymously(it1) }
        }
        replaceFragment(FoodListFragment())
    }

    private fun goToCart() {
        if (mMainViewModel.isLogin()) {
            Log.e("dashboard", "Da dang nhap")
        } else {
            Log.e("dashboard", "Chua dang nhap")
            activity?.let { it1 -> mMainViewModel.signInWithAnonymously(it1) }
        }
        startActivity(Intent(context, OrderActivity::class.java))
    }

    private fun goToReceipts() {
        if (mMainViewModel.isLogin()) {
            Log.e("dashboard", "Da dang nhap")
        } else {
            Log.e("dashboard", "Chua dang nhap")
            activity?.let { it1 -> mMainViewModel.signInWithAnonymously(it1) }
        }
        replaceFragment(ReceiptsFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.act_main_container, fragment)
        transaction.addToBackStack("dashboard")
        transaction.commit()
    }
}