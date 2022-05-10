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
import com.example.orderfood.Adapter.FoodsAdapter
import com.example.orderfood.Model.Food
import com.example.orderfood.Model.FoodCart
import com.example.orderfood.R
import com.example.orderfood.ViewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_fragment_food_list.*

class FoodListFragment : Fragment() {
    lateinit var mMainViewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.activity_main_fragment_food_list, act_main_container, false)
        mMainViewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        mMainViewModel.isShowCart.value = true
        when (mMainViewModel.vmTypeFoodSelect.value) {
            "0" -> setUpRecyclerView(mMainViewModel.vmFoodsList0)
            "1" -> setUpRecyclerView(mMainViewModel.vmFoodsList1)
            "2" -> setUpRecyclerView(mMainViewModel.vmFoodsList2)
            "3" -> setUpRecyclerView(mMainViewModel.vmFoodsList3)
            "4" -> setUpRecyclerView(mMainViewModel.vmFoodsList4)
            "5" -> setUpRecyclerView(mMainViewModel.vmFoodsSearch)
            "-1" -> {
                mMainViewModel.getFoodFavorite()
                setUpRecyclerView(mMainViewModel.vmFavoriteList)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.add(R.id.act_main_container, fragment)
        transaction.addToBackStack("food-list")
        transaction.commit()
    }

    private fun setUpRecyclerView(vmFoodsList: MutableLiveData<MutableList<Food>>) {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = FoodsAdapter(vmFoodsList.value!!)
        adapter.setCallBack {
            vmFoodsList.value!![it]
                .let { it1 ->
                    mMainViewModel.vmFoodSelect.value = it1
                    replaceFragment(DetailFoodFragment())
                }
        }
        adapter.setCallBackClickAdd {
            val food = adapter.list[it]
            val foodCart = FoodCart(
                food.id,
                food.type,
                food.name,
                food.price,
                food.describe,
                food.rate,
                food.image_uri,
                1
            )
            mMainViewModel.addToCart(foodCart)
        }
        fragment_food_list_rvFood.layoutManager = layoutManager
        fragment_food_list_rvFood.adapter = adapter
        vmFoodsList.observe(viewLifecycleOwner, Observer {
            adapter.list = it
            adapter.notifyDataSetChanged()
            if (adapter.list.size == 0){
                fragment_food_list_tvNoFood.visibility = View.VISIBLE
            }else{
                fragment_food_list_tvNoFood.visibility = View.GONE
            }
        })
        mMainViewModel.isUpdateList.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                adapter.list = vmFoodsList.value!!
                adapter.notifyDataSetChanged()
                mMainViewModel.isUpdateList.value = false
                if (adapter.list.size == 0){
                    fragment_food_list_tvNoFood.visibility = View.VISIBLE
                }else{
                    fragment_food_list_tvNoFood.visibility = View.GONE
                }
            }
        })
    }
}