package com.hakaninc.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hakaninc.messengerapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers : DatabaseReference
    private var firebaseUserID : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarRegister)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbarRegister.setNavigationOnClickListener {
            val intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        binding.registerBtn.setOnClickListener {

            registerUser()
        }
    }

    private fun registerUser() {

        val userName : String = binding.usernameRegister.text.toString().trim()
        val email : String = binding.emailRegister.text.toString()
        val password : String = binding.passwordRegister.text.toString()

        when {
            userName == "" -> {
                Toast.makeText(this,"please write username",Toast.LENGTH_SHORT).show()
            }
            email == "" -> {
                Toast.makeText(this,"please write email",Toast.LENGTH_SHORT).show()
            }
            password == "" -> {
                Toast.makeText(this,"please write password",Toast.LENGTH_SHORT).show()
            }
            else -> {

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    if (it.isSuccessful){

                        mAuth.currentUser?.let { result ->
                            firebaseUserID = result.uid
                            refUsers = FirebaseDatabase.getInstance().reference.child("Users")
                                .child(firebaseUserID)

                             val userHashMap = HashMap<String,Any>()
                            userHashMap["uid"] = firebaseUserID
                            userHashMap["username"] = userName
                            userHashMap["profile"] = ""
                            userHashMap["cover"] = ""
                            userHashMap["status"] = "offline"
                            userHashMap["search"] = userName.lowercase()
                            userHashMap["facebook"] = "https://m.facebook.com"
                            userHashMap["instagram"] = "https://m.instagram.com"
                            userHashMap["website"] = "https://www.google.com"

                            refUsers.updateChildren(userHashMap)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful){
                                        val intent = Intent(this,MainActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                        }

                    }else{
                        Toast.makeText(this,"Error Message: ${it.exception!!.message.toString()}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}









