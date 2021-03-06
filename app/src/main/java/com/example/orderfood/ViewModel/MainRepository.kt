package com.example.orderfood.ViewModel

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.orderfood.Model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainRepository {
    fun signUpWithEmail(
        email: String,
        password: String,
        activity: Activity,
        isSuccess: MutableLiveData<Boolean>
    ) {
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        if (user != null) {
            if (user.isAnonymous) {//link data voi tai khoan moi neu la ng dung an danh
                val credential = EmailAuthProvider.getCredential(email, password)
                mAuth.currentUser!!.linkWithCredential(credential)
                    .addOnCompleteListener(activity,
                        OnCompleteListener<AuthResult> { task ->
                            isSuccess.value = true
                        })
            } else { //tao tai khoan thong thuong
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity,
                        OnCompleteListener<AuthResult?> { task ->
                            isSuccess.value = true
                        })
            }
        }
    }

    fun signInWithEmail(
        email: String,
        password: String,
        activity: Activity,
        isSuccess: MutableLiveData<Boolean>,
        isError: MutableLiveData<Boolean>
    ) {
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful){
                        isSuccess.value = true
                    }else{
                        isError.value = true
                    }
                })
    }

    fun signInAnonymously(activity: Activity) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            auth.signInAnonymously()
                .addOnCompleteListener(activity,
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            Log.e("signInAnonymously", "signInAnonymously:success")
                        } else {
                            Log.e("signInAnonymously", "signInAnonymously:failure", task.exception)
                        }
                    })
        }
    }

    fun signOut() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
    }

    fun getProfile(userName: MutableLiveData<String>, userEmail: MutableLiveData<String>) {
        val auth = FirebaseAuth.getInstance()
        userName.value = auth.currentUser?.displayName
        userEmail.value = auth.currentUser?.email
    }

    private fun sendPasswordResetEmail(emailAddress: String) {
        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("sendPasswordResetEmail", "Email sent.")
                } else {
                }
            }
    }

    fun updatePhoto(uri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(uri)
            .build()

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("update profile", "User profile updated.")
                } else {

                }
            }
    }

    fun updateFullname(
        fullname: String,
        userName: MutableLiveData<String>,
        isUpdatenameSuccess: MutableLiveData<Boolean>,
        noti: MutableLiveData<String>
    ) {
        val user = FirebaseAuth.getInstance().currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullname)
            .build()

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isUpdatenameSuccess.value = true
                    userName.value = fullname
                } else {
                    isUpdatenameSuccess.value = false
                    noti.value = "Update fail."
                }
            }
    }

    fun reAuthenticate(
        password: String,
        isSuccess: MutableLiveData<Boolean>
    ) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val credential = EmailAuthProvider
            .getCredential(user.email.toString(), password)

        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isSuccess.value = true
                    Log.e("reAuthenticate", "User re-authenticated.")
                } else {
                    isSuccess.value = false
                    Log.e("reAuthenticate", "Check password again")
                }
            }
    }

    fun reAuthenticateAndChangeEmail(
        email: String,
        password: String,
        userEmail: MutableLiveData<String>,
        isUpdateEmailSuccess: MutableLiveData<Boolean>,
        notification: MutableLiveData<String>
    ) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val credential = EmailAuthProvider
            .getCredential(user.email.toString(), password)
        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updateEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                isUpdateEmailSuccess.value = true
                                userEmail.value = email
                            } else {
                                isUpdateEmailSuccess.value = false
                                notification.value = "Update fail"
                            }
                        }
                } else {
                    notification.value = "Check password again"
                }
            }
    }

    fun reAuthenticateAndChangePassword(
        newPassword: String,
        oldPassword: String,
        noti: MutableLiveData<String>,
        isChangePassSuccess: MutableLiveData<Boolean>
    ) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val credential = EmailAuthProvider
            .getCredential(user.email.toString(), oldPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    changePassword(newPassword, noti,isChangePassSuccess)
                    Log.e("reAuthenticate", "User re-authenticated.")
                } else {
                    noti.value = "Incorect password! Please check your password again!"
                    Log.e("reAuthenticate", "Check password again")
                }
            }
    }

    private fun changePassword(newPassword: String, noti: MutableLiveData<String>,
                               isChangePassSuccess: MutableLiveData<Boolean>) {
        val user = FirebaseAuth.getInstance().currentUser
        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isChangePassSuccess.value = true
                    Log.e("updatePassword", "User password updated.")
                } else {
                    Log.e("updatePassword", "Update fail")
                    noti.value = "Password change failed!"
                }
            }
    }

    fun isLogin(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user != null
    }

    fun isLoginWithEmail(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser ?: return false
        return !user.isAnonymous
    }

//    fun writeFood() {
//        val food1 = Food(
//            null,
//            "2",
//            "B??nh x??o",
//            30000F,
//            "T???i Hu???, m??n ??n n??y th?????ng ???????c g???i l?? b??nh kho??i v?? th?????ng k??m v???i th???t n?????ng, n?????c ch???m l?? n?????c l??o g???m t????ng, gan, l???c. T???i mi???n Nam Vi???t Nam, b??nh c?? cho th??m tr???ng v?? ng?????i ta ??n b??nh x??o ch???m n?????c m???m chua ng???t. T???i mi???n B???c Vi???t Nam, nh??n b??nh x??o ngo??i c??c th??nh ph???n nh?? c??c n??i kh??c c??n th??m c??? ?????u th??i m???ng ho???c khoai m??n th??i s???i. C??c lo???i rau ??n k??m v???i b??nh x??o r???t ??a d???ng g???m rau di???p, c???i xanh, rau di???p c??, t??a t??, rau h??ng, l?? qu???, l?? c??m ngu???i non... ??? C???n Th?? c?? th??m l?? chi???t, ??? ?????ng Th??p th??m l?? b???ng l??ng, ??? V??nh Long c?? th??m l?? xo??i non, ??? B???c Li??u c?? th??m l?? c??ch. C???u k??? nh???t l?? ??? c??c v??ng mi???n Trung Vi???t Nam, ngo??i rau s???ng, c??n th??m qu??? v??? ch??t, kh??? chua.. B???i v???y, d??n s??nh ??n c??? th???y ng??? ng??? nh?? m??n n??y th???c s??? ???????c b???t ngu???n t??? Hu???.",
//            5F,
//            "https://cdn.tgdd.vn/Files/2020/05/20/1256908/troi-mua-thu-lam-banh-xeo-kieu-mien-bac-gion-ngon-it-dau-mo-202005201032484905.jpg"
//        )
//        val food2 = Food(
//            null,
//            "3",
//            "Coca cola",
//            30000F,
//            "N?????c ng???t c?? gas Coca Cola ???????c s???n xu???t tr??n d??y chuy???n, c??ng ngh??? hi???n ?????i c???a Nh???t B???n, ?????m b???o tuy???t ?????i v??? ch???t l?????ng. S???n ph???m ???????c ch??? bi???n t??? nh???ng nguy??n li???u kh??ng ch???a h??a ch???t ?????c h???i, ?????m b???o an to??n tuy???t ?????i khi s??? d???ng. S???n ph???m gi??p b???n nhanh ch??ng xua tan c??n kh??t, gi??p b?? n?????c trong c??c ho???t ?????ng h??ng ng??y. Ngo??i ra N?????c ng???t c?? gas Coca Cola c??n k??ch th??ch v??? gi??c, l??m t??ng h????ng v??? cho c??c m??n ??n, gi??p b???n ti??u h??a t???t khi b??? ?????y h??i, cho m??n ??n c??ng th??m ngon mi???ng. N?????c ng???t c?? gas Coca Cola ???????c ????ng lon ti???n d???ng, d??? b???o qu???n v?? d??ng ???????c l??u. B???n c??ng c?? th??? mang theo d??? d??ng trong nh???ng chuy???n ??i ch??i, picnic ????? c??ng chia s??? v???i m???i ng?????i hay d??nh l??m qu?? t???ng cho b???n b??, ng?????i th??n.",
//            5F,
//            "https://photo-cms-tinnhanhchungkhoan.zadn.vn/w860/Uploaded/2022/wpxlcdjwi/2021_04_22/untitled-5329.jpg")
//
//        val food3 = Food(
//            null,
//            "2",
//            "Tr?? chanh",
//            20000F,
//            "Tr?? chanh l?? m???t th???c u???ng gi???i kh??t ???????c k???t h???p c??n b???ng gi???a v??? thanh ch??t d???u c???a tr?? c??ng v??? chua c???a chanh t???o n??n th??? ????? u???ng ?????c ????o gi???i kh??t v??o m??a h??. Tr?? chanh mang ?????n cho ng?????i u???ng tinh th???n t???nh t??o, th?? gi??n v?? tho???i m??i.",
//            5F,
//            "https://cdn.huongnghiepaau.com/wp-content/uploads/2019/12/thuc-uong-tra-chanh.jpg"
//        )
//        val food4 = Food(
//            null,
//            "2",
//            "Ch?? lam",
//            10000F,
//            "M??n b??nh n??y l??c x??a kh??ng c?? t??n g???i c??? th???, ch??? bi???t l?? m???t m??n b??nh ???????c l??m t??? ???????ng k???t h???p c??ng v???i ?????u ph???ng. N?? c?? v??? ng???t thanh c???a ???????ng m???t m??a, th??m ch??t b??i b??i v?? b??o c???a ?????u ph???ng. T???t c??? t???o n??n m???t m??n b??nh ??n kh??ng qu?? ng??n m?? l???i r???t ngon n???a ch???.\n" +
//                    "\n" +
//                    "Ng?????i ?????i l??c n??y l???y t??n l?? ch?? Lam r???i t??? ???? c??i t??n n??y ???????c l??u truy???n t???i b??y gi???. Nghe t??n l?? ch?? nh??ng th???t ra l?? kh??ng ph???i nh?? v???y, v??? vua th???y l??? n??n ??n th???.\n" +
//                    "\n" +
//                    "V??? ng???t thanh c???a m???t m??a, n???ng ???m c???a g???ng ???? khi???n vua t???m t???c khen ngon v?? ban th?????ng. Th??? l?? t??? ???? ?????n nay ch?? lam tr??? th??nh m??n ??n ph??? bi???n trong nh??n gian.",
//            5F,
//            "https://cdn.tgdd.vn/2020/07/CookRecipe/Avatar/che-lam-thumbnail.jpg"
//        )
//        val food5 = Food(
//            null,
//            "2",
//            "S???a chua n???p c???m",
//            30000F,
//            "Trong m??n s???a chua n???p c???m th?? g???o n???p c???m ???????c n???u th??nh x??i sau ???? ???????c ??? men cho c?? ????? chua sau ???? ???????c tr???n c??ng s???a chua ????? ??n. ;.k.Trong qu?? tr??nh ??? men n???p c???m t???o ra c??c vi khu???n c?? l???i k???t h???p c??ng v???i c??c l???i khu???n c?? trong s???a chua khi ??n s??? t???o ra c??c vi khu???n trong ???????ng ru???t t???t cho h??? ti??u h??a.\n" +
//                    "\n" +
//                    "Nh???ng ng?????i m???c c??c b???nh d??? d??y hay c??c b???nh ???????ng ti??u h??a th?? ??n s???a chua n???p c???m r???t c?? l???i. Ngo??i ra khi m???c c??c b???nh vi??m lo??t d??? d??y hay ??au d??? d??y th?? n??n ??n c??c m??n ??n t??? n???p c???m s??? nhanh kh???i b???nh.",
//            5F,
//            "https://cdn.beptruong.edu.vn/wp-content/uploads/2019/03/sua-chua-nep-cam.jpg"
//        )
//        val food6 = Food(
//            null,
//            "2",
//            "",
//            30000F,
//            "",
//            5F,
//            ""
//        )
//        val food7 = Food(
//            null,
//            "2",
//            "",
//            30000F,
//            "",
//            5F,
//            ""
//        )
//        val food8 = Food(
//            null,
//            "2",
//            "",
//            30000F,
//            "",
//            5F,
//            ""
//        )
//        val food9 = Food(
//            null,
//            "2",
//            "",
//            30000F,
//            "",
//            5F,
//            ""
//        )
//        val food10 = Food(
//            null,
//            "2",
//            "",
//            30000F,
//            "",
//            5F,
//            ""
//        )
//
//        var listFood:MutableList<Food> = mutableListOf()
//        listFood.add(food1)
//        listFood.add(food2)
//        listFood.add(food3)
//        listFood.add(food4)
//        listFood.add(food5)
//        listFood.add(food6)
//        listFood.add(food7)
//        listFood.add(food8)
//        listFood.add(food9)
//        listFood.add(food10)
//
//        for(food in listFood){
//            var database = Firebase.database.reference
//            val key = database.child("foods").push().key
//            if (key == null) {
//                Log.e("writeFood", "Couldn't get push key for foods")
//                return
//            }
//            food.id = key
//            val postValues = food.toMap()
//            val childUpdates = hashMapOf<String, Any>(
//                "/foods/$key" to postValues
//            )
//            database.updateChildren(childUpdates)
//        }
//    }

    fun addFoodToCart(food: FoodCart) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.reference
        val postValues = food.toMap()
        val childUpdates = hashMapOf<String, Any>(
            "/user-cart/${currentUser?.uid}/${food.id}" to postValues,
        )
        database.updateChildren(childUpdates).addOnSuccessListener {  }
    }

    fun getFoodsInCart(
        vmFoodsListInCart: MutableLiveData<MutableList<FoodCart>>,
        isUpdateCart: MutableLiveData<Boolean>
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.getReference("user-cart/${currentUser?.uid}")
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val foodCart = snapshot.getValue(FoodCart::class.java)
                if (foodCart != null) {
                    Log.e("onChildAdded", "${snapshot.key} + ${foodCart}")
                    vmFoodsListInCart.value!!.add(foodCart)
                    isUpdateCart.value = true
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val foodCart = snapshot.getValue(FoodCart::class.java)
                if (foodCart != null) {
                    Log.e("onChildChanged", "${snapshot.key} + ${foodCart}")
                    for (i in 0 until vmFoodsListInCart.value!!.size) {
                        if (foodCart.id.equals(vmFoodsListInCart.value!![i].id)) {
                            vmFoodsListInCart.value!![i] = foodCart
                        }
                    }
                    for (item in vmFoodsListInCart.value!!) {
                        Log.e("check list", "${item}")
                    }
                    isUpdateCart.value = true
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val foodCart = snapshot.getValue(FoodCart::class.java)
                if (foodCart != null) {
                    Log.e("onChildRemoved", "${snapshot.key} + ${foodCart}")
                    vmFoodsListInCart.value!!.remove(foodCart)
                }
                isUpdateCart.value = true
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                val foodCart = snapshot.getValue(FoodCart::class.java)
                if (foodCart != null) {
//                    Log.e("onChildMoved", "${snapshot.key} + ${foodCart}")
//                    vmFoodsListInCart.value!!.add(foodCart)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun updateQuatityFoodsInCart(food: FoodCart) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.reference
        val postValues = food.toMap()
        val childUpdates = hashMapOf<String, Any>(
            "/user-cart/${currentUser?.uid}/${food.id}" to postValues
        )
        database.updateChildren(childUpdates)
    }

    fun removeFoodInCart(food: FoodCart) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.reference
        database.child("/user-cart/${currentUser?.uid}/${food.id}").removeValue()
    }

    fun addFoodToFavorite(food: Food, isFavorite: MutableLiveData<Boolean>) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.reference
        val postValues = food.toMap()
        val childUpdates = hashMapOf<String, Any>(
            "/user-favorite/${currentUser?.uid}/${food.id}" to postValues,
        )
        database.updateChildren(childUpdates)
        isFavorite.value = true
    }

    fun removeFoodFavorite(food: Food, isFavorite: MutableLiveData<Boolean>) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.reference
        database.child("/user-favorite/${currentUser?.uid}/${food.id}").removeValue { _, _ ->
            isFavorite.value = false
        }
    }

    fun getFavoriteList(
        vmFavoriteList: MutableLiveData<MutableList<Food>>,
        isUpdateList: MutableLiveData<Boolean>
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.getReference("/user-favorite/${currentUser?.uid}")
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val food = snapshot.getValue(Food::class.java)
                if (food != null) {
                    vmFavoriteList.value!!.add(food)
                }
                isUpdateList.value = true
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val food = snapshot.getValue(Food::class.java)
                if (food != null) {
                    changeInforOfFood(vmFavoriteList, food)
                }
                isUpdateList.value = true
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val food = snapshot.getValue(Food::class.java)
                if (food != null) {
                    vmFavoriteList.value!!.remove(food)
                }
                isUpdateList.value = true
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                val food = snapshot.getValue(Food::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getFoodsList(
        vmFoodsList: MutableLiveData<MutableList<Food>>,
        vmFoodsList0: MutableLiveData<MutableList<Food>>,
        vmFoodsList1: MutableLiveData<MutableList<Food>>,
        vmFoodsList2: MutableLiveData<MutableList<Food>>,
        vmFoodsList3: MutableLiveData<MutableList<Food>>,
        vmFoodsList4: MutableLiveData<MutableList<Food>>,
        isUpdateList: MutableLiveData<Boolean>
    ) {
        var database = Firebase.database.getReference("foods")
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val food = snapshot.getValue(Food::class.java)
                if (food != null) {
                    vmFoodsList.value!!.add(food)
                    when (food.type) {
                        "0" -> vmFoodsList0.value!!.add(food)
                        "1" -> vmFoodsList1.value!!.add(food)
                        "2" -> vmFoodsList2.value!!.add(food)
                        "3" -> vmFoodsList3.value!!.add(food)
                        "4" -> vmFoodsList4.value!!.add(food)
                    }
                }
                isUpdateList.value = true
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val food = snapshot.getValue(Food::class.java)
                if (food != null) {
                    changeInforOfFood(vmFoodsList, food)
                    when (food.type) {
                        "0" -> {
                            changeInforOfFood(vmFoodsList0, food)
                        }
                        "1" -> {
                            changeInforOfFood(vmFoodsList1, food)
                        }
                        "2" -> {
                            changeInforOfFood(vmFoodsList2, food)
                        }
                        "3" -> {
                            changeInforOfFood(vmFoodsList3, food)
                        }
                        "4" -> {
                            changeInforOfFood(vmFoodsList4, food)
                        }
                    }
                }
                isUpdateList.value = true
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val food = snapshot.getValue(Food::class.java)
                if (food != null) {
                    when (food.type) {
//                        "0" -> vmFoodsList0.value!!.add(food)
//                        "1" -> vmFoodsList1.value!!.add(food)
//                        "2" -> vmFoodsList2.value!!.add(food)
//                        "3" -> vmFoodsList3.value!!.add(food)
//                        "4" -> vmFoodsList4.value!!.add(food)
                    }
                }
                isUpdateList.value = true
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                val food = snapshot.getValue(Food::class.java)
                if (food != null) {
                    when (food.type) {
//                        "0" -> vmFoodsList0.value!!.add(food)
//                        "1" -> vmFoodsList1.value!!.add(food)
//                        "2" -> vmFoodsList2.value!!.add(food)
//                        "3" -> vmFoodsList3.value!!.add(food)
//                        "4" -> vmFoodsList4.value!!.add(food)
                    }
                }
                isUpdateList.value = true
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeInforOfFood(
        vmFoodsList0: MutableLiveData<MutableList<Food>>,
        food: Food
    ) {
        for (i in 0 until vmFoodsList0.value!!.size) {
            if (food.id.equals(vmFoodsList0.value!![i].id)) {
                vmFoodsList0.value!![i] = food
            }
        }
    }

    fun addAddress(phone: String, address: String, notiAddress: MutableLiveData<String>) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.reference
        val key = database.child("user-address").push().key
        if (key == null) {
            Log.e("addAddress", "Couldn't get push key for address")
            return
        }
        val myAddress = MyAddress(key, currentUser?.uid, phone, address)
        val postValues = myAddress.toMap()
        val childUpdates = hashMapOf<String, Any>(
            "/user-address/${currentUser?.uid}/$key" to postValues
        )
        database.updateChildren(childUpdates) { _, _ ->
            notiAddress.value = "Add address successful"
        }
    }

    fun updateAddress(address: MyAddress) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.reference
        val postValues = address.toMap()
        val childUpdates = hashMapOf<String, Any>(
            "/user-address/${currentUser?.uid}/${address.id}" to postValues
        )
        database.updateChildren(childUpdates)
    }

    fun getAddressList(
        vmAddressList: MutableLiveData<MutableList<MyAddress>>,
        isUpdateAddressList: MutableLiveData<Boolean>
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.getReference("/user-address/${currentUser?.uid}")
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val myAddress = snapshot.getValue(MyAddress::class.java)
                if (myAddress != null) {
                    vmAddressList.value!!.add(myAddress)
                    isUpdateAddressList.value = true
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val myAddress = snapshot.getValue(MyAddress::class.java)
                if (myAddress != null) {
                    changeAddress(vmAddressList, myAddress)
                    isUpdateAddressList.value = true
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val myAddress = snapshot.getValue(MyAddress::class.java)
                if (myAddress != null) {
                    vmAddressList.value!!.remove(myAddress)
                    isUpdateAddressList.value = true
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                val myAddress = snapshot.getValue(MyAddress::class.java)
                isUpdateAddressList.value = true
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeAddress(
        vmAddressList: MutableLiveData<MutableList<MyAddress>>,
        myAddress: MyAddress
    ) {
        for (i in 0 until vmAddressList.value!!.size) {
            if (myAddress.id.equals(vmAddressList.value!![i].id)) {
                vmAddressList.value!![i] = myAddress
            }
        }
    }

    fun removeAddress(myAddress: MyAddress, updateAddressList: MutableLiveData<Boolean>) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.reference
        database.child("/user-address/${currentUser?.uid}/${myAddress.id}").removeValue { _, _ ->
//            updateAddressList.value = false
        }
    }

    fun addCard(
        bankName: String,
        cardNumber: String,
        name: String
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.reference
        val key = database.child("user-card").push().key
        if (key == null) {
            Log.e("addCard", "Couldn't get push key for card")
            return
        }
        val myCard = Card(key, currentUser?.uid, bankName, name, cardNumber)
        val postValues = myCard.toMap()
        val childUpdates = hashMapOf<String, Any>(
            "/user-card/${currentUser?.uid}/$key" to postValues
        )
        database.updateChildren(childUpdates) { _, _ ->
//            isUpdateCardList.value = true
            Log.e("addCard", "Add card successful")
        }
    }

    fun getCardList(
        vmCardList: MutableLiveData<MutableList<Card>>,
        updateCardList: MutableLiveData<Boolean>
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.getReference("/user-card/${currentUser?.uid}")
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val card = snapshot.getValue(Card::class.java)
                if (card != null) {
                    vmCardList.value!!.add(card)
                    updateCardList.value = true
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val card = snapshot.getValue(Card::class.java)
                if (card != null) {
                    changeCard(vmCardList, card)
                    updateCardList.value = true
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val card = snapshot.getValue(Card::class.java)
                if (card != null) {
                    vmCardList.value!!.remove(card)
                    updateCardList.value = true
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                val card = snapshot.getValue(Card::class.java)
                updateCardList.value = true
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeCard(
        vmCardList: MutableLiveData<MutableList<Card>>,
        myCard: Card
    ) {
        for (i in 0 until vmCardList.value!!.size) {
            if (myCard.id.equals(vmCardList.value!![i].id)) {
                vmCardList.value!![i] = myCard
            }
        }
    }

    fun removeCard(myCard: Card, updateCardList: MutableLiveData<Boolean>) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.reference
        database.child("/user-card/${currentUser?.uid}/${myCard.id}").removeValue { _, _ ->
            updateCardList.value = false
        }
    }

    fun addReceipt(
        receipt: MutableLiveData<Receipt>,
        time: String,
        payment: String?,
        address: String?,
        phone: String?,
        message: String,
        total: String,
        foodOrderList: MutableList<FoodOrder>,
        isAddSuccess: MutableLiveData<Boolean>
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.reference
        val key = database.child("receipt/${currentUser?.uid}").push().key
        if (key == null) {
            Log.e("addCard", "Couldn't get push key for receipt")
            isAddSuccess.value = false
            return
        }
        val myOrder = Receipt(key, currentUser?.uid, time, message, payment, phone, address, total)
        receipt.value = myOrder
        val orderValues = myOrder.toMap()
        val childOrderUpdates =
            hashMapOf<String, Any>("/receipt/${currentUser?.uid}/$key" to orderValues)
        database.updateChildren(childOrderUpdates) { _, _ ->
            Log.e("addReceipt", "Add receipt successful")
            addFoodOrder(foodOrderList, key, database, isAddSuccess)
        }
    }

    private fun addFoodOrder(
        foodOrderList: MutableList<FoodOrder>,
        key: String?,
        database: DatabaseReference,
        isAddSuccess: MutableLiveData<Boolean>
    ) {
        for (itemFood in foodOrderList) {
            val foodOrder = FoodOrder(
                itemFood.id,
                itemFood.type,
                itemFood.name,
                itemFood.price,
                itemFood.describe,
                itemFood.rate,
                itemFood.image_uri,
                itemFood.quantity,
                false
            )
            val foodOrderValues = foodOrder.toMap()
            val childFoodOrderUpdates =
                hashMapOf<String, Any>("food-order/$key/${itemFood.id}" to foodOrderValues)
            database.updateChildren(childFoodOrderUpdates) { _, _ ->
                removeCart()
                isAddSuccess.value = true
                Log.e("addFoodOrder", "Add foods receipt successful")
            }
        }
    }
     fun removeCart(){
         val auth = FirebaseAuth.getInstance()
         val currentUser = auth.currentUser
         var database = Firebase.database.reference
         database.child("/user-cart/${currentUser?.uid}")
     }

    fun getReceiptList(
        vmReceiptList: MutableLiveData<MutableList<Receipt>>,
        updateReceipts: MutableLiveData<Boolean>
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.getReference("/receipt/${currentUser?.uid}")
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val receipt = snapshot.getValue(Receipt::class.java)
                if (receipt != null) {
                    vmReceiptList.value!!.add(receipt)
                    updateReceipts.value = true
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val receipt = snapshot.getValue(Receipt::class.java)
                if (receipt != null) {
                    changeReceipt(vmReceiptList, receipt)
                    updateReceipts.value = true
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val receipt = snapshot.getValue(Receipt::class.java)
                if (receipt != null) {
                    vmReceiptList.value!!.remove(receipt)
                    updateReceipts.value = true
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                val receipt = snapshot.getValue(Receipt::class.java)
                updateReceipts.value = true
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeReceipt(
        vmReceiptList: MutableLiveData<MutableList<Receipt>>,
        receipt: Receipt
    ) {
        for (i in 0 until vmReceiptList.value!!.size) {
            if (receipt.id.equals(vmReceiptList.value!![i].id)) {
                vmReceiptList.value!![i] = receipt
            }
        }
    }

    fun getFoodsOrderList(
        vmFoodOrderList: MutableLiveData<MutableList<FoodOrder>>,
        vmReceiptSelect: MutableLiveData<Receipt>,
        updateFoodOrder: MutableLiveData<Boolean>
    ) {
        var database = Firebase.database.getReference("/food-order/${vmReceiptSelect.value?.id}")
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val foodOrder = snapshot.getValue(FoodOrder::class.java)
                if (foodOrder != null) {
                    Log.e("onChildAdded", "item food order add $foodOrder")
                    vmFoodOrderList.value!!.add(foodOrder)
                    updateFoodOrder.value = true
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val foodOrder = snapshot.getValue(FoodOrder::class.java)
                if (foodOrder != null) {
                    changeFoodOrder(vmFoodOrderList, foodOrder)
                    updateFoodOrder.value = true
                    Log.e("onChildChanged", "${vmReceiptSelect.value?.id} i tem food order change $foodOrder")
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val foodOrder = snapshot.getValue(FoodOrder::class.java)
                if (foodOrder != null) {
                    vmFoodOrderList.value!!.remove(foodOrder)
                    updateFoodOrder.value = true
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                val foodOrder = snapshot.getValue(FoodOrder::class.java)
                updateFoodOrder.value = true
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeFoodOrder(
        vmFoodOrderList: MutableLiveData<MutableList<FoodOrder>>,
        foodOrder: FoodOrder
    ) {
        for (i in 0 until vmFoodOrderList.value!!.size) {
            if (foodOrder.id.equals(vmFoodOrderList.value!![i].id)) {
                vmFoodOrderList.value!![i] = foodOrder
            }
        }
    }

    fun addComment(
        rate: Float,
        content: String,
        currentTime: String,
        foodOrder: FoodOrder,
        isAddFoodOrderSuccess: MutableLiveData<Boolean>
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.reference
        val key = database.child("food-comment/${foodOrder.id}").push().key
        if (key == null) {
            Log.e("add comment", "Couldn't get push key for comment")
            isAddFoodOrderSuccess.value = false
            return
        }
        val comment = Comment(
            key,
            currentUser?.uid,
            foodOrder.id,
            rate,
            content,
            currentTime,
            currentUser?.photoUrl.toString(),
            currentUser?.displayName,
            foodOrder.name,
            foodOrder.image_uri
        )
        if (currentUser?.displayName.equals("")){
            comment.user_name = "Anonymously"
        }
        val commentValues = comment.toMap()
        val commentUpdates =
            hashMapOf<String, Any>("food-comment/${foodOrder.id}/$key" to commentValues)
        database.updateChildren(commentUpdates) { _, _ ->
            Log.e("addComment", "Add comment successful")
            addCommentUser(isAddFoodOrderSuccess, currentUser, key, commentValues, database)
        }
    }

    private fun addCommentUser(
        isAddFoodOrderSuccess: MutableLiveData<Boolean>,
        currentUser: FirebaseUser?,
        key: String?,
        commentValues: Map<String, Any?>,
        database: DatabaseReference
    ) {
        val commentUpdate =
            hashMapOf<String, Any>("user-comment/${currentUser?.uid}/$key" to commentValues)
        database.updateChildren(commentUpdate) { _, _ ->
            Log.e("addCommentUser", "Add comment with user successful")
            isAddFoodOrderSuccess.value = true
        }
    }

    fun updateFoodOrderRate(
        vmFoodOrderSelect: MutableLiveData<FoodOrder>,
        vmReceiptSelect: MutableLiveData<Receipt>,
        updateFoodOrder: MutableLiveData<Boolean>
    ) {
        var database = Firebase.database.reference
        val foodOrder = vmFoodOrderSelect.value!!
        val foodOrderValues = foodOrder.toMap()
        val foodOrderUpdates =
            hashMapOf<String, Any>("food-order/${vmReceiptSelect.value?.id}/${foodOrder.id}" to foodOrderValues)
        database.updateChildren(foodOrderUpdates) { _, _ ->
            updateFoodOrder.value = true
        }
    }

    fun getCommentListOfFood(
        foodid: String,
        vmCommentList: MutableLiveData<MutableList<Comment>>,
        isUpdateComment: MutableLiveData<Boolean>
    ) {
        var database = Firebase.database.getReference("/food-comment/$foodid")
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val comment = snapshot.getValue(Comment::class.java)
                if (comment != null) {
                    vmCommentList.value!!.add(comment)
                    isUpdateComment.value = true
                    Log.e("getComments","$comment")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val comment = snapshot.getValue(Comment::class.java)
                if (comment != null) {
                    changeComment(vmCommentList, comment)
                    isUpdateComment.value = true
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val comment = snapshot.getValue(Comment::class.java)
                if (comment != null) {
                    vmCommentList.value!!.remove(comment)
                    isUpdateComment.value = true
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                val comment = snapshot.getValue(Comment::class.java)
                isUpdateComment.value = true
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getCommentListOfUser(
        vmCommentList: MutableLiveData<MutableList<Comment>>,
        isUpdateComment: MutableLiveData<Boolean>
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = Firebase.database.getReference("/user-comment/${currentUser?.uid}")
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val comment = snapshot.getValue(Comment::class.java)
                if (comment != null) {
                    vmCommentList.value!!.add(comment)
                    isUpdateComment.value = true
                    Log.e("getComments","$comment")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val comment = snapshot.getValue(Comment::class.java)
                if (comment != null) {
                    changeComment(vmCommentList, comment)
                    isUpdateComment.value = true
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val comment = snapshot.getValue(Comment::class.java)
                if (comment != null) {
                    vmCommentList.value!!.remove(comment)
                    isUpdateComment.value = true
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                val comment = snapshot.getValue(Comment::class.java)
                isUpdateComment.value = true
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeComment(
        vmCommentList: MutableLiveData<MutableList<Comment>>,
        comment: Comment
    ) {
        for (i in 0 until vmCommentList.value!!.size) {
            if (comment.id.equals(vmCommentList.value!![i].id)) {
                vmCommentList.value!![i] = comment
            }
        }
    }
}