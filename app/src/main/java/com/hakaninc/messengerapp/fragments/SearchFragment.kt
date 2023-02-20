package com.hakaninc.messengerapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hakaninc.messengerapp.R
import com.hakaninc.messengerapp.adapter.UserAdapter
import com.hakaninc.messengerapp.databinding.FragmentSearchBinding
import com.hakaninc.messengerapp.model.Users

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(R.layout.fragment_search) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var userAdapter : UserAdapter ?= null
    private var mUsers : List<Users> ?= null
    private var fragmentSearchbinding: FragmentSearchBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding =  FragmentSearchBinding.inflate(LayoutInflater.from(context),container,false)
        fragmentSearchbinding = binding

        binding.searchList.setHasFixedSize(true)
        binding.searchList.layoutManager = LinearLayoutManager(context)


        mUsers = ArrayList<Users>()
        retrieveAllUsers()

        binding.searchUsers.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // searchForUsers(p0.toString().lowercase())
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchForUsers(p0.toString().lowercase())
            }

            override fun afterTextChanged(p0: Editable?) {
                // searchForUsers(p0.toString().lowercase())
            }

        })

        return binding.root
    }

    private fun retrieveAllUsers() {

        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()

               if (fragmentSearchbinding!!.searchUsers.toString() == ""){

                   for (s in snapshot.children){

                       val user : Users ?= s.getValue(Users::class.java)
                       if (!(user!!.uid).equals(firebaseUserID)){

                           (mUsers as ArrayList<Users>).add(user)
                       }
                   }
                   userAdapter = context?.let { UserAdapter(it, mUsers!!,false) }
                   fragmentSearchbinding!!.searchList.adapter = userAdapter
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun searchForUsers(str : String){

        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val queryUsers = FirebaseDatabase.getInstance().reference.child("Users")
            .orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                (mUsers as ArrayList<Users>).clear()

                for (s in snapshot.children){

                    val user : Users ?= s.getValue(Users::class.java)
                    if (!(user!!.uid).equals(firebaseUserID)){

                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = context?.let { UserAdapter(it, mUsers!!,false) }
                fragmentSearchbinding!!.searchList.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentSearchbinding = null
    }
}