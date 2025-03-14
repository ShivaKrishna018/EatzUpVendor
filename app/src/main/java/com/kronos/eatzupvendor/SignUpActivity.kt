package com.kronos.eatzupvendor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.emptyIntSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.kronos.Model.UserInfo
import com.kronos.eatzupvendor.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private val signUpBinding : ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    lateinit var username: String
    lateinit var restaurantName: String
    lateinit var email:String
    lateinit var password: String
    lateinit var auth : FirebaseAuth
    lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(signUpBinding.root)

        auth = Firebase.auth
        database = Firebase.database.reference



        signUpBinding.createAccount.setOnClickListener {
            username = signUpBinding.userName.text.toString().trim()
            restaurantName = signUpBinding.restaurantName.text.toString().trim()
            email = signUpBinding.emailAddress.text.toString().trim()
            password = signUpBinding.password.text.toString().trim()
            if (username.isBlank() || restaurantName.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Oops! Looks like a few fields are missing.", Toast.LENGTH_SHORT).show()
            }else{
                authenticate(email, password)
            }
        }

        signUpBinding.alreadyAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun authenticate(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            if(task.isSuccessful) {
                Toast.makeText(this, "account created", Toast.LENGTH_SHORT).show()
                storeInfo()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, "Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun storeInfo() {
        username = signUpBinding.userName.text.toString().trim()
        restaurantName = signUpBinding.restaurantName.text.toString().trim()
        email = signUpBinding.emailAddress.text.toString().trim()
        password = signUpBinding.password.text.toString().trim()

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {

            val userId = currentUser.uid
            val user = UserInfo(username, restaurantName, email, password)
            database.child("user").child(userId).setValue(user).addOnCompleteListener{ taskInfo ->

                if (taskInfo.isSuccessful) {
                    Log.d("Firebase", "Data saved successfully")

                    Toast.makeText(this, "data Saved", Toast.LENGTH_SHORT).show()

                }else{
                    Log.e("Firebase", "Error saving data: ${taskInfo.exception?.message}")
                    Toast.makeText(this, "failed: ${taskInfo.exception?.message}" , Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}