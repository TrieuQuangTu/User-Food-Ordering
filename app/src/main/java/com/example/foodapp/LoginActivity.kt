package com.example.foodapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.foodapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient


    private lateinit var binding:ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        //initialize Firebase
        auth= FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        googleSignInClient =GoogleSignIn.getClient(this,options)

        //Button Login
        binding.btnLogin.setOnClickListener {

            //get data from editText
            email =binding.editEmailSignup.text.toString().trim()
            password =binding.editPasswordSignup.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"Please fill details",Toast.LENGTH_SHORT).show()
            }else{
                CreateUser()
                Toast.makeText(this,"Login Successfully",Toast.LENGTH_SHORT).show()
            }
        }



        //click don't have account
        binding.txtDonthaveaccount.setOnClickListener {
            val intent = Intent(this@LoginActivity,SignActivity::class.java)
            startActivity(intent)
        }

        //Button Login with Google
        binding.btnGoogleLogin.setOnClickListener {
            val signIntent =googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }
    }
    private var launcher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if (result.resultCode == Activity.RESULT_OK){
            var task =  GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account:GoogleSignInAccount =task.result
                val credential =GoogleAuthProvider.getCredential(account.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener {task->
                    if (task.isSuccessful){
                        Toast.makeText(this,"Sign In Google Successfully",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }else {
                        Toast.makeText(this,"Sign In field",Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }else{
            Toast.makeText(this,"Sign In field",Toast.LENGTH_SHORT).show()

        }
    }

    private fun CreateUser() {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task->
            if (task.isSuccessful){
                val user =auth.currentUser
                updateUI(user)
            }else {
                Toast.makeText(this,"Sign In fail",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}