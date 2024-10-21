package com.example.lunchmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.model.Restaurant

class RestaurantAdapter(
    private val restaurantList: MutableList<Restaurant>,
    private val clickListener: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val restaurantName: TextView = itemView.findViewById(R.id.textName)
        private val restaurantAddress: TextView = itemView.findViewById(R.id.textAddress)

        fun bind(restaurant: Restaurant) {
            restaurantName.text = restaurant.name
            restaurantAddress.text = restaurant.address
            itemView.setOnClickListener { clickListener(restaurant) }  // Pass the clicked restaurant to the listener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_list_item, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(restaurant = restaurantList[position])
    }

    override fun getItemCount(): Int = restaurantList.size
}