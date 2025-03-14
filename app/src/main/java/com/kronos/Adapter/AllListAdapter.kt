package com.kronos.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.kronos.Model.JustDataStorage
import com.kronos.eatzupvendor.R
import com.kronos.eatzupvendor.databinding.ItemListLayoutBinding
import kotlin.contracts.contract

class AllListAdapter(val context: Context, val menuItem : ArrayList<JustDataStorage>, databaseReference: DatabaseReference): RecyclerView.Adapter<AllListAdapter.AllViewHolder>() {

    val quantities = IntArray(menuItem.size) {1}

    //private val defaultImage = R.drawable.pngegg


    inner class AllViewHolder(var binding: ItemListLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val mainQuantity = quantities[position]
            val menuItem = menuItem[position]
            // if firebase storage does work

            //val uriString = menuItem.foodImage
           // val uri = Uri.parse(uriString)
            //Glide.with(context).load(uri).into(foodImageList)

            //if firebase storage doesn't work

//            if(!menuItem.foodImageReference.isNullOrEmpty()) {
//                Glide.with(context).load(menuItem.foodImageReference).into(binding.foodImageList)
//            }else{
//                Glide.with(context).load(defaultImage).into(binding.foodImageList)
//            }

            binding.FoodNameList.text = menuItem.foodName

            // default method
//           binding.foodImageList.setImageResource(mainImage)

            binding.amount.text = menuItem.foodPrice

            binding.AddItem.text = mainQuantity.toString()
            binding.plusButton.setOnClickListener {
                increaseItem(position)

            }
            binding.minusButton.setOnClickListener {

                decreaseItem(position)

            }
        }

        private fun increaseItem(position: Int) {
            if (quantities[position] < 10) {
                quantities[position]++
                binding.AddItem.text = quantities[position].toString()
            }
        }


        private fun decreaseItem(position: Int) {
            if (quantities[position] != 1) {
                quantities[position]--
                binding.AddItem.text = quantities[position].toString()
            }else{
                menuItem.removeAt(position)
                menuItem.removeAt(position)
                //menuItem.removeAt(position)
                notifyItemRemoved(position)
              notifyItemRangeChanged(position, menuItem.size)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllViewHolder {
        return AllViewHolder(ItemListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: AllViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
       return menuItem.size


    }


}