package com.hakaninc.messengerapp.notifications

import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hakaninc.messengerapp.MessageChatActivity

class MyFirebaseMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(mRemoteMessage : RemoteMessage) {
        super.onMessageReceived(mRemoteMessage)

        val sented = mRemoteMessage.data["sented"]

        val user = mRemoteMessage.data["user"]

        val sharedPref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        val currentOnlineUser = sharedPref.getString("currentUser", "none")

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null && sented == firebaseUser.uid){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                sendOreoNotification(mRemoteMessage)
            }else{
                sendOreoNotification(mRemoteMessage)

            }
        }
    }

    private fun sendOreoNotification(mRemoteMessage: RemoteMessage) {

        val user = mRemoteMessage.data["user"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]

        val notification = mRemoteMessage.notification
        val j = user!!.replace("[\\D]".toRegex(),"").toInt()
        val intent = Intent(this,MessageChatActivity::class.java)
    }
}