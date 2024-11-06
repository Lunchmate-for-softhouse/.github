package com.example.lunchmate.ui.screens

import com.google.firebase.firestore.FirebaseFirestore

fun orderCreate(
    orderedBy: String, // Person who ordered the meal
    mealName: String,
    drinkName: String,
    mealPrice: Double,
    drinkPrice: Double
) {
    val db = FirebaseFirestore.getInstance()

    // Create a new order map
    val mealOrder = hashMapOf(
        "orderedBy" to orderedBy,
        "mealName" to mealName,
        "drinkName" to drinkName,
        "mealPrice" to mealPrice,
        "drinkPrice" to drinkPrice,
        "totalPrice" to (mealPrice + drinkPrice)
    )

    // Add meal order to Firestore
    db.collection("mealOrders")
        .add(mealOrder)
        .addOnSuccessListener { documentReference ->
            println("Meal order successfully added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            println("Error adding meal order: $e")
        }
}



fun getAllOrders(onOrdersFetched: (List<Map<String, Any>>) -> Unit, onError: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("mealOrders")
        .get()
        .addOnSuccessListener { result ->
            val orders = result.map { document -> document.data }
            onOrdersFetched(orders)
        }
        .addOnFailureListener { exception ->
            onError(exception)
        }
}

