package com.hakaninc.messengerapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hakaninc.messengerapp.databinding.ActivityMainBinding
import com.hakaninc.messengerapp.fragments.ChatsFragment
import com.hakaninc.messengerapp.fragments.SearchFragment
import com.hakaninc.messengerapp.fragments.SettingsFragment
import com.hakaninc.messengerapp.model.Chat
import com.hakaninc.messengerapp.model.Users
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var refUsers : DatabaseReference ?= null
    var firebaseUser : FirebaseUser ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        setSupportActionBar(binding.toolbarMain)
        supportActionBar!!.title = ""

        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var countUnreadMessages = 0

                for (s in snapshot.children){

                    val chat = s.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(firebaseUser!!.uid) && !chat.isseen){

                        countUnreadMessages += 1
                    }
                }
                if (countUnreadMessages == 0){
                    viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
                }else{
                    viewPagerAdapter.addFragment(ChatsFragment(), "($countUnreadMessages) Chats")
                }

                viewPagerAdapter.addFragment(SearchFragment(), "Search")
                viewPagerAdapter.addFragment(SettingsFragment(), "Settings")
                binding.viewPager.adapter = viewPagerAdapter
                binding.tabLayout.setupWithViewPager(binding.viewPager)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        refUsers?.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    val user : Users?= snapshot.getValue(Users::class.java)

                    binding.userName.text = user!!.username
                    // Picasso.get().load(user.profile).placeholder(R.drawable.ic_profile).into(binding.profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this,WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

                return true
            }
        }
        return false
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager){

        private val fragments : ArrayList<Fragment>
        private val titles : ArrayList<String>

        init {
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()
        }

        override fun getCount(): Int {

            return fragments.size
        }

        override fun getItem(position: Int): Fragment {

            return fragments[position]
        }

        fun addFragment(fragment: Fragment, title:String){

            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }


}


















