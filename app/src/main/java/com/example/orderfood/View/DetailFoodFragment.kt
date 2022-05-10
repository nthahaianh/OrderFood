package com.example.orderfood.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.orderfood.Model.FoodCart
import com.example.orderfood.R
import com.example.orderfood.ViewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_fragment_detail_food.*

class DetailFoodFragment : Fragment() {
    lateinit var mMainViewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.activity_main_fragment_detail_food, act_main_container, false)
        mMainViewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        connectVM(view)
    }

    override fun onDestroy() {
        mMainViewModel.isShowCart.value = true
        mMainViewModel.isDisplayToolbar.value = true
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    private fun connectVM(view: View) {
        mMainViewModel.getComments()
        mMainViewModel.isUpdateComment.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                var sum = 5F
                for (item in mMainViewModel.vmCommentList.value!!) {
                    sum += item.rate!!
                }
                val average = sum / (mMainViewModel.vmCommentList.value!!.size + 1)
                fragment_detail_food_ratingBar.rating = average
                fragment_detail_food_tvRate.text = String.format("%.1f", average) + "/5"
            }
        })
        mMainViewModel.isShowCart.value = false
        mMainViewModel.isDisplayToolbar.value = false
        mMainViewModel.vmFoodSelect.observe(viewLifecycleOwner, Observer {
            fragment_detail_food_tvNameOfFood.text = it.name
            fragment_detail_food_tvPrice.text = String.format("%.0f", it.price)
            fragment_detail_food_tvRate.text = String.format("%.1f", it.rate) + "/5"
            fragment_detail_food_ratingBar.rating = it.rate!!
            fragment_detail_food_tvDescribe.text = it.describe
            Glide.with(view).load(it.image_uri).placeholder(R.drawable.image_loading)
                .error(R.drawable.image_error).into(fragment_detail_food_ivImage)
        })
        mMainViewModel.checkFavorite()
        mMainViewModel.isFavorite.observe(viewLifecycleOwner, Observer {
            if (it) {
                fragment_detail_food_ivFavorite.setImageResource(R.drawable.ic_heart_fill)
            } else {
                fragment_detail_food_ivFavorite.setImageResource(R.drawable.ic_heart)
            }
        })
    }

    private fun setUpView() {
        fragment_detail_food_ivBack.setOnClickListener { finishFragment() }
        fragment_detail_food_ivImage.setOnClickListener { }
        fragment_detail_food.setOnClickListener { }
        fragment_detail_food_ivFavorite.setOnClickListener {
            if (mMainViewModel.isFavorite.value == true) {
                mMainViewModel.removeFoodFavorite()
            } else {
                mMainViewModel.addFoodFavorite()
            }
        }
        fragment_detail_food_ivComments.setOnClickListener {
            replaceFragment(ReviewsFragment())
        }
        fragment_detail_food_tvComments.setOnClickListener {
            replaceFragment(ReviewsFragment())
        }
        fragment_detail_food_btnMinus.setOnClickListener {
            var quantity = fragment_detail_food_etQuantity.text.toString().trim().toIntOrNull() ?: 1
            if (quantity > 1) {
                quantity = quantity.minus(1)
                fragment_detail_food_etQuantity.text = quantity.toString()
            }
        }
        fragment_detail_food_btnPlus.setOnClickListener {
            var quantity = fragment_detail_food_etQuantity.text.toString().trim().toIntOrNull() ?: 1
            quantity = quantity.plus(1)
            fragment_detail_food_etQuantity.text = quantity.toString()
        }
        fragment_detail_food_btnAddToCart.setOnClickListener {
            val food = mMainViewModel.vmFoodSelect.value
            val quantity = fragment_detail_food_etQuantity.text.toString().trim().toIntOrNull() ?: 1
            val foodAddToCart = FoodCart(
                food?.id,
                food?.type,
                food?.name,
                food?.price,
                food?.describe,
                food?.rate,
                food?.image_uri,
                quantity
            )
            mMainViewModel.addToCart(foodAddToCart)
        }
    }

    private fun finishFragment() {
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.add(R.id.act_main_container, fragment)
        transaction.addToBackStack("detail-food")
        transaction.commit()
    }
}