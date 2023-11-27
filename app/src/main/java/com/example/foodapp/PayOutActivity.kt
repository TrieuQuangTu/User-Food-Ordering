package com.example.foodapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodapp.BottomSheet.CongratsBottomSheet
import com.example.foodapp.Model.OrderDetails
import com.example.foodapp.databinding.ActivityPayOutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var foodItemName: ArrayList<String>
    private lateinit var foodItemPrice: ArrayList<String>
    private lateinit var foodItemImage: ArrayList<String>
    private lateinit var foodItemDescription: ArrayList<String>
    private lateinit var foodItemIngredient: ArrayList<String>
    private lateinit var foodItemQuantities: ArrayList<Int>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize Firebase and User Detail
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        // set user data
        SetUserData()

        //get user details from Firebase
        foodItemName = intent.getStringArrayListExtra("FoodItemName") as ArrayList<String>
        foodItemPrice = intent.getStringArrayListExtra("FoodItemPrice") as ArrayList<String>
        foodItemDescription = intent.getStringArrayListExtra("FoodItemDescription") as ArrayList<String>
        foodItemImage = intent.getStringArrayListExtra("FoodItemImage") as ArrayList<String>
        foodItemIngredient = intent.getStringArrayListExtra("FoodItemIngredient") as ArrayList<String>
        foodItemQuantities = intent.getIntegerArrayListExtra("FoodItemQuantity") as ArrayList<Int>

        totalAmount = calculateTotalAmount().toString() + ".000 VND"
        //binding.payoutTotal.isEnabled =false
        binding.payoutTotal.setText(totalAmount)

        binding.ButtonBackPayout.setOnClickListener {
            finish()
        }
        binding.PlaceMyOrder.setOnClickListener {

            //get data from textview
            name = binding.payoutName.text.toString().trim()
            address = binding.payoutAddress.text.toString().trim()
            phone = binding.payoutPhone.text.toString().trim()

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill information", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }


        }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid ?: ""
        val time = System.currentTimeMillis()
        val itemPushkey = databaseReference.child("OrderDetails").push().key
        val orderDetails = OrderDetails(
            userId,
            name,
            foodItemName,
            foodItemPrice,
            foodItemImage,
            foodItemQuantities,
            address,
            totalAmount,
            phone,
            time,
            itemPushkey,
            false,
            false
        )
        val orderReference =databaseReference.child("OrderDetails").child(itemPushkey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "Test")
            //khi order xong se phai remove item o add to cart
            removeItemFromCart()

            //sau khi nhan button placeOrder ,cac phan order se chay vao trong historyFragment
            addOrderToHistory(orderDetails)
        }.addOnFailureListener {
            Toast.makeText(this,"failed to order",Toast.LENGTH_SHORT).show()
        }

    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("user").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushkey!!).setValue(orderDetails)
            .addOnSuccessListener {

            }

    }

    private fun removeItemFromCart(){
        val cartItemsReference =databaseReference.child("user").child(userId).child("CartItems")
        cartItemsReference.removeValue()
    }

    //tinh tong tien dua tren gia va so luong
    private fun calculateTotalAmount(): Int {
        var totalAmount =
            0 //Khởi tạo một biến totalAmountđể lưu trữ tổng số tiền tích lũy và đặt nó thành 0.
        //Lặp lại các chỉ mục của foodItemPricemảng bằng vòng lặp for.
        // Biến vòng lặp i nằm trong khoảng từ 0 đến nhỏ hơn kích thước của foodItemPrice.
        for (i in 0 until foodItemPrice.size) {
            var price =
                foodItemPrice[i] //Lấy price mục thực phẩm tại chỉ mục i từ foodItemPricemảng.
            var lastChar =
                price.last() //Trích xuất ký tự cuối cùng của price chuỗi, giả sử nó đại diện cho ký hiệu tiền tệ ('$').

            // Xác định giá trị dựa trên sự hiện diện của '$' ở cuối

            val priceIntValue = if (lastChar == 'k') {
                price.dropLast(1).toInt()
            } else {
                price.toInt()
            }
            var quantity = foodItemQuantities[i]
            totalAmount += priceIntValue * quantity

        }
        return totalAmount
    }

    private fun SetUserData() {
        var user = auth.currentUser
        if (user != null) {
            var userId = user.uid
            val userReference = databaseReference.child("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").getValue(String::class.java) ?: ""
                        val address = snapshot.child("address").getValue(String::class.java) ?: ""
                        val phone = snapshot.child("phone").getValue(String::class.java) ?: ""

                        binding.apply {
                            payoutName.setText(name)
                            payoutAddress.setText(address)
                            payoutPhone.setText(phone)
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}