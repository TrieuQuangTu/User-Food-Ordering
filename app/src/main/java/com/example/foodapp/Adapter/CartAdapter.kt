package com.example.foodapp.Adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.foodapp.databinding.CartCustomItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    private var context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemsPrice: MutableList<String>,
    private var cartDescription:MutableList<String>,
    private val cartImages: MutableList<String>,
    private var cartQuantity:MutableList<Int>,
    private var cartIngredient:MutableList<String>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var auth =FirebaseAuth.getInstance()

    init {
        val database =FirebaseDatabase.getInstance()
        val userId =auth.currentUser?.uid?:""
        val cartItemNumber =cartItems.size

        itemQuantities =IntArray(cartItemNumber){1}
        cartItemsReference =database.reference.child("user").child(userId).child("CartItems")

    }
    companion object{
        private var itemQuantities:IntArray = intArrayOf()
        private lateinit var cartItemsReference:DatabaseReference
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding =
            CartCustomItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = cartItems.size


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class CartViewHolder(private val binding: CartCustomItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                txtFoodnameCart.text = cartItems[position]
                txtPriceCart.text = cartItemsPrice[position]
                //load image using Glide
                val uriString =cartImages[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(imgCart)
                txtQuantityCart.text = quantity.toString()

                minusButton.setOnClickListener {
                    deceaseQuantity(position)
                }
                plusButton.setOnClickListener {
                    increaseQuantity(position)
                }
                deleteButton.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }
                }
            }
        }

        //ham giam so luong
        fun deceaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                cartQuantity[position] = itemQuantities[position]
                binding.txtQuantityCart.text = itemQuantities[position].toString()
            }
        }

        //ham tang so luong
        fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                cartQuantity[position] = itemQuantities[position]
                binding.txtQuantityCart.text = itemQuantities[position].toString()
            }
        }

        fun deleteItem(position: Int) {
            val positionRetrieve =position
            getUnikeyAtPosition(positionRetrieve){uniquekey->
                if (uniquekey !=null){
                    removeItem(position,uniquekey)
                }
            }
        }

    }

    private fun removeItem(position: Int, uniquekey: String) {
        if (uniquekey !=null){
            cartItemsReference.child(uniquekey).removeValue().addOnCompleteListener {
                cartItems.removeAt(position)
                cartImages.removeAt(position)
                cartDescription.removeAt(position)
                cartItemsPrice.removeAt(position)
                cartQuantity.removeAt(position)
                cartIngredient.removeAt(position)
                Toast.makeText(context,"Item Delete", Toast.LENGTH_SHORT).show()


                //update quantities
                itemQuantities = itemQuantities.filterIndexed { index, i -> index !=position }.toIntArray()
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,cartItems.size)
            }.addOnFailureListener {
                Toast.makeText(context,"Failed to Delete", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getUnikeyAtPosition(positionRetrieve: Int, onComplete:(String?) -> Unit) {
        cartItemsReference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniquekey:String?=null
                snapshot.children.forEachIndexed{index,dataSnapshot ->
                    if (index == positionRetrieve){
                        uniquekey =dataSnapshot.key
                        return@forEachIndexed
                    }
                }
                onComplete(uniquekey)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getUpdatedItemsQuantities(): MutableList<Int> {

        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity

    }
}