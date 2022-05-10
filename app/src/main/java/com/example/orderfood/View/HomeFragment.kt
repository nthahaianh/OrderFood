package com.example.orderfood.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.Adapter.FoodsAdapter2
import com.example.orderfood.Model.Food
import com.example.orderfood.R
import com.example.orderfood.ViewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_fragment_home.*

class HomeFragment : Fragment() {
    lateinit var mMainViewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main_fragment_home, act_main_container, false)
        mMainViewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    private fun setUpView() {
        mMainViewModel.isShowCart.value = true
        setUpRecyclerView(fragment_home_rvType0, mMainViewModel.vmFoodsList0)
        setUpRecyclerView(fragment_home_rvType1, mMainViewModel.vmFoodsList1)
        setUpRecyclerView(fragment_home_rvType2, mMainViewModel.vmFoodsList2)
        setUpRecyclerView(fragment_home_rvType3, mMainViewModel.vmFoodsList3)
        setUpRecyclerView(fragment_home_rvType4, mMainViewModel.vmFoodsList4)
        fragment_home_tvType0.setOnClickListener {
            mMainViewModel.vmTypeFoodSelect.value = "0"
            replaceFragment(FoodListFragment())
        }
        fragment_home_tvType1.setOnClickListener {
            mMainViewModel.vmTypeFoodSelect.value = "1"
            replaceFragment(FoodListFragment())
        }
        fragment_home_tvType2.setOnClickListener {
            mMainViewModel.vmTypeFoodSelect.value = "2"
            replaceFragment(FoodListFragment())
        }
        fragment_home_tvType3.setOnClickListener {
            mMainViewModel.vmTypeFoodSelect.value = "3"
            replaceFragment(FoodListFragment())
        }
        fragment_home_tvType4.setOnClickListener {
            mMainViewModel.vmTypeFoodSelect.value = "4"
            replaceFragment(FoodListFragment())
        }
        fragment_home_ivSearch.setOnClickListener {
            mMainViewModel.vmTypeFoodSelect.value = "5"
            mMainViewModel.searchFood(fragment_home_etSearch.text.toString().trim())
            replaceFragment(FoodListFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.add(R.id.act_main_container, fragment)
        transaction.addToBackStack("home")
        transaction.commit()
    }

    private fun setUpRecyclerView(
        dashboardRv0: RecyclerView,
        vmFoodsList: MutableLiveData<MutableList<Food>>
    ) {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = FoodsAdapter2(vmFoodsList.value!!)
        adapter.setCallBack {
            vmFoodsList.value!![it]
                .let { it1 ->
                    mMainViewModel.vmTypeFoodSelect.value = it1.type
                    mMainViewModel.vmFoodSelect.value = it1
                    replaceFragment(DetailFoodFragment())
                }
        }
        dashboardRv0.layoutManager = layoutManager
        dashboardRv0.adapter = adapter
        vmFoodsList.observe(viewLifecycleOwner, Observer {
            adapter.list = it
            adapter.notifyDataSetChanged()
        })
        mMainViewModel.isUpdateList.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                adapter.list = vmFoodsList.value!!
                adapter.notifyDataSetChanged()
                mMainViewModel.isUpdateList.value = false
            }
        })
    }
}