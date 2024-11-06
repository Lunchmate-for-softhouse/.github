package com.example.lunchmate.com.example.lunchmate.model

data class Order(
    val creator: String = "",
    val drinkName: String = "",
    val mealName: String = "",
    val mealPrice: Double = 0.0,
    val drinkPrice: Double = 0.0
) {
    val totalPrice: Double
        get() = mealPrice + drinkPrice
}