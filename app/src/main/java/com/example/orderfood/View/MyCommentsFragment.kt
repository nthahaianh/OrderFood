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
import com.example.orderfood.Adapter.CommentAdapter
import com.example.orderfood.R
import com.example.orderfood.ViewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile_fragment_comments.*

class MyCommentsFragment : Fragment() {
    lateinit var profileViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.activity_profile_fragment_comments, container, false)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        connectUI()
    }

    private fun setUpView() {
        profileViewModel.onGetCommentList()
        fragment_comments_ivBack.setOnClickListener { finishFragment() }
    }

    private fun connectUI() {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = CommentAdapter(profileViewModel.vmCommentList.value!!, 1)
        fragment_comments_rvComments.layoutManager = layoutManager
        fragment_comments_rvComments.adapter = adapter
        profileViewModel.vmCommentList.observe(viewLifecycleOwner, Observer {
            setUpAdapter(adapter)
        })
        profileViewModel.isUpdateComment.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                setUpAdapter(adapter)
                profileViewModel.isUpdateComment.value = false
            }
        })
    }

    private fun setUpAdapter(adapter: CommentAdapter) {
        adapter.list = profileViewModel.vmCommentList.value!!
        adapter.notifyDataSetChanged()
        fragment_comments_tvReviewsCount.text = adapter.list.size.toString()
        if (adapter.list.size > 0) {
            fragment_comments_tvNoComment.visibility = View.GONE
        } else {
            fragment_comments_tvNoComment.visibility = View.VISIBLE
        }
    }

    private fun finishFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.commit()
    }
}