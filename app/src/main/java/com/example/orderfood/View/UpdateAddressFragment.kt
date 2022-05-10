package com.example.orderfood.View

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.Adapter.AddressAdapter
import com.example.orderfood.Model.MyAddress
import com.example.orderfood.R
import com.example.orderfood.ViewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile_fragment_update_address.*

class UpdateAddressFragment : Fragment() {
    lateinit var profileViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(
            R.layout.activity_profile_fragment_update_address,
            container,
            false
        )
        profileViewModel =ViewModelProvider(this).get(ProfileViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI()
        setUpView()
    }

    private fun connectUI() {
        profileViewModel.onGetAddressList()
        setUpRecyclerView()
    }

    private fun setUpView() {
        fragment_update_address_btnAdd.setOnClickListener {
            showDialogAdd()
        }
        fragment_update_address_ivBack.setOnClickListener { finishFragment() }
    }

    private fun setUpRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        var adapter = profileViewModel.vmAddressList.value?.let { AddressAdapter(it) }
        adapter?.setCallBack {
            var address = adapter.list[it]
            showDialogUpdateAddress(address)
        }
        adapter?.setCallBackClickRemove {
            onDeleteAddress(adapter.list[it])
        }
        fragment_update_address_rvAddress.layoutManager = layoutManager
        fragment_update_address_rvAddress.adapter = adapter
        profileViewModel.vmAddressList.observe(viewLifecycleOwner, Observer {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        })
        profileViewModel.isUpdateAddressList.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                adapter?.list = profileViewModel.vmAddressList.value!!
                adapter?.notifyDataSetChanged()
                profileViewModel.isUpdateAddressList.value = false
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showDialogUpdateAddress(address: MyAddress) {
        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_address)

        val window: Window = dialog?.window ?: return
        val gravity = Gravity.CENTER

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttributes: WindowManager.LayoutParams? = window.attributes
        windowAttributes?.gravity = gravity
        window.attributes = windowAttributes
        if (Gravity.BOTTOM == gravity) dialog.setCancelable(true) else dialog.setCancelable(false)

        val tvTitle = dialog.findViewById<TextView>(R.id.dialog_address_tvTitle)
        val etPhone = dialog.findViewById<EditText>(R.id.dialog_address_etPhone)
        val etAddress = dialog.findViewById<EditText>(R.id.dialog_address_etAddress)
        val btnCancel = dialog.findViewById<Button>(R.id.dialog_address_btnCancel)
        val btnOk = dialog.findViewById<Button>(R.id.dialog_address_btnOk)

        tvTitle?.text = "Update address"
        etPhone?.setText(address.phone)
        etAddress?.setText(address.address)
        btnCancel?.setOnClickListener { dialog.dismiss() }
        btnOk?.setOnClickListener {
            val newPhone = etPhone?.text.toString().trim()
            val newAddress = etAddress?.text.toString().trim()
            if(!(newPhone == "" && newAddress == "")){
                address.phone = newPhone
                address.address = newAddress
                profileViewModel.onUpdateAddress(address)
                dialog.dismiss()
            }else{
                Toast.makeText(context,"Phone or address is empty",Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    fun onDeleteAddress(myAddress: MyAddress) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Remove address")
            .setMessage("Are you sure remove this address?")
            .setNegativeButton("No") { _: DialogInterface, _: Int -> }
            .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                profileViewModel.onRemoveAddress(myAddress)
            }
            .show()
    }

    private fun showDialogAdd() {
        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_address)

        val window: Window = dialog?.window ?: return
        val gravity = Gravity.CENTER

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttributes: WindowManager.LayoutParams = window.attributes
        windowAttributes.gravity = gravity
        window.attributes = windowAttributes

        if (Gravity.BOTTOM == gravity) dialog.setCancelable(true) else dialog.setCancelable(false)

        val etPhone = dialog.findViewById<EditText>(R.id.dialog_address_etPhone)
        val etAddress = dialog.findViewById<EditText>(R.id.dialog_address_etAddress)
        val btnCancel = dialog.findViewById<Button>(R.id.dialog_address_btnCancel)
        val btnOk = dialog.findViewById<Button>(R.id.dialog_address_btnOk)

        btnCancel?.setOnClickListener { dialog.dismiss() }
        btnOk?.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            val address = etAddress.text.toString().trim()
            profileViewModel.onAddAddress(phone, address)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun finishFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.commit()
    }
}