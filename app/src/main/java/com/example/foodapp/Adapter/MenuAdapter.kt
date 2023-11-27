package com.example.foodapp.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.DetailActivity
import com.example.foodapp.Model.MenuItem
import com.example.foodapp.databinding.MenuItemBinding

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val requireContext:Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailActivity(position)
                }

            }
        }

        //set data into recyclerview  item: name,price,image
        fun bind(position: Int) {
            val menuItem =menuItems[position]
            binding.apply {
                menuFoodName.text =menuItem.foodName
                menuPrice.text =menuItem.foodPrice
                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(menuImage)


            }
        }
    }

    private fun openDetailActivity(position: Int) {
        val menuItem =menuItems[position]

        //a intent to open Activity and pass data
        val intent =Intent(requireContext,DetailActivity::class.java)
        intent.putExtra("MenuItemName",menuItem.foodName)
        intent.putExtra("MenuItemImage",menuItem.foodImage)
        intent.putExtra("MenuItemDescription",menuItem.foodDescription)
        intent.putExtra("MenuItemIngredient",menuItem.foodIngredient)
        intent.putExtra("MenuItemPrice",menuItem.foodPrice)

        requireContext.startActivity(intent)
    }
}


