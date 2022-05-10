package com.example.orderfood.Model

import android.net.Uri
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Comment(
    var id:String?="",
    var uid: String? = "",
    var foodid: String? = "",
    val rate: Float? = 0F,
    var content: String? = "",
    var time: String? = "",
    var user_avatar: String? = "",
    var user_name:String?="",
    var food_name:String?="",
    var food_image:String?=""
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "uid" to uid,
            "foodid" to foodid,
            "rate" to rate,
            "content" to content,
            "time" to time,
            "user_avatar" to user_avatar,
            "user_name" to user_name,
            "food_name" to food_name,
            "food_image" to food_image
        )
    }
}