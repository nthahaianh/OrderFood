package com.example.orderfood.View

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.orderfood.R
import com.example.orderfood.ViewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main_fragment_add_comment.*

class AddCommentFragment : Fragment() {
    lateinit var mMainViewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.activity_main_fragment_add_comment, container, false)
        mMainViewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI(view)
        setUpView()
    }

    private fun setUpView() {
        fragment_add_comment_ratingBar.setOnRatingBarChangeListener { ratingBar, rate, boolean ->
            Log.e("rating bar", "$ratingBar - $rate - $boolean")
            fragment_add_comment_tvRate.text = "$rate"
        }
        fragment_add_comment__btnRate.setOnClickListener {
            val rate = fragment_add_comment_ratingBar.rating
            val content = fragment_add_comment_etComment.text.toString()
            mMainViewModel.addNewRate(rate, content)
        }
    }

    private fun connectUI(view: View) {
        mMainViewModel.vmFoodOrderSelect.observe(viewLifecycleOwner, Observer {
            Glide.with(view).load(it.image_uri).into(fragment_add_comment_ivFoodImage)
            fragment_add_comment_tvNameOfFood.text = it.name
            fragment_add_comment_tvPrice.text = it.price.toString()
        })
        mMainViewModel.isAddFoodOrderSuccess.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                mMainViewModel.updateFoodOrder()
                showDialog()
                mMainViewModel.isAddFoodOrderSuccess.value = false
            }
        })
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Comment")
            .setMessage("Successfully commented!")
            .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                finishFragment()
            }
            .show()
    }

    private fun finishFragment() {
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack()
        }
    }
}