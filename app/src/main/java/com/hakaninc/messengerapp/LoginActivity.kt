package com.hakaninc.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.hakaninc.messengerapp.databinding.ActivityLoginBinding
import com.hakaninc.messengerapp.databinding.ActivityRegisterBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarLogin)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbarLogin.setNavigationOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        binding.loginBtn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {

        val email: String = binding.emailLogin.text.toString()
        val password: String = binding.passwordLogin.text.toString()


        when {
            email == "" -> {
                Toast.makeText(this, "please write email", Toast.LENGTH_SHORT).show()
            }
            password == "" -> {
                Toast.makeText(this, "please write password", Toast.LENGTH_SHORT).show()
            }
            else -> {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {

                    if (it.isSuccessful) {

                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(
                            this,
                            "Error Message: ${it.exception!!.message.toString()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}