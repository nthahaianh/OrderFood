package com.example.orderfood.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.orderfood.R
import com.example.orderfood.ViewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile_fragment_add_card.*

class AddCardFragment : Fragment(){
    lateinit var profileViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.activity_profile_fragment_add_card, container, false)
        profileViewModel = activity?.let { ViewModelProvider(it).get(ProfileViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    private fun setUpView() {
        fragment_add_card_ivBack.setOnClickListener { finishFragment() }
        fragment_add_card_btnAdd.setOnClickListener {
            val cardNumber = fragment_add_card_etCardNumber.text.toString().trim()
            val fullname = fragment_add_card_etFullName.text.toString().trim()
            if(cardNumber != "" && fullname != ""){
                profileViewModel.onAddCard(cardNumber,fullname)
                finishFragment()
            }else{
                Toast.makeText(context,"Card number or name is empty",Toast.LENGTH_SHORT).show()
            }
        }
        val list = profileViewModel.shortBankName.value
        val arrayAdapter = context?.let { ArrayAdapter(it,android.R.layout.simple_spinner_item,list!!) }
        fragment_add_card_spinnerBank.adapter = arrayAdapter
        fragment_add_card_spinnerBank.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                profileViewModel.setNameOfBank(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

    }
    private fun finishFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.commit()
    }
}