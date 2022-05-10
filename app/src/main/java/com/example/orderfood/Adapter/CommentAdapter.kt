package com.example.orderfood.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orderfood.Model.Comment
import com.example.orderfood.R

class CommentAdapter (var list: MutableList<Comment>, var type: Int):RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    lateinit var view: View
//    lateinit var itemClick: (position: Int) -> Unit
//    fun setCallBack(click: (position: Int) -> Unit) {
//        itemClick = click
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment_list, parent, false)
        this.view = view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        val comment = list[position]
        holder.tvContent.text = comment.content
        holder.tvTime.text = comment.time
        holder.ratingBar.rating = comment.rate!!
        if(type == 0){
            holder.tvName.text = comment.user_name
            Glide.with(view).load(comment.user_avatar).into(holder.ivImage)
        }else{
            holder.tvName.text = comment.food_name
            Glide.with(view).load(comment.food_image).into(holder.ivImage)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        var tvName: TextView = view.findViewById(R.id.item_comment_list_tvName)
        var tvContent: TextView = view.findViewById(R.id.item_comment_list_tvContent)
        var tvTime: TextView = view.findViewById(R.id.item_comment_list_tvTime)
        var ivImage: ImageView = view.findViewById(R.id.item_comment_list_ivImage)
        var ratingBar: RatingBar = view.findViewById(R.id.item_comment_list_ratingBar)

        init {
//            view.setOnClickListener { itemClick.invoke(adapterPosition) }
        }
    }

}