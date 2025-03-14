package com.kronos.eatzupvendor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.internal.GoogleSignInOptionsExtensionParcelable
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.kronos.Model.UserInfo
import com.kronos.eatzupvendor.databinding.ActivityLoginBinding
import kotlin.math.log

class LoginActivity : AppCompatActivity() {

    private val  loginBinding : ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val username: String? = null
    private val restaurantName: String? = null
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(loginBinding.root)

        //initialize auth and database
        auth = Firebase.auth
        database = Firebase.database.reference



        configureGoogleSignIn()
        configureSignInLauncher()

        loginBinding.signInButton.setOnClickListener {
            signInWithGoogle()
        }
        signInWithGoogle()




        loginBinding.createAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }



        loginBinding.loginButton.setOnClickListener {
            email = loginBinding.emailAddress.text.toString().trim()
            password = loginBinding.password.text.toString().trim()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Oops! Looks like a few fields are missing.", Toast.LENGTH_SHORT).show()
            }else{
                loginMain(email, password)
                loginBinding.loginButton.isClickable = false

            }
        }
    }




    private fun configureGoogleSignIn() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


    }

    private fun configureSignInLauncher() {
        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            }catch (e: ApiException) {
                Log.e("GoogleSignIn", "Google sign-in failed", e)
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)

    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if(task.isSuccessful) {
                val user = auth.currentUser
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Google sign-in successful", Toast.LENGTH_SHORT).show()
                finish()
            }else {
                Log.e("GoogleSignIn", "Firebase authenticate failed", task.exception)
                Toast.makeText(this, "Firebase authenticate failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun loginMain(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveData()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }else {
                startActivity(Intent(this, SignUpActivity::class.java))
                loginBinding.loginButton.isClickable = true
                Toast.makeText(this, "create account", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun saveData() {
        email = loginBinding.emailAddress.text.toString().trim()
        password = loginBinding.password.text.toString().trim()
        
        val user = UserInfo(username, restaurantName, email, password)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        
        userId?.let {
            database.child("user").child(it).setValue(user)
        }
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
    }
    
}