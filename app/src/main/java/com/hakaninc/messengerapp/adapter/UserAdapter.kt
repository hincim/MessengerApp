package com.hakaninc.messengerapp.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.hakaninc.messengerapp.MainActivity
import com.hakaninc.messengerapp.MessageChatActivity
import com.hakaninc.messengerapp.R
import com.hakaninc.messengerapp.databinding.UserSearchItemLayoutBinding
import com.hakaninc.messengerapp.model.Users
import com.squareup.picasso.Picasso

class UserAdapter(val mContext : Context,
                  val mUsers : List<Users>,
                  isChatCheck : Boolean) : RecyclerView.Adapter<UserAdapter.DesignViewHolder>() {

    class DesignViewHolder(val binding : UserSearchItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DesignViewHolder {
        val view = UserSearchItemLayoutBinding.inflate(LayoutInflater.from(mContext),parent,false)
        return DesignViewHolder(view)
    }

    override fun onBindViewHolder(holder: DesignViewHolder, position: Int) {

        val user : Users = mUsers[position]

        holder.binding.username.text = user.username
    //    Picasso.get().load(user.profile).placeholder(R.drawable.ic_profile).into(holder.binding.profileImage)

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

                }
            })
        }
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }
}














