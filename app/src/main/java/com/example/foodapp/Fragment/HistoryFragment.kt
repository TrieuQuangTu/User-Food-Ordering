package com.example.foodapp.Fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodapp.Adapter.BuyAgainAdapter
import com.example.foodapp.Model.OrderDetails
import com.example.foodapp.databinding.FragmentHistoryBinding
import com.example.foodapp.RecentOrderItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HistoryFragment : Fragment() {


    private lateinit var binding:FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    
    private lateinit var database:FirebaseDatabase
    private lateinit var auth:FirebaseAuth

    private lateinit var userId:String
    private  var listOfOrderItem:ArrayList<OrderDetails> = arrayListOf()

    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHistoryBinding.inflate(layoutInflater,container,false)

        //initialize FirebaseAuth
        auth= FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()

        //Retrieve and display The User Order History
        retrieveBuyHistory()

        //click vao item trong recent buy de sang 1 activity moi hien thi toan bo recent buy
        binding.recentBuyItem.setOnClickListener {
            seeItemsRecentBuy()
        }

        //
        binding.receivedButton.setOnClickListener {
            updateOrderStatus()
        }
        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey =listOfOrderItem[0].itemPushkey
        val completeOrderRef =database.reference.child("CompleteOrder").child(itemPushKey!!)
        completeOrderRef.child("paymentReceived").setValue(true)

    }

    //function to see items Recent buy
    private fun seeItemsRecentBuy() {
       listOfOrderItem.firstOrNull()?.let { recentbuy->
           val intent =Intent(requireContext(),RecentOrderItems::class.java)
           intent.putExtra("RecentBuyOrderItem",listOfOrderItem)
           startActivity(intent)
       }
    }

    //function to retrieve items buy history
    private fun retrieveBuyHistory() {
        binding.recentBuyItem.visibility =View.VISIBLE
        userId =auth.currentUser?.uid?:""

        val buyItemReference:DatabaseReference =database.reference.child("user").child(userId).child("BuyHistory")
        val shortingQuery  = buyItemReference.orderByChild("currentTime") //sap xep theo currentTime

        shortingQuery.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(buySnapShot in snapshot.children){
                    val buyHistoryItem =buySnapShot.getValue(OrderDetails::class.java)
                    listOfOrderItem.add(buyHistoryItem!!)
                }
                listOfOrderItem.reverse() //đảo ngược thứ tự của danh sách listOfOrderItem.

                if (listOfOrderItem.isNotEmpty()){
                    //display the most recent order details
                    setDataInRecentBuyItem()
                    //setup to recyclerview with previous order details
                    setPreviousBuyItemsRecyclerView()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    //function to display the most recent order details
    private fun setDataInRecentBuyItem() {
        binding.recentBuyItem.visibility =View.VISIBLE
        //firstOrNull(): Phương thức này sẽ trả về phần tử đầu tiên trong danh sách listOfOrderItem
        // nếu danh sách không rỗng.
        // Nếu danh sách rỗng, nó sẽ trả về null
        val recentOrderItem =listOfOrderItem.firstOrNull()
        recentOrderItem?.let {
            with(binding){
                buyAgainFoodName.text =it.foodNames?.firstOrNull()?:""
                buyAgainPrice.text=it.foodPrices?.firstOrNull()?:""
                val image =it.foodImages?.firstOrNull()?:""
                val uri = Uri.parse(image)
                Glide.with(requireContext()).load(uri).into(buyAgainImage)

                //OrderAccepted
                val isOrderIsAccepted =listOfOrderItem[0].orderAccepted
                if (isOrderIsAccepted){
                    orderStatus.background.setTint(Color.GREEN)
                    receivedButton.visibility =View.VISIBLE
                }

                listOfOrderItem.reverse()
                if (listOfOrderItem.isNotEmpty()){

                }

            }
        }
    }

    private fun setPreviousBuyItemsRecyclerView() {
        val buyAgainFoodname = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()

        //lap phan tu tu 1-> nho hon danh sach
        for (i in 1 until listOfOrderItem.size){
            listOfOrderItem[i].foodNames?.firstOrNull()?.let { buyAgainFoodname.add(it) }
            listOfOrderItem[i].foodPrices?.firstOrNull()?.let { buyAgainFoodPrice.add(it) }
            listOfOrderItem[i].foodImages?.firstOrNull()?.let { buyAgainFoodImage.add(it) }
        }
        val rv =binding.BuyAgainRecyclerView
        rv.layoutManager =LinearLayoutManager(requireContext())
        buyAgainAdapter =BuyAgainAdapter(buyAgainFoodname,buyAgainFoodPrice,buyAgainFoodImage,requireContext())
        rv.adapter =buyAgainAdapter
    }




}