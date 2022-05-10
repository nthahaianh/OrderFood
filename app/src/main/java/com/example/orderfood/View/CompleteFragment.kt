package com.example.orderfood.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.orderfood.R
import com.example.orderfood.ViewModel.OrderViewModel
import kotlinx.android.synthetic.main.activity_order_fragment_complete.*

class CompleteFragment : Fragment() {
    lateinit var orderViewModel: OrderViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.activity_order_fragment_complete, container, false)
        orderViewModel = activity?.let { ViewModelProvider(it).get(OrderViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            }
        activity?.let { requireActivity().onBackPressedDispatcher.addCallback(it, callback) }

        fragment_success_imageView.setImageBitmap(orderViewModel.QRCode())
        view.setOnClickListener { activity?.finish() }
        fragment_complete_btnConfirm.setOnClickListener { activity?.finish() }
    }
}