package com.hakaninc.messengerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }
}














