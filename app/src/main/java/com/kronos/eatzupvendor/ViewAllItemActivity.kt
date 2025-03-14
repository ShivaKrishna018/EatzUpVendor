package com.kronos.eatzupvendor

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRegistrar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.kronos.Adapter.AllListAdapter
import com.kronos.Model.JustDataStorage
import com.kronos.eatzupvendor.databinding.ActivityVeiwAllItemBinding

class ViewAllItemActivity : AppCompatActivity() {
    private val listItemBinding: ActivityVeiwAllItemBinding by lazy {
        ActivityVeiwAllItemBinding.inflate(layoutInflater)
    }
    lateinit var adapter: AllListAdapter
    lateinit var database: FirebaseDatabase
    lateinit var firebaseReference: DatabaseReference
    private var justFillData : ArrayList<JustDataStorage> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(listItemBinding.root)
        firebaseReference = FirebaseDatabase.getInstance().reference

        retrieveMenuItem()


//        val cartFoodItems = listOf("salad", "Beef Wellington", "Pasta", "Margherita Pizza", "Chicken Wing", "The Inferno Burger","Briyani", "Noodles", "Pasta", "Fried Rice", "Chicken Wing", "Fries")
//        val cartPriceItems = listOf("110.89","120.99","80.89","130.99","160.99","70.59","110.89","120.99","80.89","130.99","160.99","70.59")
//        val cartImages = listOf(R.drawable.pngegg,R.drawable.beef,R.drawable.pasta_png,R.drawable.pizza_full,R.drawable.chicken_png,R.drawable.burger_png,R.drawable.briyani,R.drawable.noodles,R.drawable.pasta_mid,R.drawable.fried_rice,R.drawable.chicken_png,R.drawable.french_fries)


    }

    private fun retrieveMenuItem() {
        database = FirebaseDatabase.getInstance()
        val dataRef = database.reference.child("vendor").child("Menu")

        // fetch data

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                justFillData.clear()

                    // loop for through each food item

                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(JustDataStorage::class.java)
                    menuItem?.let {
                        justFillData.add(it)
                    }
                }

                setAdapter()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", "Error ${error.message}")
            }
        })

    }

    private fun setAdapter() {

        adapter = AllListAdapter(this@ViewAllItemActivity, justFillData, firebaseReference)
        listItemBinding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        listItemBinding.recyclerView.adapter = adapter

    }
}