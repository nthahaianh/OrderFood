package com.example.orderfood.Model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class MyAddress(
    val id: String? = "",
    val uid: String? = "",
    var phone: String? = "",
    var address: String? = ""
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "uid" to uid,
            "phone" to phone,
            "address" to address
        )
    }
}