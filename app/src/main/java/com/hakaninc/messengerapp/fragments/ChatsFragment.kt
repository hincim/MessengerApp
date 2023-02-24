package com.hakaninc.messengerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import com.hakaninc.messengerapp.R
import com.hakaninc.messengerapp.adapter.UserAdapter
import com.hakaninc.messengerapp.databinding.FragmentChatsBinding
import com.hakaninc.messengerapp.model.ChatList
import com.hakaninc.messengerapp.model.Users
import com.hakaninc.messengerapp.notifications.Token


class ChatsFragment : Fragment() {

    private var fragmentChatsbinding : FragmentChatsBinding ?= null
    private var userAdapter : UserAdapter ?= null
    private var mUsers : List<Users> ?= null
    private var usersChatList : List<ChatList> ?= null
    private var firebaseUser : FirebaseUser ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentChatsBinding.inflate(LayoutInflater.from(context),container,false)
        fragmentChatsbinding = binding


        binding.recyclerViewChatlist.setHasFixedSize(true)
        binding.recyclerViewChatlist.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                (usersChatList as ArrayList).clear()

                for (s in snapshot.children){

                    val chatList = snapshot.getValue(ChatList::class.java)

                    (usersChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        updateToken(FirebaseMessaging.getInstance().token)

        return binding.root
    }

    private fun updateToken(token: Task<String>) {

        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token.result)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retrieveChatList(){

        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                (mUsers as ArrayList).clear()

                for (s in snapshot.children){

                    val user = s.getValue(Users::class.java)

                    for (eachChatList in usersChatList!!){

                        if (user?.uid.equals(eachChatList.id)){

                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }

                context?.let {
                    userAdapter = UserAdapter(it, (mUsers as ArrayList<Users>), true)
                    fragmentChatsbinding!!.recyclerViewChatlist.adapter = userAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentChatsbinding = null
    }
}







