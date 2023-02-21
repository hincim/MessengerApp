package com.hakaninc.messengerapp.fragments

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.hakaninc.messengerapp.databinding.FragmentSettingsBinding
import com.hakaninc.messengerapp.model.Users
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var fragmentSettingsbinding: FragmentSettingsBinding? = null
    var usersReference: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    var selected: Uri? = null
    private var storage: StorageReference? = null
    private var coverChecker : String ?= ""


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
        val binding =
            FragmentSettingsBinding.inflate(LayoutInflater.from(context), container, false)
        fragmentSettingsbinding = binding



        storage = FirebaseStorage.getInstance().reference.child("User Images")
        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersReference = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser!!.uid)

        usersReference!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)

                    if (context != null) {
                        binding.usernameSettings.text = user!!.username
                        //  Picasso.get().load(user.profile).into(binding.profileImageSettings)
                        //  Picasso.get().load(user.cover).into(binding.coverImageSettings)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.profileImageSettings.setOnClickListener {
            pickImageProfile()
        }

        binding.coverImageSettings.setOnClickListener {
            coverChecker = "cover"
            pickImageCover()
        }

        return binding.root
    }

    private fun pickImageCover() {

        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 2)
            // bize sonuç verecek bir activity başlat.
        }
    }


    private fun pickImageProfile() {

        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                3
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 4)
            // bize sonuç verecek bir activity başlat.
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 2)
                // bize sonuç verecek bir activity başlat.
            }
        }else if (requestCode == 3){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 4)
                // bize sonuç verecek bir activity başlat.
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selected = data.data
            Toast.makeText(context,"Uploading",Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selected)
                fragmentSettingsbinding!!.coverImageSettings.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }else {
            selected = data?.data
            Toast.makeText(context,"Uploading",Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selected)
                fragmentSettingsbinding!!.profileImageSettings.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait...")
        progressBar.show()
        println(selected)
        println("errorrrrr")

        if (selected != null){
            val fileRef = storage?.child(System.currentTimeMillis().toString() + ".jpg")
            var uploadTask : StorageTask<*>
            uploadTask = fileRef!!.putFile(selected!!)

            uploadTask.continueWithTask((Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if (!task.isSuccessful){

                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl

            })).addOnCompleteListener { task ->
                if (task.isSuccessful){

                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "cover"){

                        val mapCoverImg = HashMap<String,Any>()
                        mapCoverImg["cover"] = url
                        usersReference!!.setValue(mapCoverImg)
                        coverChecker = ""

                    }else{

                        val mapProfileImg = HashMap<String,Any>()
                        mapProfileImg["profile"] = url
                        usersReference!!.setValue(mapProfileImg)
                        coverChecker = ""

                    }
                    progressBar.dismiss()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        fragmentSettingsbinding = null
    }
}