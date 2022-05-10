package com.example.orderfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orderfood.Model.Food
import com.example.orderfood.R

class FoodsAdapter2(var list: MutableList<Food>) :
    RecyclerView.Adapter<FoodsAdapter2.ViewHolder>() {
    lateinit var view: View
    lateinit var itemClick: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodsAdapter2.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_foods_list_type2, parent, false)
        this.view = view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodsAdapter2.ViewHolder, position: Int) {
        val food = list[position]
        holder.tvNameFood.text = food.name
        holder.tvPrice.text = String.format("%.0f", food.price)
        Glide.with(view).load(food.image_uri).placeholder(R.drawable.image_loading)
            .error(R.drawable.image_error).into(holder.ivFood)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvNameFood: TextView = view.findViewById(R.id.item_foods_list_type2_tvNameOfFood)
        var tvPrice: TextView = view.findViewById(R.id.item_foods_list_type2_tvPrice)
        var ivFood: ImageView = view.findViewById(R.id.item_foods_list_type2_ivImage)

        //        var tvRate: TextView = view.findViewById(R.id.item_foods_list_type2_tvRate)
//        var ratingBar:RatingBar = view.findViewById(R.id.item_foods_list_type2_ratingBar)
        init {
            view.setOnClickListener { itemClick.invoke(adapterPosition) }
        }
    }

}