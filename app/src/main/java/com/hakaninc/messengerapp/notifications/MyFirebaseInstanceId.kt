package com.hakaninc.messengerapp.notifications

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.FirebaseMessaging

class MyFirebaseInstanceId : FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refreshToken = FirebaseMessaging.getInstance().token

        if (firebaseUser != null){
            updateToken(refreshToken)
        }
    }

    private fun updateToken(refreshToken: Task<String>) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref  = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token = Token(refreshToken.result)
        ref.child(firebaseUser!!.uid).setValue(token)

    }
}