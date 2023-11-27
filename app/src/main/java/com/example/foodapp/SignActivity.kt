package com.example.foodapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.foodapp.Model.UserModel
import com.example.foodapp.databinding.ActivitySignBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignActivity : AppCompatActivity() {
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var username:String
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var googleSignInclient:GoogleSignInClient

    private lateinit var binding:ActivitySignBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options =GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        //initialize Fibase Database, initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        googleSignInclient = GoogleSignIn.getClient(this,options)

        binding.btnGoogleLogin.setOnClickListener {
            val signIntent =googleSignInclient.signInIntent
            launcher.launch(signIntent)

        }

        binding.btnCreateAccount.setOnClickListener {
            username =binding.editNameSignup.text.toString().trim()
            email =binding.editEmailSignup.text.toString().trim()
            password =binding.editPasswordSignup.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"Please fill information",Toast.LENGTH_SHORT).show()
            }else{
                CreateAccount(email,password)
            }
        }

        //click already have account
        binding.txtAlready.setOnClickListener {
            val intent = Intent(this@SignActivity,LoginActivity::class.java)
            startActivity(intent)
        }
    }
    private val launcher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if (result.resultCode == Activity.RESULT_OK){
            val task =GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account:GoogleSignInAccount =task.result
                val credential =GoogleAuthProvider.getCredential(account.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Sign In Google Successfully",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(this,"Sign In field",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else{
            Toast.makeText(this,"Sign in failed",Toast.LENGTH_SHORT).show()
        }
    }

    private fun CreateAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    Toast.makeText(this,"Account created successfully",Toast.LENGTH_SHORT).show()
                    saveUserData()
                    startActivity(Intent(this@SignActivity,LoginActivity::class.java))
                    finish()
                }else {
                    Toast.makeText(this,"Account creation failed",Toast.LENGTH_SHORT).show()
                    Log.d("Account","creatAccount: Failure",task.exception)
                }

            }
    }

    private fun saveUserData() {
        //retrive data from input file
        username =binding.editNameSignup.text.toString().trim()
        email =binding.editEmailSignup.text.toString().trim()
        password =binding.editPasswordSignup.text.toString().trim()

        val user = UserModel(username,email,password)
        val userId =FirebaseAuth.getInstance().currentUser!!.uid

        //save database to Firebase database
        database.child("user").child(userId).setValue(user)
    }
}