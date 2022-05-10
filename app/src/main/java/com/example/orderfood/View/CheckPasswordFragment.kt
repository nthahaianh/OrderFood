package com.example.orderfood.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.orderfood.R
import com.example.orderfood.ViewModel.OrderViewModel
import kotlinx.android.synthetic.main.activity_order_fragment_check_password.*

class CheckPasswordFragment : Fragment() {
    lateinit var orderViewModel: OrderViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =
            inflater.inflate(R.layout.activity_order_fragment_check_password, container, false)
        orderViewModel = activity?.let { ViewModelProvider(it).get(OrderViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI()
        setUpView()
    }

    private fun setUpView() {
        fragment_check_password_btnConfirm.setOnClickListener {
            orderViewModel.reAuthenticate(fragment_check_password_etPassword.text.toString().trim())
        }
        fragment_check_password_ivBack.setOnClickListener { finishFragment() }
    }

    private fun connectUI() {
        orderViewModel.isReAuthenticateSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {
                orderViewModel.onAddReceipt()
                fragment_check_password_tvNotification.visibility = View.GONE
            } else {
                fragment_check_password_tvNotification.visibility = View.VISIBLE
            }
        })
        orderViewModel.isAddSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {
                replaceFragment(CompleteFragment())
            }
        })
    }

    private fun finishFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.add(R.id.act_order_container, fragment)
        transaction.addToBackStack("check")
        transaction.commit()
    }
}