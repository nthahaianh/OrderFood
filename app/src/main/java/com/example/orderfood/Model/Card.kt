package com.example.orderfood.Model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Card(
    val id: String? = "",
    val uid: String? = "",
    var bank: String? = "",
    var name: String? = "",
    var numbercard: String? = ""
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "uid" to uid,
            "bank" to bank,
            "name" to name,
            "numbercard" to numbercard
        )
    }

    fun toInfor(): String {
        return "$bank\n$numbercard\n$name"
    }
}