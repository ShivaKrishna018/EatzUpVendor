package com.kronos.eatzupvendor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kronos.eatzupvendor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        mainBinding.DeliveryPost.setOnClickListener {
            startActivity(Intent(this, ViewAllItemActivity::class.java))
        }

        mainBinding.AddToCart.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }

        mainBinding.Profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

    }
}