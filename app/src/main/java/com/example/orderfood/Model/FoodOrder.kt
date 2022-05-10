package com.example.orderfood.Model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FoodOrder(
    val id: String? = "",
    val type: String? = "",
    val name: String? = "",
    val price: Float? = 0F,
    val describe: String? = "",
    var rate: Float? = 0F,
    val image_uri: String? = "",
    var quantity: Int? = 0,
    var is_rate: Boolean? = false
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
            "quantity" to quantity,
            "is_rate" to is_rate
        )
    }
}