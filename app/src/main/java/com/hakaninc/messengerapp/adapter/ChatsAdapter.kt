package com.hakaninc.messengerapp.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.hakaninc.messengerapp.R
import com.hakaninc.messengerapp.ViewFullImageActivity
import com.hakaninc.messengerapp.WelcomeActivity
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
                Picasso.get().load(chat.url).into(holder.right_image_view)

                holder.right_image_view!!.setOnClickListener {

                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Delete Image",
                        "Cancel"
                    )

                    var builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")

                    builder.setItems(options, DialogInterface.OnClickListener{
                        dialogInterface, i ->

                        if (i == 0){

                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.url)
                            mContext.startActivity(intent)

                        } else if (i == 1){

                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }

                // image message - left side
            } else if (!chat.sender.equals(firebaseUser!!.uid)) {

                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.left_image_view)

                holder.left_image_view!!.setOnClickListener {

                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Cancel"
                    )

                    var builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")

                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialogInterface, i ->

                        if (i == 0){

                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.url)
                            mContext.startActivity(intent)

                        }
                    })
                    builder.show()
                }

            }
        }
        // text messages
        else{
            holder.show_text_message!!.text = chat.message

            if (firebaseUser.uid == chat.sender){

                holder.show_text_message!!.setOnClickListener {

                    val options = arrayOf<CharSequence>(
                        "Delete Message",
                        "Cancel"
                    )

                    var builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")

                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialogInterface, i ->
                        if (i == 0){

                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            }


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

    private fun deleteSentMessage(position : Int, holder : ChatsAdapter.ChatDesignViewHolder){

        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList.get(position).messageID)
            .removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful){

                    Toast.makeText(holder.itemView.context, "Deleted", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(holder.itemView.context, "Failed, Not Deleted", Toast.LENGTH_SHORT).show()
                }
            }

    }
}













