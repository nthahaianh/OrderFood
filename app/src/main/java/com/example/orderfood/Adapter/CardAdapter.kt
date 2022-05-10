package com.example.orderfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.Model.Card
import com.example.orderfood.R

class CardAdapter (var list: MutableList<Card>):RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    lateinit var view: View
    lateinit var itemClick: (position: Int) -> Unit
    lateinit var itemClickRemove: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }
    fun setCallBackClickRemove(click: (position: Int) -> Unit) {
        itemClickRemove = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        this.view = view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardAdapter.ViewHolder, position: Int) {
        val cardInfor = list[position]
        holder.tvBankName.text = cardInfor.bank
        holder.tvCardNumber.text = cardInfor.numbercard
        holder.tvFullName.text = cardInfor.name
        holder.btnRemove.setOnClickListener {
            itemClickRemove.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        var tvBankName: TextView = view.findViewById(R.id.item_card_tvNameBank)
        var tvCardNumber: TextView = view.findViewById(R.id.item_card_tvNumberCard)
        var tvFullName: TextView = view.findViewById(R.id.item_card_tvNameUser)
        var btnRemove: Button = view.findViewById(R.id.item_card_btnRemove)

        init {
            view.setOnClickListener { itemClick.invoke(adapterPosition) }
        }
    }

}