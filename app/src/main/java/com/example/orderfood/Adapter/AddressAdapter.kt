package com.example.orderfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orderfood.Model.FoodCart
import com.example.orderfood.Model.MyAddress
import com.example.orderfood.R

class AddressAdapter (var list: MutableList<MyAddress>):RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
    lateinit var view: View
    lateinit var itemClick: (position: Int) -> Unit
    lateinit var itemClickRemove: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }
    fun setCallBackClickRemove(click: (position: Int) -> Unit) {
        itemClickRemove = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_address_list, parent, false)
        this.view = view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressAdapter.ViewHolder, position: Int) {
        val myAddress = list[position]
        holder.tvAddress.text = myAddress.address
        holder.tvPhone.text = myAddress.phone
        holder.ivRemove.setOnClickListener { itemClickRemove.invoke(position) }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        var tvPhone: TextView = view.findViewById(R.id.item_address_list_tvPhone)
        var tvAddress: TextView = view.findViewById(R.id.item_address_list_tvAddress)
        var ivRemove: ImageView = view.findViewById(R.id.item_address_list_ivRemove)

        init {
            view.setOnClickListener { itemClick.invoke(adapterPosition) }
        }
    }

}