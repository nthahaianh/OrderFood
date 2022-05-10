package com.example.orderfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orderfood.Model.Food
import com.example.orderfood.R

class FoodsAdapter(var list: MutableList<Food>) : RecyclerView.Adapter<FoodsAdapter.ViewHolder>() {
    lateinit var view: View
    lateinit var itemClick: (position: Int) -> Unit
    lateinit var itemClickAdd: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }

    fun setCallBackClickAdd(click: (position: Int) -> Unit) {
        itemClickAdd = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodsAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_foods_list_type1, parent, false)
        this.view = view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodsAdapter.ViewHolder, position: Int) {
        val food = list[position]
        holder.tvNameFood.text = food.name
        holder.tvPrice.text = String.format("%.0f", food.price)
        Glide.with(view).load(food.image_uri).placeholder(R.drawable.image_loading)
            .error(R.drawable.image_error).into(holder.ivFood)
        holder.btnAdd.setOnClickListener {
            itemClickAdd.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvNameFood: TextView = view.findViewById(R.id.item_foods_list_type1_tvNameOfFood)
        var tvPrice: TextView = view.findViewById(R.id.item_foods_list_type1_tvPrice)
        var btnAdd: Button = view.findViewById(R.id.item_foods_list_type1_btnAdd)
        var ivFood: ImageView = view.findViewById(R.id.item_foods_list_type1_ivImage)

        init {
            view.setOnClickListener { itemClick.invoke(adapterPosition) }
        }
    }

}