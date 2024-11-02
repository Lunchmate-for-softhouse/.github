package com.example.lunchmate.model

data class Order(
    val name: String,
    val mealName: String,
    val drink: String,
    val mealPrice: Double,
    val drinkPrice: Double
) {
    val totalPrice: Double
        get() = mealPrice + drinkPrice
}
