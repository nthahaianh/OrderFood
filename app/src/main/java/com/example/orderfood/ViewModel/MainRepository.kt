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
//            "Bánh xèo",
//            30000F,
//            "Tại Huế, món ăn này thường được gọi là bánh khoái và thường kèm với thịt nướng, nước chấm là nước lèo gồm tương, gan, lạc. Tại miền Nam Việt Nam, bánh có cho thêm trứng và người ta ăn bánh xèo chấm nước mắm chua ngọt. Tại miền Bắc Việt Nam, nhân bánh xèo ngoài các thành phần như các nơi khác còn thêm củ đậu thái mỏng hoặc khoai môn thái sợi. Các loại rau ăn kèm với bánh xèo rất đa dạng gồm rau diếp, cải xanh, rau diếp cá, tía tô, rau húng, lá quế, lá cơm nguội non... Ở Cần Thơ có thêm lá chiết, ở Đồng Tháp thêm lá bằng lăng, ở Vĩnh Long có thêm lá xoài non, ở Bạc Liêu có thêm lá cách. Cầu kỳ nhất là ở các vùng miền Trung Việt Nam, ngoài rau sống, còn thêm quả vả chát, khế chua.. Bởi vậy, dân sành ăn cứ thấy ngờ ngợ như món này thực sự được bắt nguồn từ Huế.",
//            5F,
//            "https://cdn.tgdd.vn/Files/2020/05/20/1256908/troi-mua-thu-lam-banh-xeo-kieu-mien-bac-gion-ngon-it-dau-mo-202005201032484905.jpg"
//        )
//        val food2 = Food(
//            null,
//            "3",
//            "Coca cola",
//            30000F,
//            "Nước ngọt có gas Coca Cola được sản xuất trên dây chuyền, công nghệ hiện đại của Nhật Bản, đảm bảo tuyệt đối về chất lượng. Sản phẩm được chế biến từ những nguyên liệu không chứa hóa chất độc hại, đảm bảo an toàn tuyệt đối khi sử dụng. Sản phẩm giúp bạn nhanh chóng xua tan cơn khát, giúp bù nước trong các hoạt động hàng ngày. Ngoài ra Nước ngọt có gas Coca Cola còn kích thích vị giác, làm tăng hương vị cho các món ăn, giúp bạn tiêu hóa tốt khi bị đầy hơi, cho món ăn càng thêm ngon miệng. Nước ngọt có gas Coca Cola được đóng lon tiện dụng, dễ bảo quản và dùng được lâu. Bạn cũng có thể mang theo dễ dàng trong những chuyến đi chơi, picnic để cùng chia sẻ với mọi người hay dành làm quà tặng cho bạn bè, người thân.",
//            5F,
//            "https://photo-cms-tinnhanhchungkhoan.zadn.vn/w860/Uploaded/2022/wpxlcdjwi/2021_04_22/untitled-5329.jpg")
//
//        val food3 = Food(
//            null,
//            "2",
//            "Trà chanh",
//            20000F,
//            "Trà chanh là một thức uống giải khát được kết hợp cân bằng giữa vị thanh chát dịu của trà cùng vị chua của chanh tạo nên thứ đồ uống độc đáo giải khát vào mùa hè. Trà chanh mang đến cho người uống tinh thần tỉnh táo, thư giãn và thoải mái.",
//            5F,
//            "https://cdn.huongnghiepaau.com/wp-content/uploads/2019/12/thuc-uong-tra-chanh.jpg"
//        )
//        val food4 = Food(
//            null,
//            "2",
//            "Chè lam",
//            10000F,
//            "Món bánh này lúc xưa không có tên gọi cụ thể, chỉ biết là một món bánh được làm từ đường kết hợp cùng với đậu phộng. Nó có vị ngọt thanh của đường mật mía, thêm chút bùi bùi và béo của đậu phộng. Tất cả tạo nên một món bánh ăn không quá ngán mà lại rất ngon nữa chứ.\n" +
//                    "\n" +
//                    "Người đời lúc này lấy tên là chè Lam rồi từ đó cái tên này được lưu truyền tới bây giờ. Nghe tên là chè nhưng thật ra là không phải như vậy, vị vua thấy lạ nên ăn thử.\n" +
//                    "\n" +
//                    "Vị ngọt thanh của mật mía, nồng ấm của gừng đã khiến vua tấm tắc khen ngon và ban thưởng. Thế là từ đó đến nay chè lam trở thành món ăn phổ biến trong nhân gian.",
//            5F,
//            "https://cdn.tgdd.vn/2020/07/CookRecipe/Avatar/che-lam-thumbnail.jpg"
//        )
//        val food5 = Food(
//            null,
//            "2",
//            "Sữa chua nếp cẩm",
//            30000F,
//            "Trong món sữa chua nếp cẩm thì gạo nếp cẩm được nấu thành xôi sau đó được ủ men cho có độ chua sau đó được trộn cùng sữa chua để ăn. ;.k.Trong quá trình ử men nếp cẩm tạo ra các vi khuẩn có lợi kết hợp cùng với các lọi khuẩn có trong sữa chua khi ăn sẽ tạo ra các vi khuẩn trong đường ruột tốt cho hệ tiêu hóa.\n" +
//                    "\n" +
//                    "Những người mắc các bệnh dạ dày hay các bệnh đường tiêu hóa thì ăn sữa chua nếp cẩm rất có lợi. Ngoài ra khi mắc các bệnh viêm loét dạ dày hay đau dạ dày thì nên ăn các món ăn từ nếp cẩm sẽ nhanh khỏi bệnh.",
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