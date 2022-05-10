package com.example.or

import com.example.orderfood.View.InformationOrderFragment
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.Adapter.FoodsCartAdapter
import com.example.orderfood.Model.FoodCart
import com.example.orderfood.R
import com.example.orderfood.ViewModel.OrderViewModel
import kotlinx.android.synthetic.main.activity_order_fragment_cart.*

class CartFragment : Fragment() {
    lateinit var orderViewModel: OrderViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.activity_order_fragment_cart, container, false)
        orderViewModel = activity?.let { ViewModelProvider(it).get(OrderViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpRecyclerView()
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.add(R.id.act_order_container, fragment)
        transaction.addToBackStack("cart")
        transaction.commit()
    }

    private fun setUpView() {
        fragment_cart_ivBack.setOnClickListener {
            activity?.finish()
        }
        fragment_cart_btnOrder.setOnClickListener {
            if(orderViewModel.vmFoodsListInCart.value?.isEmpty() == true){
                Toast.makeText(context,"Cart is empty",Toast.LENGTH_SHORT).show()
            }else{
                replaceFragment(InformationOrderFragment())
            }
        }
    }

    private fun setUpRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        var adapter = orderViewModel.vmFoodsListInCart.value?.let { FoodsCartAdapter(it, 1) }
        adapter?.setCallBack {
        }
        adapter?.setCallBackClickPlus {
            var foodCart = adapter.list[it]
            foodCart.quantity = foodCart.quantity?.plus(1)
            orderViewModel.updateQuantity(foodCart)
        }
        adapter?.setCallBackClickMinus {
            var foodCart = adapter.list[it]
            if (foodCart.quantity!! > 1) {
                foodCart.quantity = foodCart.quantity?.minus(1)
                orderViewModel.updateQuantity(foodCart)
            } else {
                onDeleteFood(foodCart)
            }
        }
        adapter?.setCallBackClickRemove {
            onDeleteFood(adapter.list[it])
        }
        fragment_cart_rvFoods.layoutManager = layoutManager
        fragment_cart_rvFoods.adapter = adapter
        orderViewModel.vmFoodsListInCart.observe(viewLifecycleOwner, Observer {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
            if (adapter?.list?.size == 0) {
                fragment_cart_tvNoFood.visibility = View.VISIBLE
            } else {
                fragment_cart_tvNoFood.visibility = View.GONE
            }
        })
        orderViewModel.isUpdateCart.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                adapter?.list = orderViewModel.vmFoodsListInCart.value!!
                adapter?.notifyDataSetChanged()
                if (adapter?.list?.size == 0) {
                    fragment_cart_tvNoFood.visibility = View.VISIBLE
                } else {
                    fragment_cart_tvNoFood.visibility = View.GONE
                }
                orderViewModel.isUpdateCart.value = false

                var total = 0F
                for (item in orderViewModel.vmFoodsListInCart.value!!) {
                    total += item.price!! * item.quantity!!
                }
                fragment_cart_tvTotal.text = String.format("%.0f",total)
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
}