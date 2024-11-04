package com.example.lunchmate.com.example.lunchmate.ui.screens

import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.lunchmate.ui.screens.ChatScreen


@Composable
fun EventDetails(navController: NavController, eventName: String, userid:String){

    ChatScreen(eventName,userid)



}