package com.example.foodapp.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.Adapter.CartAdapter
import com.example.foodapp.Model.CartItems
import com.example.foodapp.PayOutActivity
import com.example.foodapp.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodImagesUri: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var quantity: MutableList<Int>

    private lateinit var userId: String
    private lateinit var cartAdapter: CartAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCartBinding.inflate(inflater, container, false)


        auth = FirebaseAuth.getInstance()
        retrieveCartItems()



        //click button process
        binding.processButton.setOnClickListener {
            //get order items detials before processding to check out
            //khi ta nhan proceesButton se sang Thanh toan va hien thi name dua tren tai khoan dang nhap
            getOrderItemsDetails()
        }


        return binding.root
    }

    private fun getOrderItemsDetails() {
        val orderIdReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")
        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodImage = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodIngredient = mutableListOf<String>()

        //get items Quantities
        val foodQuantities = cartAdapter.getUpdatedItemsQuantities()
        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    //get the cartItems to respective List
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)
                    //add items details in to list
                    orderItems?.foodname?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodDescription?.let { foodDescription.add(it) }
                    orderItems?.foodImage?.let { foodImage.add(it) }
                    orderItems?.foodIngredient?.let { foodIngredient.add(it) }
                }
                orderNow(foodName, foodPrice, foodDescription, foodImage, foodIngredient,foodQuantities)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Order Making failed ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescription: MutableList<String>,
        foodImage: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQuantities:MutableList<Int>
    ) {
        if (isAdded && context !=null){
            val intent =Intent(requireContext(), PayOutActivity::class.java).apply {
                putExtra("FoodItemName",foodName as ArrayList<String>)
                putExtra("FoodItemPrice",foodPrice as ArrayList<String>)
                putExtra("FoodItemDescription",foodDescription as ArrayList<String>)
                putExtra("FoodItemImage",foodImage as ArrayList<String>)
                putExtra("FoodItemIngredient",foodIngredient as ArrayList<String>)
                putExtra("FoodItemQuantity",foodQuantities as ArrayList<Int>)
            }
            startActivity(intent)
        }
    }

    private fun retrieveCartItems() {
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid?:""
        val foodReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")

        //list to store cart items
        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDescriptions = mutableListOf()
        foodImagesUri = mutableListOf()
        foodIngredients = mutableListOf()
        quantity = mutableListOf()

        //fetch data from the database
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {

                    //get the cartItems object from the child node
                    val cartItem = foodSnapshot.getValue(CartItems::class.java)

                    //add cart items detail to the list
                    cartItem?.foodname?.let { foodNames.add(it) }
                    cartItem?.foodPrice?.let { foodPrices.add(it) }
                    cartItem?.foodDescription?.let { foodDescriptions.add(it) }
                    cartItem?.foodImage?.let { foodImagesUri.add(it) }
                    cartItem?.foodQuantity?.let { quantity.add(it) }
                    cartItem?.foodIngredient?.let { foodIngredients.add(it) }
                }
                setAdapter()
            }
            private fun setAdapter() {
                cartAdapter = CartAdapter(requireContext(),foodNames,foodPrices,foodDescriptions,foodImagesUri,quantity,foodIngredients)
                binding.cartRecyclerview.layoutManager =LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
                binding.cartRecyclerview.adapter =cartAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "data not fetch", Toast.LENGTH_SHORT).show()
            }

        })

    }


}