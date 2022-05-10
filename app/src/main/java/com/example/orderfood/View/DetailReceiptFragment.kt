package com.example.orderfood.View

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
import com.example.orderfood.Adapter.FoodOrderAdapter
import com.example.orderfood.R
import com.example.orderfood.ViewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main_fragment_detail_receipt.*

class DetailReceiptFragment : Fragment() {
    lateinit var mMainViewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =
            inflater.inflate(R.layout.activity_main_fragment_detail_receipt, container, false)
        mMainViewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI()
        setUpView()
    }

    private fun setUpView() {
        fragment_detail_receipt_ivBack.setOnClickListener { finishFragment() }
        setUpRecyclerView()
    }

    private fun connectUI() {
        mMainViewModel.getFoodOrderList()
        mMainViewModel.vmReceiptSelect.observe(viewLifecycleOwner, Observer {
            fragment_detail_receipt_tvTotal.text = it.total
            fragment_detail_receipt_tvPayment.text = it.payment
            fragment_detail_receipt_tvTime.text = it.time
            fragment_detail_receipt_tvAddress.text = it.address
            val phone = it.phone
            if (phone.equals("")) {
                fragment_detail_receipt_llPhone.visibility = View.GONE
            } else {
                fragment_detail_receipt_tvPhone.text = phone
                fragment_detail_receipt_llPhone.visibility = View.VISIBLE
            }
            val message = it.message
            if (message.equals("")) {
                fragment_detail_receipt_llMessage.visibility = View.GONE
                fragment_detail_receipt_tvMessage.visibility = View.GONE
            } else {
                fragment_detail_receipt_tvMessage.text = message
                fragment_detail_receipt_llMessage.visibility = View.VISIBLE
                fragment_detail_receipt_tvMessage.visibility = View.VISIBLE
            }
        })
        if (mMainViewModel.isLoginEmail()) {
            fragment_detail_sign_in_to_rate.visibility = View.GONE
        } else {
            fragment_detail_sign_in_to_rate.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = FoodOrderAdapter(mMainViewModel.vmFoodOrderList.value!!)
        adapter.setCallBack {
            mMainViewModel.setUpFoodSelected(adapter.list[it])
            replaceFragment(ReviewsFragment())
        }
        adapter.setCallBackClickRate {
            if (adapter.list[it].is_rate == false) {
                mMainViewModel.vmFoodOrderSelect.value = adapter.list[it]
                if (mMainViewModel.isLoginEmail()) {
                    replaceFragment(AddCommentFragment())
                } else {
                    Toast.makeText(context, "Please sign up to rate this food!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(context, "You rated this food for receipt!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        fragment_detail_receipt_rvFoods.layoutManager = layoutManager
        fragment_detail_receipt_rvFoods.adapter = adapter
        mMainViewModel.vmFoodOrderList.observe(viewLifecycleOwner, Observer {
            adapter.list = it
            adapter.notifyDataSetChanged()
        })
        mMainViewModel.isUpdateFoodOrder.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                adapter.list = mMainViewModel.vmFoodOrderList.value!!
                adapter.notifyDataSetChanged()
                mMainViewModel.isUpdateFoodOrder.value = false
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.add(R.id.act_main_container, fragment)
        transaction.addToBackStack("receipts")
        transaction.commit()
    }

    private fun finishFragment() {
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack()
        }
    }
}