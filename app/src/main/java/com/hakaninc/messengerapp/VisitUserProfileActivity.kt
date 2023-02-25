package com.hakaninc.messengerapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hakaninc.messengerapp.databinding.ActivityVisitUserProfileBinding
import com.hakaninc.messengerapp.model.Users
import com.squareup.picasso.Picasso

class VisitUserProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityVisitUserProfileBinding
    private var userVisitId : String = ""
    var user : Users? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userVisitId = intent.getStringExtra("visit_id")!!

        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userVisitId)
        ref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    user = snapshot.getValue(Users::class.java)

                    binding.usernameDisplay.text = user?.username
                    Picasso.get().load(user!!.profile).into(binding.profileDisplay)
                    Picasso.get().load(user!!.cover).into(binding.coverDisplay)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.facebookDisplay.setOnClickListener {

            val uri = Uri.parse(user!!.facebook)

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        binding.instagramDisplay.setOnClickListener {

            val uri = Uri.parse(user!!.instagram)

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        binding.facebookDisplay.setOnClickListener {

            val uri = Uri.parse(user!!.website)

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        binding.sendMsgBtn.setOnClickListener {

            val intent = Intent(this@VisitUserProfileActivity, MessageChatActivity::class.java)
            intent.putExtra("visit_id", user!!.uid)
            startActivity(intent)
        }
    }
}













