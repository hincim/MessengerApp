package com.hakaninc.messengerapp.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hakaninc.messengerapp.MainActivity
import com.hakaninc.messengerapp.MessageChatActivity
import com.hakaninc.messengerapp.R
import com.hakaninc.messengerapp.VisitUserProfileActivity
import com.hakaninc.messengerapp.databinding.UserSearchItemLayoutBinding
import com.hakaninc.messengerapp.model.Chat
import com.hakaninc.messengerapp.model.Users
import com.squareup.picasso.Picasso

class UserAdapter(val mContext : Context,
                  val mUsers : List<Users>,
                  val isChatCheck : Boolean) : RecyclerView.Adapter<UserAdapter.DesignViewHolder>() {

    class DesignViewHolder(val binding : UserSearchItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    var lastMsg : String = ""



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DesignViewHolder {
        val view = UserSearchItemLayoutBinding.inflate(LayoutInflater.from(mContext),parent,false)
        return DesignViewHolder(view)
    }

    override fun onBindViewHolder(holder: DesignViewHolder, position: Int) {

        val user : Users = mUsers[position]

        holder.binding.username.text = user.username
    //    Picasso.get().load(user.profile).placeholder(R.drawable.ic_profile).into(holder.binding.profileImage)

        if (isChatCheck){

            retrieveLastMessage(user.uid, holder.binding.messageLast)
        } else{

            holder.binding.messageLast.visibility = View.GONE
        }

        if (isChatCheck){

            if (user.status == "online"){

                holder.binding.imageOnline.visibility = View.VISIBLE
                holder.binding.imageOffline.visibility = View.GONE

            }else {

                holder.binding.imageOnline.visibility = View.GONE
                holder.binding.imageOffline.visibility = View.VISIBLE

            }
        }else {

            holder.binding.imageOnline.visibility = View.GONE
            holder.binding.imageOffline.visibility = View.GONE

        }

        holder.itemView.setOnClickListener {

            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
                )
            val builder : AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialogInterface, i ->

                if (position == 0){
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("visit_id", user.uid)
                    mContext.startActivity(intent)
                }
                if (position == 1){
                    val intent = Intent(mContext, VisitUserProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("visit_id", user.uid)
                    mContext.startActivity(intent)
                }
            })

            builder.show()
        }
    }

    private fun retrieveLastMessage(chatUserId: String?, messageLast: TextView) {

        lastMsg = "defaultMsg"

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (s in snapshot.children){
                    val chat : Chat? = s.getValue(Chat::class.java)

                    if (firebaseUser != null && chat != null){

                        if (chat.receiver == firebaseUser.uid && chat.sender == chatUserId || chat.receiver == chatUserId && chat.sender == firebaseUser.uid){

                            lastMsg = chat.message
                        }
                    }
                }
                when(lastMsg){

                    "defaultMsg" -> messageLast.text = "No message"
                    "sent you an image." -> messageLast.text = "image sent"

                    else -> messageLast.text = lastMsg
                }
                lastMsg = "defaultMsg"
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }
}














