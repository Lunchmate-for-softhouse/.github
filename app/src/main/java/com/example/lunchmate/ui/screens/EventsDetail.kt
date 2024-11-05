package com.example.lunchmate.com.example.lunchmate.ui.screens

import ChatScreen
import androidx.compose.runtime.*
import androidx.navigation.NavController



@Composable
fun EventDetails(navController: NavController, eventName: String, userid:String){

    ChatScreen(eventName,userid)



}