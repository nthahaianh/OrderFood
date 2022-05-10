package com.example.orderfood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.or.CartFragment
import com.example.orderfood.ViewModel.OrderViewModel

class OrderActivity : AppCompatActivity() {
    lateinit var orderViewModel: OrderViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        orderViewModel.getCartData()
        replaceFragment(CartFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.act_order_container, fragment)
        transaction.commit()
    }
}