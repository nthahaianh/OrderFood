package com.example.orderfood.Model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Receipt(
    var id:String?="",
    var uid: String? = "",
    var time: String? = "",
    var message: String? = "",
    var payment: String? = "",
    var phone: String? = "",
    var address: String? = "",
    var total:String?=""
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "id" to id,
            "time" to time,
            "message" to message,
            "payment" to payment,
            "phone" to phone,
            "address" to address,
            "total" to total
        )
    }
}