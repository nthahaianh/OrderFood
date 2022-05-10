package com.example.orderfood.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.Model.Receipt
import com.example.orderfood.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class ReceiptAdapter(var list: MutableList<Receipt>) :
    RecyclerView.Adapter<ReceiptAdapter.ViewHolder>() {
    lateinit var view: View
    lateinit var itemClick: (position: Int) -> Unit
    fun setCallBack(click: (position: Int) -> Unit) {
        itemClick = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receipt, parent, false)
        this.view = view
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReceiptAdapter.ViewHolder, position: Int) {
        val receipt = list[position]
        holder.tvTotal.text = receipt.total
        holder.tvTime.text = receipt.time
        if (receipt.phone.equals("")) {
            holder.tvDelivery.text = "At restaurant"
        } else {
            holder.tvDelivery.text = "Shipping"
        }
        if (receipt.payment.equals("Direct payment")) {
            holder.tvPayment.text = receipt.payment
        } else {
            holder.tvPayment.text = "Online payment"
        }
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(
            receipt.toString(),
            BarcodeFormat.QR_CODE,
            200,
            200
        )
        val barcodeEncoder = BarcodeEncoder()
        var bitmap = barcodeEncoder.createBitmap(bitMatrix)
        holder.btnQRCode.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvTotal: TextView = view.findViewById(R.id.item_receipt_tvTotal)
        var tvTime: TextView = view.findViewById(R.id.item_receipt_tvTime)
        var tvDelivery: TextView = view.findViewById(R.id.item_receipt_tvDelivery)
        var tvPayment: TextView = view.findViewById(R.id.item_receipt_tvPayment)
        var btnQRCode: ImageView = view.findViewById(R.id.item_receipt_qrCode)

        init {
            view.setOnClickListener { itemClick.invoke(adapterPosition) }
        }
    }

}