package com.example.lunchmate.com.example.lunchmate.model

data class Order(

    var name:String,
    var mealName: String,
    var drink: String,
    var mealPrice: Double,
    var drinkPrice: Double
)
{
    val totalPrice: Double
        get() = mealPrice + drinkPrice
}


