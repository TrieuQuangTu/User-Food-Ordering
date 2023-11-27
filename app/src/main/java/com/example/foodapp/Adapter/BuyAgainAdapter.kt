package com.example.foodapp.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.BuyAgainItemBinding

class BuyAgainAdapter(
    private val buyAgainFoodName: MutableList<String>,
    private val buyAgainFoodPrice: MutableList<String>,
    private val buyAgainFoodImage: MutableList<String>,
    private val requiredContext: Context
) :RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BuyAgainViewHolder(binding)
    }

    override fun getItemCount():Int = buyAgainFoodImage.size

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        holder.bind(buyAgainFoodName[position], buyAgainFoodPrice[position], buyAgainFoodImage[position])
    }

    inner class BuyAgainViewHolder(private val binding: BuyAgainItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(foodName: String, foodPrice: String, foodImage: String) {

            binding.buyAgainFoodName.text =foodName
            binding.buyAgainPrice.text = foodPrice
           val uriString =foodImage
            val uri =Uri.parse(uriString)
            Glide.with(requiredContext).load(uri).into(binding.buyAgainImage)


        }

    }
}