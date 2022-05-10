package com.example.orderfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.Model.MyAddress
import com.example.orderfood.R

class AddressAdapter2 (var list: MutableList<MyAddress>,var itemChoose:Int):RecyclerView.Adapter<AddressAdapter2.ViewHolder>() {
    lateinit var view: View
    lateinit var itemClick: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressAdapter2.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_address_list2, parent, false)
        this.view = view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressAdapter2.ViewHolder, position: Int) {
        val myAddress = list[position]
        holder.tvPhone.text = myAddress.phone
        holder.tvAddress.text = myAddress.address
        holder.rbChoose.isChecked = position == itemChoose
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        var tvPhone: TextView = view.findViewById(R.id.item_address_list2_tvPhone)
        var tvAddress: TextView = view.findViewById(R.id.item_address_list2_tvAddress)
        var rbChoose: RadioButton = view.findViewById(R.id.item_address_list2_rbChoose)

        init {
            view.setOnClickListener { itemClick.invoke(adapterPosition) }
        }
    }

}