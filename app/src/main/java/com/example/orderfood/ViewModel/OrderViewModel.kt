package com.example.orderfood.ViewModel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.orderfood.Model.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*

class OrderViewModel : ViewModel() {
    private val mainRepository = MainRepository()
    private var foodOrderList: MutableList<FoodOrder> = mutableListOf()
    private var message: String = ""
    private var time: String = ""
    private var total: String = ""
    var vmFoodsListInCart: MutableLiveData<MutableList<FoodCart>> = MutableLiveData()
    var vmAddressList: MutableLiveData<MutableList<MyAddress>> = MutableLiveData()
    var vmCardList: MutableLiveData<MutableList<Card>> = MutableLiveData()
    var isUpdateCart: MutableLiveData<Boolean> = MutableLiveData()
    var isUpdateAddressList: MutableLiveData<Boolean> = MutableLiveData()
    var isUpdateCardList: MutableLiveData<Boolean> = MutableLiveData()
    var isReAuthenticateSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var isAddSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var paymentOrder: MutableLiveData<String> = MutableLiveData()
    var phoneOrder: MutableLiveData<String> = MutableLiveData()
    var addressOrder: MutableLiveData<String> = MutableLiveData()
    var vmReceipt: MutableLiveData<Receipt> = MutableLiveData()

    init {
        vmFoodsListInCart.value = mutableListOf()
        vmAddressList.value = mutableListOf()
        vmCardList.value = mutableListOf()
        isUpdateCart.value = false
        isUpdateAddressList.value = false
        isUpdateCardList.value = false
        isReAuthenticateSuccess.value = false
        paymentOrder.value = "Direct payment"
        phoneOrder.value = ""
        addressOrder.value = "At restaurant"
        vmReceipt.value = null
    }

    fun isLoginEmail(): Boolean {
        return mainRepository.isLoginWithEmail()
    }

    fun getCartData() {
        vmFoodsListInCart.value?.clear()
        mainRepository.getFoodsInCart(vmFoodsListInCart, isUpdateCart)
    }

    fun updateQuantity(food: FoodCart) {
        mainRepository.updateQuatityFoodsInCart(food)
    }

    fun removeFood(foodCart: FoodCart) {
        mainRepository.removeFoodInCart(foodCart)
    }

    fun getAddressList() {
        vmAddressList.value!!.clear()
        mainRepository.getAddressList(vmAddressList, isUpdateAddressList)
    }

    fun getCardList() {
        vmCardList.value!!.clear()
        mainRepository.getCardList(vmCardList, isUpdateCardList)
    }

    fun setPhoneOrder(string: String) {
        phoneOrder.value = string
    }

    fun setAddressOrder(string: String) {
        addressOrder.value = string
    }

    fun setPaymentOrder(string: String) {
        paymentOrder.value = string
    }

    fun getMassage(): String {
        return message
    }

    fun getTime(): String {
        return time
    }

    fun setTotal(string: String) {
        total = string
    }

    fun setUpReceipt(string: String) {
        for (item in vmFoodsListInCart.value!!) {
            val foodOrder = FoodOrder(
                item.id,
                item.type,
                item.name,
                item.price,
                item.describe,
                item.rate,
                item.image_uri,
                item.quantity,
                false
            )
            foodOrderList.add(foodOrder)
        }
        message = string
        time = getCurrentTime()
        Log.e(
            "receipt",
            "$time, ${paymentOrder.value}, ${addressOrder.value},${phoneOrder.value},$message"
        )
    }

    fun onAddReceipt() {
        mainRepository.addReceipt(
            vmReceipt,
            time,
            paymentOrder.value,
            addressOrder.value,
            phoneOrder.value,
            message,
            total,
            foodOrderList,
            isAddSuccess
        )
    }

    fun reAuthenticate(password: String) {
        mainRepository.reAuthenticate(password, isReAuthenticateSuccess)
    }

    fun QRCode(): Bitmap {
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(
            vmReceipt.value.toString(),
            BarcodeFormat.QR_CODE,
            500,
            500
        )
        Log.e("QRCode", "${vmReceipt.value}")
        val barcodeEncoder = BarcodeEncoder()
        var bitmap = barcodeEncoder.createBitmap(bitMatrix)
        return bitmap
    }

    fun getCurrentTime(): String {
        var calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val date = calendar.get(Calendar.DATE)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return "$date/$month/$year  ${hour}h ${minute}min"
    }
}