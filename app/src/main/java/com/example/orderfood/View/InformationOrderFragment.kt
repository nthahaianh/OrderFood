package com.example.orderfood.View

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.Adapter.AddressAdapter2
import com.example.orderfood.Adapter.CardAdapter2
import com.example.orderfood.Adapter.FoodsCartAdapter
import com.example.orderfood.Model.FoodCart
import com.example.orderfood.ProfileActivity
import com.example.orderfood.R
import com.example.orderfood.ViewModel.OrderViewModel
import kotlinx.android.synthetic.main.activity_order_fragment_information_order.*

class InformationOrderFragment : Fragment() {
    lateinit var orderViewModel: OrderViewModel
    var indexShip = 0
    var indexPay = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =
            inflater.inflate(R.layout.activity_order_fragment_information_order, container, false)
        orderViewModel = activity?.let { ViewModelProvider(it).get(OrderViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI()
        setUpView()
    }

    private fun setUpView() {
        fragment_information_ivBack.setOnClickListener { finishFragment() }
        fragment_information_rbDirect.setOnClickListener {
            fragment_information_llOnline.visibility = View.GONE
            orderViewModel.setPaymentOrder("Direct payment")
        }
        fragment_information_rbOnline.setOnClickListener {
            fragment_information_llOnline.visibility = View.VISIBLE
            if (orderViewModel.vmCardList.value?.size!! > 1) {
                orderViewModel.setPaymentOrder(
                    "${
                        orderViewModel.vmCardList.value?.get(indexPay)?.toInfor()
                    }"
                )
            }
        }
        fragment_information_rbRestaurant.setOnClickListener {
            fragment_information_llShipping.visibility = View.GONE
            orderViewModel.setAddressOrder("At restaurant")
            orderViewModel.setPhoneOrder("")
        }
        fragment_information_rbShipping.setOnClickListener {
            fragment_information_llShipping.visibility = View.VISIBLE
            if (orderViewModel.vmAddressList.value?.size!! > 1) {
                orderViewModel.setPhoneOrder("${orderViewModel.vmAddressList.value?.get(indexShip)?.phone}")
                orderViewModel.setAddressOrder("${orderViewModel.vmAddressList.value?.get(indexShip)?.address}")
            }
        }
        fragment_information_ivAddPayment.setOnClickListener {
            startActivity(Intent(activity, ProfileActivity::class.java))
        }
        fragment_information_ivAddShip.setOnClickListener {
            startActivity(Intent(activity, ProfileActivity::class.java))
        }
        fragment_information_btnConfirm.setOnClickListener {
            orderViewModel.setUpReceipt(fragment_information_etMessage.text.toString())
            replaceFragment(CheckReceiptFragment())
        }
    }

    private fun connectUI() {
        val checkLogin = orderViewModel.isLoginEmail()
        if (checkLogin) {
            fragment_information_tvEnableOnline.visibility = View.GONE
            fragment_information_tvEnableShipping.visibility = View.GONE
            fragment_information_rbOnline.isEnabled = true
            fragment_information_rbShipping.isEnabled = true
        } else {
            fragment_information_tvEnableOnline.visibility = View.VISIBLE
            fragment_information_tvEnableShipping.visibility = View.VISIBLE
            fragment_information_rbOnline.isEnabled = false
            fragment_information_rbShipping.isEnabled = false
        }
        orderViewModel.getAddressList()
        orderViewModel.getCardList()
        setUpRecyclerViewFood()
        setUpRecyclerViewShipping()
        setUpRecyclerViewPayment()
        var total = 0F
        for (item in orderViewModel.vmFoodsListInCart.value!!) {
            total += item.price!! * item.quantity!!
        }
        fragment_information_tvTotal.text = String.format("%.0f", total)
    }

    private fun setUpRecyclerViewPayment() {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        var adapter = orderViewModel.vmCardList.value?.let { CardAdapter2(it, indexPay) }
        adapter?.setCallBack {
            adapter.itemSelected = it
            indexPay = it
            orderViewModel.setPaymentOrder(
                "${
                    orderViewModel.vmCardList.value?.get(indexPay)?.toInfor()
                }"
            )
            adapter.notifyDataSetChanged()
        }
        fragment_information_rvCard.layoutManager = layoutManager
        fragment_information_rvCard.adapter = adapter
        orderViewModel.vmCardList.observe(viewLifecycleOwner, Observer {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        })
        orderViewModel.isUpdateCardList.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                adapter?.list = orderViewModel.vmCardList.value!!
                adapter?.notifyDataSetChanged()
                orderViewModel.isUpdateCart.value = false
            }
        })
    }

    private fun setUpRecyclerViewShipping() {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        var adapter = orderViewModel.vmAddressList.value?.let { AddressAdapter2(it, indexShip) }
        adapter?.setCallBack {
            adapter.itemChoose = it
            indexShip = it
            orderViewModel.setPhoneOrder("${orderViewModel.vmAddressList.value?.get(indexShip)?.phone}")
            orderViewModel.setAddressOrder("${orderViewModel.vmAddressList.value?.get(indexShip)?.address}")
            adapter.notifyDataSetChanged()
        }
        fragment_information_rvAddress.layoutManager = layoutManager
        fragment_information_rvAddress.adapter = adapter
        orderViewModel.vmAddressList.observe(viewLifecycleOwner, Observer {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        })
        orderViewModel.isUpdateAddressList.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                adapter?.list = orderViewModel.vmAddressList.value!!
                adapter?.notifyDataSetChanged()
                orderViewModel.isUpdateAddressList.value = false
            }
        })
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
        fragment_information_rvFoods.layoutManager = layoutManager
        fragment_information_rvFoods.adapter = adapter
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
                fragment_information_tvTotal.text = total.toString()
            }
        })
    }

    fun onDeleteFood(foodCart: FoodCart) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Remove food in cart")
            .setMessage("Are you sure remove this food?")
            .setNegativeButton("No") { _: DialogInterface, _: Int -> }
            .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                orderViewModel.removeFood(foodCart)
            }
            .show()
    }

    private fun finishFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.add(R.id.act_order_container, fragment)
        transaction.addToBackStack("information")
        transaction.commit()
    }
}