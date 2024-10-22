package com.example.lunchmate.repository

import android.app.Activity
import android.content.Intent
import com.example.lunchmate.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepository(private val activity: Activity) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Create a Google Sign-In client with default options
    fun createGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(activity, gso)
    }

    // Authenticate with Firebase using the Google account
    fun firebaseAuthWithGoogle(
        account: GoogleSignInAccount?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    task.exception?.let { onFailure(it) }
                }
            }
    }

    // Get the intent to start the Google Sign-In flow
    fun getGoogleSignInIntent(): Intent {
        return createGoogleSignInClient().signInIntent
    }
}
