package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.foodapp.databinding.ActivityChoseLocationBinding

class ChoseLocationActivity : AppCompatActivity() {

    private lateinit var binding:ActivityChoseLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityChoseLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //autoCompleteTextView : la 1 widget cua Android cho phep nguoi dung nhap van ban va hien thi 1 danh sach
        //cac goi y tu dong khi nguoi dung nhap. Danh sach hien thi la dang drop-down

        //cung cap cho autoCOmpletextview 1 nguon du lieu
        val locationList = arrayListOf("Jaipur","Odisha","Bundi","Sikar")
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,locationList)
        val autoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)
    }
}