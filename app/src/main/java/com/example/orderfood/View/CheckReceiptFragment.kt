package com.example.orderfood.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.Adapter.FoodsCartAdapter
import com.example.orderfood.R
import com.example.orderfood.ViewModel.OrderViewModel
import kotlinx.android.synthetic.main.activity_order_fragment_check_receipt.*

class CheckReceiptFragment : Fragment() {
    lateinit var orderViewModel: OrderViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =
            inflater.inflate(R.layout.activity_order_fragment_check_receipt, container, false)
        orderViewModel = activity?.let { ViewModelProvider(it).get(OrderViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI()
        setUpView()
    }

    private fun setUpView() {
        fragment_check_ivBack.setOnClickListener { finishFragment() }
        fragment_check_btnOrder.setOnClickListener {
            orderViewModel.setTotal(fragment_check_tvTotal.text.toString().trim())
            if (orderViewModel.paymentOrder.value?.equals("Direct payment") == true) {
                orderViewModel.onAddReceipt()
            } else {
                replaceFragment(CheckPasswordFragment())
            }
        }
    }

    private fun connectUI() {
        setUpRecyclerViewFood()
        var total = 0F
        for (item in orderViewModel.vmFoodsListInCart.value!!) {
            total += item.price!! * item.quantity!!
        }
        fragment_check_tvTotal.text = String.format("%.0f",total)
        fragment_check_tvTime.text = orderViewModel.getTime()
        orderViewModel.isAddSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {
                replaceFragment(CompleteFragment())
            }
        })
        orderViewModel.phoneOrder.observe(viewLifecycleOwner, Observer {
            fragment_check_tvPhone.text = it
            if (it.equals("")) {
                fragment_check_llPhone.visibility = View.GONE
            } else {
                fragment_check_llPhone.visibility = View.VISIBLE
            }
        })
        orderViewModel.addressOrder.observe(viewLifecycleOwner, Observer {
            fragment_check_tvAddress.text = it
        })
        orderViewModel.paymentOrder.observe(viewLifecycleOwner, Observer {
            fragment_check_tvPayment.text = it
        })
        val message = orderViewModel.getMassage()
        if (message == "") {
            fragment_check_llMessage.visibility = View.GONE
            fragment_check_tvMessage.visibility = View.GONE
        } else {
            fragment_check_tvMessage.text = message
            fragment_check_tvMessage.visibility = View.VISIBLE
            fragment_check_llMessage.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerViewFood() {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        var adapter = orderViewModel.vmFoodsListInCart.value?.let { FoodsCartAdapter(it, 0) }
        adapter?.setCallBack {
        }
        adapter?.setCallBackClickPlus {
        }
        adapter?.setCallBackClickMinus {
        }
        adapter?.setCallBackClickRemove {
        }
        fragment_check_rvFoods.layoutManager = layoutManager
        fragment_check_rvFoods.adapter = adapter
        orderViewModel.vmFoodsListInCart.observe(viewLifecycleOwner, Observer {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        })
        orderViewModel.isUpdateCart.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                adapter?.list = orderViewModel.vmFoodsListInCart.value!!
                adapter?.notifyDataSetChanged()
                orderViewModel.isUpdateCart.value = false

                var total = 0F
                for (item in orderViewModel.vmFoodsListInCart.value!!) {
                    total += item.price!! * item.quantity!!
                }
                fragment_check_tvTotal.text = total.toString()
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