package com.hakaninc.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hakaninc.messengerapp.databinding.ActivityMessageChatBinding
import com.hakaninc.messengerapp.model.Users
import com.squareup.picasso.Picasso

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

        val reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIDVisit)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val user : Users ?= snapshot.getValue(Users::class.java)

                binding.usernameMchat.text = user!!.username
                Picasso.get().load(user.profile).into(binding.profileImageMchat)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.sendMessageBtn.setOnClickListener {

            val message = binding.textMessage.text.toString()

            if (message == ""){
                Toast.makeText(this,"Please write a message, first...",Toast.LENGTH_SHORT).show()
            }else{

                sendMessageToUser(firebaseUser!!.uid, userIDVisit, message)
            }
        }

        binding.attactImageFileBtn.setOnClickListener {

            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"),438)

        }
    }

    private fun sendMessageToUser(senderID: String, receiverID: String, message: String) {

        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderID
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverID
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageID"] = messageKey
        reference.child("Chats").child(messageKey!!).setValue(messageHashMap).addOnCompleteListener {

            if (it.isSuccessful){
                val chatsListReference = FirebaseDatabase.getInstance().reference.child("ChatList")

                // implement the push notifications using fcm
                chatsListReference.child("id").setValue(firebaseUser!!.uid)
                val reference = FirebaseDatabase.getInstance().reference
                    .child("Users").child(firebaseUser!!.uid)
            }
        }

    }
}


















