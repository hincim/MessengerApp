package com.hakaninc.messengerapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.hakaninc.messengerapp.adapter.ChatsAdapter
import com.hakaninc.messengerapp.databinding.ActivityMessageChatBinding
import com.hakaninc.messengerapp.model.Chat
import com.hakaninc.messengerapp.model.Users
import com.squareup.picasso.Picasso

class MessageChatActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMessageChatBinding
    var userIDVisit : String = ""
    var firebaseUser : FirebaseUser ?= null
    var chatsAdapter : ChatsAdapter ?= null
    var mChatList : List<Chat> ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra("visit_id")?.let {
            userIDVisit = intent.getStringExtra("visit_id")!!
        }
        firebaseUser = FirebaseAuth.getInstance().currentUser

        binding.recyclerViewChats.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        binding.recyclerViewChats.layoutManager = linearLayoutManager


        val reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIDVisit)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val user : Users ?= snapshot.getValue(Users::class.java)

                binding.usernameMchat.text = user!!.username
            //    Picasso.get().load(user.profile).into(binding.profileImageMchat)

                retrieveMessages(firebaseUser!!.uid, userIDVisit, user.profile)
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

            binding.textMessage.setText("")
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
                val chatsListReference = FirebaseDatabase.getInstance().reference
                    .child("ChatList")
                    .child(firebaseUser!!.uid)
                    .child(userIDVisit)

                chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (!snapshot.exists()){
                            chatsListReference.child("id").setValue(userIDVisit)
                        }
                        val chatsListReceiverRef = FirebaseDatabase.getInstance().reference
                            .child("ChatList")
                            .child(userIDVisit)
                            .child(firebaseUser!!.uid)
                        chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })



                // implement the push notifications using fcm
                val reference = FirebaseDatabase.getInstance().reference
                    .child("Users").child(firebaseUser!!.uid)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.data != null){

            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Please wait, image is sending...")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageID = ref.push().key
            val filePath = storageReference.child("$messageID.jpg")

            var uploadTask : StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask<Uri?>(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if (!task.isSuccessful){

                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl

            }).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "send you an image."
                    messageHashMap["receiver"] = userIDVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageID"] = messageID

                    ref.child("Chats").child(messageID!!).setValue(messageHashMap)

                    progressBar.dismiss()
                }
            }
        }
    }


    private fun retrieveMessages(senderID: String, receiverID: String, receiverImageUrl: String?) {

        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                (mChatList as ArrayList<Chat>).clear()

                for (s in snapshot.children){
                    val chat = s.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(senderID) && chat.sender.equals(receiverID)
                        || chat.receiver.equals(receiverID) && chat.sender.equals(senderID)){


                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                    chatsAdapter = ChatsAdapter(this@MessageChatActivity, mChatList as ArrayList<Chat>,receiverImageUrl!!)
                    binding.recyclerViewChats.adapter = chatsAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}


















