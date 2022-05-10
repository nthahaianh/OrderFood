package com.example.orderfood.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orderfood.Model.FoodCart
import com.example.orderfood.R

class FoodsCartAdapter(var list: MutableList<FoodCart>, val type: Int) :
    RecyclerView.Adapter<FoodsCartAdapter.ViewHolder>() {
    lateinit var view: View
    lateinit var itemClick: (position: Int) -> Unit
    lateinit var itemClickRemove: (position: Int) -> Unit
    lateinit var itemClickPlus: (position: Int) -> Unit
    lateinit var itemClickMinus: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }

    fun setCallBackClickPlus(click: (position: Int) -> Unit) {
        itemClickPlus = click
    }

    fun setCallBackClickMinus(click: (position: Int) -> Unit) {
        itemClickMinus = click
    }

    fun setCallBackClickRemove(click: (position: Int) -> Unit) {
        itemClickRemove = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodsCartAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_foods_in_cart, parent, false)
        this.view = view
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: FoodsCartAdapter.ViewHolder, position: Int) {
        val food = list[position]
        holder.tvNameFood.text = food.name
        holder.tvPrice.text = String.format("%.0f", food.price)
        holder.etQuantity.text = food.quantity.toString()
        Glide.with(view).load(food.image_uri).placeholder(R.drawable.image_loading)
            .error(R.drawable.image_error).into(holder.ivFood)
        holder.tvPlus.setOnClickListener { itemClickPlus.invoke(position) }
        holder.tvMinus.setOnClickListener { itemClickMinus.invoke(position) }
        holder.ivRemove.setOnClickListener { itemClickRemove.invoke(position) }
        if (type == 0) {
            holder.etQuantity.text = "x" + food.quantity.toString()
            holder.tvPlus.visibility = View.GONE
            holder.tvMinus.visibility = View.GONE
            holder.ivRemove.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvNameFood: TextView = view.findViewById(R.id.item_foods_in_cart_tvNameOfFood)
        var tvPrice: TextView = view.findViewById(R.id.item_foods_in_cart_tvPrice)
        var ivFood: ImageView = view.findViewById(R.id.item_foods_in_cart_ivImage)
        var ivRemove: ImageView = view.findViewById(R.id.item_foods_in_cart_ivRemove)
        var etQuantity: TextView = view.findViewById(R.id.item_foods_in_cart_etQuantity)
        var tvPlus: TextView = view.findViewById(R.id.btnPlus)
        var tvMinus: TextView = view.findViewById(R.id.btnMinus)

        init {
            view.setOnClickListener { itemClick.invoke(adapterPosition) }
        }
    }

}