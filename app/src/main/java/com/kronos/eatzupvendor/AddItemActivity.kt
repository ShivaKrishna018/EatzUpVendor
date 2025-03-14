package com.kronos.eatzupvendor

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.kronos.Model.FirebaseModel
import com.kronos.Model.JustDataStorage
import com.kronos.eatzupvendor.databinding.ActivityAddItemBinding

class AddItemActivity : AppCompatActivity() {
    private val addBinding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    lateinit var dishName: String
    lateinit var dishPrice: String
    lateinit var dishDescription: String
    private var dishImage: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(addBinding.root)

        //init
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        addBinding.proceedBtn.setOnClickListener {
            dishName = addBinding.dishName.text.toString().trim()
            dishPrice = addBinding.dishPrice.text.toString().trim()
            dishDescription = addBinding.dishDescription.text.toString().trim()

            if (!(dishName.isBlank() || dishPrice.isBlank() || dishDescription.isBlank())) {
                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                uploadImage()
                finish()
            } else {
                Toast.makeText(this, "please fill all the items", Toast.LENGTH_SHORT).show()
            }

        }


//        addBinding.selectImage.setOnClickListener {
//            PickImage.launch("image/*")
//        }


        addBinding.selectImageInfo.setOnClickListener {
            PickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }


    val PickImage: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

            if (uri != null) {
                addBinding.selectImageInfo.setImageURI(uri)
                dishImage = uri
            }

        }

//    val PickImage : ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//
//        if (uri != null){
//            addBinding.selectImageInfo.setImageURI(uri)
//        }
//
//    }


    fun uploadImage() {
        // get reference of the "menu" node in the database
        val menuReference = database.getReference("menu")
        //generate the new unique key for the new menu item
        val newItemKey = menuReference.push().key

        if (dishImage != null) {
            val cloudStorage = FirebaseStorage.getInstance().reference
            val imageReference = cloudStorage.child("menu_image${newItemKey}.jpg")
            val uploadTask = imageReference.putFile(dishImage!!)

            uploadTask.addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener { downloadUrl ->

                    val newItem = FirebaseModel(
                        foodName = dishName,
                        foodPrice = dishPrice,
                        foodDescription = dishDescription,
                        foodImage = downloadUrl.toString()
                    )

                    newItemKey?.let { key ->
                        menuReference.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this, "data transfer", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "data transfer failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                }
            }.addOnFailureListener {
                val newInfo = JustDataStorage(
                    foodName = dishName,
                    foodPrice = dishPrice,
                    foodDescription = dishDescription,


                )
                newItemKey?.let { key ->
                    menuReference.child(key).setValue(newInfo).addOnSuccessListener {
                        Toast.makeText(this, "just data transfer", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }else {
            Toast.makeText(this, "image upload failed", Toast.LENGTH_SHORT).show()
        }

    }
}