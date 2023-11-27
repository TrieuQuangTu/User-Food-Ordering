package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.Adapter.RecentBuyAdapter
import com.example.foodapp.Model.OrderDetails
import com.example.foodapp.databinding.ActivityRecentOrderItemsBinding

class RecentOrderItems : AppCompatActivity() {
    private lateinit var binding:ActivityRecentOrderItemsBinding


    private lateinit var allFoodName:ArrayList<String>
    private lateinit var allFoodImage:ArrayList<String>
    private lateinit var allFoodPrice:ArrayList<String>
    private lateinit var allFoodQuantity:ArrayList<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentOrderItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //click ButtonBack
        binding.imageButton.setOnClickListener {
            finish()
        }

        val recentOrderItems =
            intent.getSerializableExtra("RecentBuyOrderItem") as ArrayList<OrderDetails>

        recentOrderItems?.let {
            orderDetails ->
            if (orderDetails.isNotEmpty()){
                val recentOrderItem =orderDetails[0]

                allFoodName =recentOrderItem.foodNames as ArrayList<String>
                allFoodImage =recentOrderItem.foodImages as ArrayList<String>
                allFoodPrice =recentOrderItem.foodPrices as ArrayList<String>
                allFoodQuantity =recentOrderItem.foodQuantities as ArrayList<Int>
            }

        }
        setAdapter()
    }

    private fun setAdapter() {
        val rv=binding.BuyAgainRecyclerview
        rv.layoutManager =LinearLayoutManager(this)
        val adapter =RecentBuyAdapter(this,allFoodName,allFoodImage,allFoodPrice,allFoodQuantity)
        rv.adapter =adapter
    }
}