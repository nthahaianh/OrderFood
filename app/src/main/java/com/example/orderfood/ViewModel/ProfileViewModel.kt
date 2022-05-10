package com.example.orderfood.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.orderfood.Model.Card
import com.example.orderfood.Model.Comment
import com.example.orderfood.Model.MyAddress

class ProfileViewModel : ViewModel() {
    val fullBankName = arrayOf(
        "BIDV - Ngân hàng Thương mại cổ phần Đầu tư và Phát triển Việt Nam",
        "Vietcombank - Ngân hàng thương mại cổ phần Ngoại thương Việt Nam",
        "Viettinbank - Ngân hàng Thương mại cổ phần Công Thương Việt Nam",
        "MBbank - Ngân hàng Quân đội",
        "Agribank - Ngân hàng Nông nghiệp và Phát triển Nông thôn Việt Nam",
        "Techcombank - Ngân hàng Thương mại cổ phần Kỹ Thương Việt Nam"
    )

    private val mainRepository = MainRepository()
    var noti: MutableLiveData<String> = MutableLiveData()
    var notiAddress: MutableLiveData<String> = MutableLiveData()
    var userName: MutableLiveData<String> = MutableLiveData()
    var userEmail: MutableLiveData<String> = MutableLiveData()
    var vmAddressList: MutableLiveData<MutableList<MyAddress>> = MutableLiveData()
    var vmCardList: MutableLiveData<MutableList<Card>> = MutableLiveData()
    var isUpdateAddressList: MutableLiveData<Boolean> = MutableLiveData()
    var isUpdateCardList: MutableLiveData<Boolean> = MutableLiveData()
    var shortBankName: MutableLiveData<Array<String>> = MutableLiveData()
    var bankName: MutableLiveData<String> = MutableLiveData()
    var vmCommentList: MutableLiveData<MutableList<Comment>> = MutableLiveData()
    var isUpdateComment: MutableLiveData<Boolean> = MutableLiveData()
    var isChangePassSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var isUpdateEmailSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var isUpdatenameSuccess: MutableLiveData<Boolean> = MutableLiveData()

    init {
        noti.value = ""
        notiAddress.value = ""
        userName.value = ""
        userEmail.value = ""
        bankName.value = "BIDV"
        vmAddressList.value = mutableListOf()
        vmCardList.value = mutableListOf()
        vmCommentList.value = mutableListOf()
        shortBankName.value = arrayOf("BIDV", "Vietcombank", "Viettinbank", "MBbank", "Agribank", "Techcombank")
        isUpdateAddressList.value = false
        isUpdateCardList.value = false
        isUpdateComment.value = false
        isChangePassSuccess.value = false
        isUpdateEmailSuccess.value = false
        isUpdatenameSuccess.value = false
    }

    fun onGetProfile() {
        mainRepository.getProfile(userName, userEmail)
    }

    fun onChangePassword(newPassword: String, oldPassword: String) {
        mainRepository.reAuthenticateAndChangePassword(
            newPassword,
            oldPassword,
            noti,
            isChangePassSuccess
        )
    }

    fun onUpdateName(fullname: String) {
        mainRepository.updateFullname(fullname, userName,isUpdatenameSuccess, noti)
    }

    fun onUpdateEmail(email: String, password: String) {
        mainRepository.reAuthenticateAndChangeEmail(email, password, userEmail,isUpdateEmailSuccess, noti)
    }

    fun onSignOut() {
        mainRepository.signOut()
    }

    fun checkPasswordInput(password: String): Boolean {
        val pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,30}$".toRegex()

        return pattern.matches(password)
    }

    fun checkPasswordMatch(password: String, passwordAgain: String): Boolean {
        return password.equals(passwordAgain)
    }

    fun onAddAddress(phone: String, address: String) {
        mainRepository.addAddress(phone, address, notiAddress)
    }

    fun onUpdateAddress(address: MyAddress) {
        mainRepository.updateAddress(address)
    }

    fun onGetAddressList() {
        vmAddressList.value!!.clear()
        mainRepository.getAddressList(vmAddressList, isUpdateAddressList)
    }

    fun onRemoveAddress(myAddress: MyAddress) {
        mainRepository.removeAddress(myAddress, isUpdateAddressList)
    }

    fun setNameOfBank(position: Int) {
        bankName.value = fullBankName[position]
    }

    fun onAddCard(cardNumber: String, name: String) {
        mainRepository.addCard(bankName.value!!, cardNumber, name)
    }

    fun onGetCardList() {
        vmCardList.value!!.clear()
        mainRepository.getCardList(vmCardList, isUpdateCardList)
    }

    fun onRemoveCard(myCard: Card) {
        mainRepository.removeCard(myCard, isUpdateCardList)
    }

    fun onGetCommentList() {
        mainRepository.getCommentListOfUser(vmCommentList, isUpdateComment)
    }
}