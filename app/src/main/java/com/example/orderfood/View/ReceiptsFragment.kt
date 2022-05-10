package com.example.orderfood.View

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.Adapter.ReceiptAdapter
import com.example.orderfood.R
import com.example.orderfood.ViewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main_fragment_receipts.*

class ReceiptsFragment : Fragment() {
    lateinit var mMainViewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.activity_main_fragment_receipts, container, false)
        mMainViewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI()
        setUpView()
    }

    override fun onDestroy() {
        mMainViewModel.isDisplayToolbar.value = true
        super.onDestroy()
    }

    private fun setUpView() {
        fragment_receipts_ivBack.setOnClickListener { finishFragment() }
        mMainViewModel.isDisplayToolbar.value = false
    }

    private fun connectUI() {
        mMainViewModel.getReceiptList()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = ReceiptAdapter(mMainViewModel.vmReceiptList.value!!)
        adapter.setCallBack {
            mMainViewModel.vmReceiptSelect.value = adapter.list[it]
            replaceFragment(DetailReceiptFragment())
        }
        fragment_receipts_rvReceipt.layoutManager = layoutManager
        fragment_receipts_rvReceipt.adapter = adapter
        mMainViewModel.vmReceiptList.observe(viewLifecycleOwner, Observer {
            adapter.list = it
            adapter.notifyDataSetChanged()
        })
        mMainViewModel.isUpdateReceipts.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                adapter.list = mMainViewModel.vmReceiptList.value!!
                adapter.notifyDataSetChanged()
                mMainViewModel.isUpdateReceipts.value = false
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