package com.example.orderfood.View

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.Adapter.CardAdapter
import com.example.orderfood.Model.Card
import com.example.orderfood.R
import com.example.orderfood.ViewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile_fragment_update_card.*

class UpdateCardFragment : Fragment() {
    lateinit var profileViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =
            inflater.inflate(R.layout.activity_profile_fragment_update_card, container, false)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI()
        setUpView()
    }

    private fun connectUI() {
        profileViewModel.onGetCardList()
        setUpRecyclerView()
    }


    private fun setUpView() {
        fragment_update_card_ivBack.setOnClickListener {
            finishFragment()
        }
        fragment_update_card_btnAdd.setOnClickListener {
            replaceFragment(AddCardFragment())
        }
    }

    private fun setUpRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        var adapter = profileViewModel.vmCardList.value?.let { CardAdapter(it) }
        adapter?.setCallBack {
            var card = adapter.list[it]
        }
        adapter?.setCallBackClickRemove {
            val card = adapter.list[it]
            onDeleteCard(card)
        }
        fragment_update_card_rvCard.layoutManager = layoutManager
        fragment_update_card_rvCard.adapter = adapter
        profileViewModel.vmCardList.observe(viewLifecycleOwner, Observer {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        })
        profileViewModel.isUpdateCardList.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                adapter?.list = profileViewModel.vmCardList.value!!
                adapter?.notifyDataSetChanged()
                profileViewModel.isUpdateCardList.value = false
            }
        })
    }

    private fun finishFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.add(R.id.act_profile_container, fragment)
        transaction.addToBackStack("personal")
        transaction.commit()
    }

    fun onDeleteCard(card: Card) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Remove card")
            .setMessage("Are you sure remove this card?")
            .setNegativeButton("No") { _: DialogInterface, _: Int -> }
            .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                profileViewModel.onRemoveCard(card)
            }
            .show()
    }
}