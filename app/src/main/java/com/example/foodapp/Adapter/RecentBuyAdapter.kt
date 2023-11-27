package com.example.foodapp.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.CustomRecentBuyBinding

class RecentBuyAdapter(private val context: Context,
    private val foodNameList:ArrayList<String>,
    private val foodImageList:ArrayList<String>,
    private val foodPriceList:ArrayList<String>,
    private val foodQuantityList:ArrayList<Int>)
    :RecyclerView.Adapter<RecentBuyAdapter.RecentViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding =CustomRecentBuyBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RecentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodNameList.size
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class RecentViewHolder(val binding:CustomRecentBuyBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                txtNameRecentbuy.text =foodNameList[position]
                txtPricePopular.text =foodPriceList[position]
                txtQuantityRecentbuy.text =foodQuantityList[position].toString()
                val uriString =foodImageList[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(imgRencentbuy)
            }
        }

    }
}