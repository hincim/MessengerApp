package com.hakaninc.messengerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hakaninc.messengerapp.R
import com.hakaninc.messengerapp.databinding.MessageItemLeftBinding
import com.hakaninc.messengerapp.databinding.MessageItemRightBinding
import com.hakaninc.messengerapp.databinding.UserSearchItemLayoutBinding
import com.hakaninc.messengerapp.model.Chat
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(
    val mContext : Context,
    val mChatList : List<Chat>,
    val imageUrl : String
) : RecyclerView.Adapter<ChatsAdapter.ChatDesignViewHolder>() {

    var firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    inner class ChatDesignViewHolder( itemView : View) : RecyclerView.ViewHolder(itemView){

        var profile_image : CircleImageView ?= null
        var show_text_message : TextView ?= null
        var left_image_view : ImageView ?= null
        var text_seen : TextView ?= null
        var right_image_view : ImageView ?= null

        init {

            profile_image = itemView.findViewById(R.id.profile_image)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)

        }
    }

    override fun getItemViewType(position: Int): Int {



        return if (mChatList[position].sender.equals(firebaseUser!!.uid)){

            1
        }else{

            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ChatDesignViewHolder {

        return if (position == 1){

            val view : View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ChatDesignViewHolder(view)
        }else{
            val view : View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ChatDesignViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ChatDesignViewHolder, position: Int) {

        val chat : Chat = mChatList[position]

        // Picasso.get().load(imageUrl).into(holder.profile_image)

        // images messages
        if (chat.message.equals("send you an image.") && !chat.url.equals("")){

            // image message - rigt side
            if (chat.sender.equals(firebaseUser!!.uid)){

                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                // Picasso.get().load(chat.url).into(holder.right_image_view)

                // image message - left side
            } else if (!chat.sender.equals(firebaseUser!!.uid)) {

                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.left_image_view)
            }
        }
        // text messages
        else{
            holder.show_text_message!!.text = chat.message

        }

        // send and seen message
        if (position == mChatList.size-1){

           if (chat.isseen){

               holder.text_seen!!.text = "Seen"

               if (chat.message.equals("send you an image.") && !chat.url.equals("")){

                   val lp : RelativeLayout.LayoutParams ?= holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                   lp!!.setMargins(0, 245, 10, 0)
                   holder.text_seen!!.layoutParams = lp
               }
           }else{

               holder.text_seen!!.text = "Sent"

               if (chat.message.equals("send you an image.") && !chat.url.equals("")){

                   val lp : RelativeLayout.LayoutParams ?= holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                   lp!!.setMargins(0, 245, 10, 0)
                   holder.text_seen!!.layoutParams = lp
               }
           }

        }else{

            holder.text_seen!!.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }
}