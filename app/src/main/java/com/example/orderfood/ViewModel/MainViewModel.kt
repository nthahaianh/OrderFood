package com.example.orderfood.ViewModel

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.orderfood.Model.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*

class MainViewModel : ViewModel() {
    private val mainRepository = MainRepository()
    var vmFoodsList: MutableLiveData<MutableList<Food>> = MutableLiveData()
    var vmFoodsSearch: MutableLiveData<MutableList<Food>> = MutableLiveData()
    var vmFoodsList0: MutableLiveData<MutableList<Food>> = MutableLiveData()
    var vmFoodsList1: MutableLiveData<MutableList<Food>> = MutableLiveData()
    var vmFoodsList2: MutableLiveData<MutableList<Food>> = MutableLiveData()
    var vmFoodsList3: MutableLiveData<MutableList<Food>> = MutableLiveData()
    var vmFoodsList4: MutableLiveData<MutableList<Food>> = MutableLiveData()
    var vmFavoriteList: MutableLiveData<MutableList<Food>> = MutableLiveData()
    var vmReceiptList: MutableLiveData<MutableList<Receipt>> = MutableLiveData()
    var vmFoodOrderList: MutableLiveData<MutableList<FoodOrder>> = MutableLiveData()
    var vmCommentList: MutableLiveData<MutableList<Comment>> = MutableLiveData()
    var vmFoodSelect: MutableLiveData<Food> = MutableLiveData()
    var vmFoodOrderSelect: MutableLiveData<FoodOrder> = MutableLiveData()
    var vmTypeFoodSelect: MutableLiveData<String> = MutableLiveData()
    var isShowCart: MutableLiveData<Boolean> = MutableLiveData()
    var isUpdateFavorite: MutableLiveData<Boolean> = MutableLiveData()
    var isUpdateList: MutableLiveData<Boolean> = MutableLiveData()
    var isFavorite: MutableLiveData<Boolean> = MutableLiveData()
    var isUpdateReceipts: MutableLiveData<Boolean> = MutableLiveData()
    var isUpdateFoodOrder: MutableLiveData<Boolean> = MutableLiveData()
    var vmReceiptSelect: MutableLiveData<Receipt> = MutableLiveData()
    var isDisplayToolbar: MutableLiveData<Boolean> = MutableLiveData()
    var isAddFoodOrderSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var isUpdateComment: MutableLiveData<Boolean> = MutableLiveData()

    init {
        vmFoodsList.value = mutableListOf()
        vmFoodsSearch.value = mutableListOf()
        vmFoodsList0.value = mutableListOf()
        vmFoodsList1.value = mutableListOf()
        vmFoodsList2.value = mutableListOf()
        vmFoodsList3.value = mutableListOf()
        vmFoodsList4.value = mutableListOf()
        vmFavoriteList.value = mutableListOf()
        vmReceiptList.value = mutableListOf()
        vmFoodOrderList.value = mutableListOf()
        vmCommentList.value = mutableListOf()
        vmFoodSelect.value = Food()
        vmReceiptSelect.value = Receipt()
        vmFoodOrderSelect.value = FoodOrder()
        vmTypeFoodSelect.value = "0"
        isShowCart.value = false
        isFavorite.value = false
        isUpdateFavorite.value = false
        isUpdateList.value = false
        isUpdateReceipts.value = false
        isUpdateFoodOrder.value = false
        isDisplayToolbar.value = true
        isAddFoodOrderSuccess.value = false
        isUpdateComment.value = false
    }

    fun signInWithAnonymously(activity: Activity) {
        mainRepository.signInAnonymously(activity)
        getFoodFavorite()
    }

    fun isLogin(): Boolean {
        return mainRepository.isLogin()
    }

    fun isLoginEmail(): Boolean {
        return mainRepository.isLoginWithEmail()
    }

    fun getFoodsData() {
        vmFoodsList.value?.clear()
        mainRepository.getFoodsList(
            vmFoodsList,
            vmFoodsList0,
            vmFoodsList1,
            vmFoodsList2,
            vmFoodsList3,
            vmFoodsList4,
            isUpdateList
        )
    }

    fun getRecommendFood() {
        vmFoodsList.value?.sortBy { food: Food -> food.price }
    }

    fun searchFood(searchStr: String) {
        vmFoodsSearch.value?.clear()
        searchStr.toLowerCase()
        for (item in vmFoodsList.value!!) {
            if (item.name?.toLowerCase()?.contains(searchStr)!!) {
                vmFoodsSearch.value?.add(item)
            }
        }
    }

    fun getFoodFavorite() {
        vmFavoriteList.value?.clear()
        mainRepository.getFavoriteList(vmFavoriteList, isUpdateList)
    }

    fun addToCart(food: FoodCart) {
        mainRepository.addFoodToCart(food)
    }

    fun checkFavorite() {
        for (item in vmFavoriteList.value!!) {
            if (item == vmFoodSelect.value) {
                isFavorite.value = true
                return
            }
        }
        isFavorite.value = false
    }

    fun addFoodFavorite() {
        mainRepository.addFoodToFavorite(vmFoodSelect.value!!, isFavorite)
    }

    fun removeFoodFavorite() {
        mainRepository.removeFoodFavorite(vmFoodSelect.value!!, isFavorite)
    }

    fun getReceiptList() {
        vmReceiptList.value?.clear()
        mainRepository.getReceiptList(vmReceiptList, isUpdateReceipts)
    }

    fun getFoodOrderList() {
        vmFoodOrderList.value?.clear()
        mainRepository.getFoodsOrderList(vmFoodOrderList, vmReceiptSelect, isUpdateFoodOrder)
    }

    fun setUpFoodSelected(foodOrder: FoodOrder) {
        val food = Food(
            foodOrder.id,
            foodOrder.type,
            foodOrder.name,
            foodOrder.price,
            foodOrder.describe,
            foodOrder.rate,
            foodOrder.image_uri
        )
        vmFoodSelect.value = food
    }

    fun addNewRate(rate: Float, content: String) {
        mainRepository.addComment(
            rate,
            content,
            getCurrentTime(),
            vmFoodOrderSelect.value!!,
            isAddFoodOrderSuccess
        )
    }

    fun updateFoodOrder() {
        vmFoodOrderSelect.value?.is_rate = true
        mainRepository.updateFoodOrderRate(vmFoodOrderSelect, vmReceiptSelect, isUpdateFoodOrder)
    }

    fun getComments() {
        vmCommentList.value?.clear()
        val id = vmFoodSelect.value!!.id
        if (id != null) {
            mainRepository.getCommentListOfFood(id, vmCommentList, isUpdateComment)
        }
    }

    fun QRCode(): Bitmap {
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(
            vmReceiptSelect.value.toString(),
            BarcodeFormat.QR_CODE,
            500,
            500
        )
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