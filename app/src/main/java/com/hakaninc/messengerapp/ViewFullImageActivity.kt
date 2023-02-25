package com.hakaninc.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hakaninc.messengerapp.databinding.ActivityViewFullImageBinding
import com.squareup.picasso.Picasso

class ViewFullImageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityViewFullImageBinding
    private var imaageUrl : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFullImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imaageUrl = intent.getStringExtra("url")

        Picasso.get().load(imaageUrl).into(binding.imageViewer)

    }
}