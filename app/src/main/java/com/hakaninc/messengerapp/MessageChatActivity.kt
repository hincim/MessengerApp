package com.hakaninc.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hakaninc.messengerapp.databinding.ActivityMessageChatBinding

class MessageChatActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMessageChatBinding
    var userIDVisit : String = ""
    var firebaseUser : FirebaseUser ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra("visit_id")?.let {
            userIDVisit = intent.getStringExtra("visit_id")!!
        }
        firebaseUser = FirebaseAuth.getInstance().currentUser

        binding.sendMessageBtn.setOnClickListener {

            val message = binding.textMessage.text.toString()

            if (message == ""){
                Toast.makeText(this,"Please write a message, first...",Toast.LENGTH_SHORT).show()
            }else{

                sendMessageToUser(firebaseUser!!.uid, userIDVisit, message)
            }
        }
    }

    private fun sendMessageToUser(uid: String, userIDVisit: String, message: String) {

    }
}