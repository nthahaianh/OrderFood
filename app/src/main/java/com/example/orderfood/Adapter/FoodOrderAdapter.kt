package com.example.orderfood.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orderfood.Model.FoodOrder
import com.example.orderfood.R

class FoodOrderAdapter(var list: MutableList<FoodOrder>) :
    RecyclerView.Adapter<FoodOrderAdapter.ViewHolder>() {
    lateinit var view: View
    lateinit var itemClick: (position: Int) -> Unit
    lateinit var itemClickRate: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }

    fun setCallBackClickRate(click: (position: Int) -> Unit) {
        itemClickRate = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodOrderAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_food_order, parent, false)
        this.view = view
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FoodOrderAdapter.ViewHolder, position: Int) {
        val foodOrder = list[position]
        holder.tvNameOfFood.text = foodOrder.name
        holder.tvPrice.text = String.format("%.0f", foodOrder.price)
        holder.tvQuantity.text = foodOrder.quantity.toString()
        Glide.with(view).load(foodOrder.image_uri).placeholder(R.drawable.image_loading)
            .error(R.drawable.image_error).into(holder.ivImage)
        if (foodOrder.is_rate == true) {
            holder.tvRate.visibility = View.GONE
        } else {
            holder.tvRate.text = "Rate"
            holder.tvRate.visibility = View.VISIBLE
        }
        holder.tvRate.setOnClickListener {
            itemClickRate.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvNameOfFood: TextView = view.findViewById(R.id.item_food_order_tvNameOfFood)
        var tvPrice: TextView = view.findViewById(R.id.item_food_order_tvPrice)
        var tvQuantity: TextView = view.findViewById(R.id.item_food_order_tvQuantity)
        var tvRate: TextView = view.findViewById(R.id.item_food_order_tvRate)
        var ivImage: ImageView = view.findViewById(R.id.item_food_order_ivImage)

        init {
            view.setOnClickListener { itemClick.invoke(adapterPosition) }
        }
    }

}