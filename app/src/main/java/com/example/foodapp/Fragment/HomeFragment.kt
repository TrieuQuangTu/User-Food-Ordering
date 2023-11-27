package com.example.foodapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodapp.Adapter.MenuAdapter
import com.example.foodapp.BottomSheet.MenuBottomSheetFragment
import com.example.foodapp.Model.MenuItem
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private lateinit var binding:FragmentHomeBinding
    private lateinit var database:FirebaseDatabase
    private lateinit var menuItems:MutableList<MenuItem>




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        binding.ViewAllMenu.setOnClickListener {
            val bottomsheetdialog = MenuBottomSheetFragment()
            bottomsheetdialog.show(parentFragmentManager,"Test")
        }

        //Retrieve and dislay popular menu items
        retrieveAndDisplayPopulItems()
        return binding.root


    }

    private fun retrieveAndDisplayPopulItems() {

        //get reference to the database
        database =FirebaseDatabase.getInstance()
        val foodRef:DatabaseReference =database.reference.child("menu")
        menuItems = mutableListOf()

        //retrieve memu items from the database
        foodRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               for(foodSnapshot in snapshot.children){
                   val menuItem =foodSnapshot.getValue(MenuItem::class.java)
                   menuItem?.let {
                       menuItems.add(it)
                   }

                   //display a random popular items
                   randomPopularItems()
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun randomPopularItems() {
        //create as shuffled list of menu items
        //tao muc danh sach bi xao tron
        val index =menuItems.indices.toList().shuffled()
        val numItemtoShow=6
        val subsetMenuItems =index.take(numItemtoShow).map { menuItems[it] }

        setPopularItemsAdapter(subsetMenuItems)
    }

    private fun setPopularItemsAdapter(subsetMenuItems: List<MenuItem>) {
        val adapterPopular = MenuAdapter(subsetMenuItems,requireContext())

        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = adapterPopular
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ImageSlider
        val imagelist = ArrayList<SlideModel>()
        imagelist.add(SlideModel(R.drawable.banner1,ScaleTypes.FIT))
        imagelist.add(SlideModel(R.drawable.banner2,ScaleTypes.FIT))
        imagelist.add(SlideModel(R.drawable.banner3,ScaleTypes.FIT))

        val imageSlider =binding.imageSlider
        imageSlider.setImageList(imagelist)
        imageSlider.setImageList(imagelist,ScaleTypes.FIT)

        imageSlider.setItemClickListener(object :ItemClickListener{
            override fun doubleClick(position: Int) {

            }

            override fun onItemSelected(position: Int) {
               val itemPosition = imagelist[position]
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(),itemMessage,Toast.LENGTH_SHORT).show()
            }

        })

    }
}