package com.example.orderfood.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orderfood.Adapter.CommentAdapter
import com.example.orderfood.R
import com.example.orderfood.ViewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main_fragment_reviews.*

class ReviewsFragment : Fragment() {
    lateinit var mMainViewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.activity_main_fragment_reviews, container, false)
        mMainViewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI(view)
        setUpView()
    }

    private fun setUpView() {
        setUpRecyclerView()
        fragment_reviews_ivBack.setOnClickListener { finishFragment() }
    }

    @SuppressLint("SetTextI18n")
    private fun connectUI(view: View) {
        mMainViewModel.getComments()
        mMainViewModel.vmFoodSelect.observe(viewLifecycleOwner, Observer {
            Glide.with(view).load(it.image_uri).into(fragment_reviews_ivImageFood)
            fragment_reviews_tvNameOfFood.text = it.name
            fragment_reviews_tvPrice.text = String.format("%.0f", it.price)
            fragment_reviews_tvRate.text = String.format("%.1f", it.rate) + "/5"
            fragment_reviews_ratingBar.rating = it.rate!!
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setUpRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = CommentAdapter(mMainViewModel.vmCommentList.value!!, 0)
        fragment_reviews_rvComments.layoutManager = layoutManager
        fragment_reviews_rvComments.adapter = adapter
        mMainViewModel.vmCommentList.observe(viewLifecycleOwner, Observer {
            adapter.list = mMainViewModel.vmCommentList.value!!
            adapter.notifyDataSetChanged()
            fragment_reviews_tvReviewsCount.text = adapter.list.size.toString()
            if (adapter.list.size > 0) {
                fragment_reviews_tvNoComment.visibility = View.GONE
            } else {
                fragment_reviews_tvNoComment.visibility = View.VISIBLE
            }
        })
        mMainViewModel.isUpdateComment.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                adapter.list = mMainViewModel.vmCommentList.value!!
                adapter.notifyDataSetChanged()
                fragment_reviews_tvReviewsCount.text = adapter.list.size.toString()
                mMainViewModel.isUpdateComment.value = false
                if (adapter.list.size > 0) {
                    fragment_reviews_tvNoComment.visibility = View.GONE
                } else {
                    fragment_reviews_tvNoComment.visibility = View.VISIBLE
                }
                var sum = 5F
                for (item in adapter.list) {
                    sum += item.rate!!
                }
                val average = sum / (adapter.list.size + 1)
                fragment_reviews_ratingBar.rating = average
                fragment_reviews_tvRate.text = String.format("%.1f", average) + "/5"
            }
        })
    }

    private fun finishFragment() {
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack()
        }
    }
}