package com.example.orderfood.Model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FoodCart(
    val id: String? = "",
    val type: String? = "",
    val name: String? = "",
    val price: Float? = 0F,
    val describe: String? = "",
    val rate: Float? = 0F,
    val image_uri: String? = "",
    var quantity:Int? = 0
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "type" to type,
            "name" to name,
            "price" to price,
            "describe" to describe,
            "rate" to rate,
            "image_uri" to image_uri,
            "quantity" to quantity
        )
    }
}