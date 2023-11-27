package com.example.foodapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.foodapp.Model.UserModel
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileFragment : Fragment() {
    private lateinit var binding:FragmentProfileBinding
    private lateinit var auth :FirebaseAuth
    private  var database= FirebaseDatabase.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        binding.apply {
            //vo hieu hoa editText trong profile
            profileName.isEnabled=false
            profileEmail.isEnabled=false
            profileAddress.isEnabled=false

            profilePhone.isEnabled=false

            editButton.setOnClickListener {
                profileName.isEnabled =!profileName.isEnabled
                profileEmail.isEnabled =!profileEmail.isEnabled
                profileAddress.isEnabled =!profileAddress.isEnabled
                profilePhone.isEnabled =!profilePhone.isEnabled
            }
        }
        //click Edit Profile

        setUserData()

        binding.ButtonSave.setOnClickListener {
            val name =binding.profileName.text.toString()
            val email =binding.profileEmail.text.toString()
            val address =binding.profileAddress.text.toString()
            val phone =binding.profilePhone.text.toString()

            updateUserData(name,email,address,phone)
        }

        return binding.root
    }

    private fun updateUserData(name: String, email: String, address: String, phone: String) {
        val userId =auth.currentUser?.uid
        if (userId !=null){
            val userReference =database.getReference("user").child(userId)
            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "address" to address,
                "phone" to phone
            )
            userReference.setValue(userData).addOnSuccessListener {
                Toast.makeText(requireContext(),"Profile Update Successfully",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"Profile Update Failed:${it.message}",Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun setUserData(){
        auth = FirebaseAuth.getInstance()
        val userId =auth.currentUser?.uid
        if(userId !=null){
            val userRef = database.getReference("user").child(userId)

            userRef.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val userProfile =snapshot.getValue(UserModel::class.java)
                        if (userProfile !=null){
                            binding.profileName.setText(userProfile.name)
                            binding.profileAddress.setText(userProfile.address)
                            binding.profileEmail.setText(userProfile.email)
                            binding.profilePhone.setText(userProfile.phone)
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