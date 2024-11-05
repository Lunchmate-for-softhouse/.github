package com.example.lunchmate

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.model.Restaurant
import androidx.navigation.NavController
import com.example.lunchmate.ui.screens.CreateEvents
import com.example.lunchmate.ui.screens.chaneloc


class RestaurantAdapter(
    private val restaurants: List<Restaurant>,
    private val itemClick: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textName)
        private val addressTextView: TextView = itemView.findViewById(R.id.textAddress)
        private val ratingTextView: TextView = itemView.findViewById(R.id.ratingTextView)
        private val userRatingsTotalTextView: TextView = itemView.findViewById(R.id.userRatingsTotalTextView)
        private val openingHoursTextView: TextView = itemView.findViewById(R.id.openingHoursTextView)
        private val priceLevelTextView: TextView = itemView.findViewById(R.id.priceLevelTextView)
        private val menuButton: Button = itemView.findViewById(R.id.menuButton)

        fun showmenu(restaurant: Restaurant){
            menuButton.text = "View Menu"
            menuButton.setOnClickListener {
                if (!restaurant.website.isNullOrBlank()) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.website))
                    it.context.startActivity(browserIntent)
                } else {
                    Toast.makeText(it.context, "No menu available", Toast.LENGTH_SHORT).show()
                }
            }

        }
        fun handleNameClick(name: String) {

        }

        fun bind(restaurant: Restaurant) {
            nameTextView.text = restaurant.name
            addressTextView.text = restaurant.address
            ratingTextView.text = "Rating: ${restaurant.rating ?: "N/A"}"
            userRatingsTotalTextView.text = "User Ratings: ${restaurant.userRatingsTotal ?: "N/A"}"
            priceLevelTextView.text = "Price Level: ${restaurant.priceLevel ?: "N/A"}"

            // Set the OnClickListener to call the handleNameClick function
            nameTextView.setOnClickListener {
                handleNameClick(restaurant.name)
            }




            // Set button text and click listener
            menuButton.text = "View Menu"
            menuButton.setOnClickListener {
                if (!restaurant.website.isNullOrBlank()) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.website))
                    it.context.startActivity(browserIntent)
                } else {
                    // Logic for showing a placeholder or message when no website is available
                    // For example, you could show a Toast or open a dialog
                    // Here, we are just using a placeholder Toast
                     Toast.makeText(it.context, "No menu available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_list_item, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(restaurants[position])
    }

    override fun getItemCount() = restaurants.size
}