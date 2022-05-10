package com.example.orderfood.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.Model.Card
import com.example.orderfood.R

class CardAdapter2 (var list: MutableList<Card>,var itemSelected:Int):RecyclerView.Adapter<CardAdapter2.ViewHolder>() {
    lateinit var view: View
    lateinit var itemClick: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAdapter2.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card2, parent, false)
        this.view = view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardAdapter2.ViewHolder, position: Int) {
        val cardInfor = list[position]
        holder.tvBankName.text = cardInfor.bank
        holder.tvCardNumber.text = cardInfor.numbercard
        holder.tvFullName.text = cardInfor.name
        if (itemSelected == position){
            holder.tvCardNumber.visibility = View.VISIBLE
            holder.tvFullName.visibility = View.VISIBLE
        }else{
            holder.tvCardNumber.visibility = View.GONE
            holder.tvFullName.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var tvBankName: TextView = view.findViewById(R.id.item_card2_tvNameBank)
        var tvCardNumber: TextView = view.findViewById(R.id.item_card2_tvNumberCard)
        var tvFullName: TextView = view.findViewById(R.id.item_card2_tvNameUser)

        init {
            view.setOnClickListener { itemClick.invoke(adapterPosition) }
        }
    }

}