package com.hakaninc.messengerapp.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hakaninc.messengerapp.databinding.FragmentSettingsBinding
import com.hakaninc.messengerapp.model.Users
import com.squareup.picasso.Picasso

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

    private var fragmentSettingsbinding: FragmentSettingsBinding?= null
    var usersReference : DatabaseReference ?= null
    var firebaseUser : FirebaseUser ?= null
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var selectedPicture: Uri? = null
    private var storageRef : StorageReference ?= null




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
        val binding = FragmentSettingsBinding.inflate(LayoutInflater.from(context),container,false)
        fragmentSettingsbinding = binding


        registerLauncherProfile()

        storageRef = FirebaseStorage.getInstance().reference.child("User Images")
        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersReference = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser!!.uid)

        usersReference!!.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    val user : Users? = snapshot.getValue(Users::class.java)

                    if (context != null){
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

        binding.coverImageSettings .setOnClickListener {
            pickImageCover()
        }

        return binding.root
    }

    private fun pickImageCover() {

            if (context?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Snackbar.make(fragmentSettingsbinding!!.coverImageSettings, "İzin tekrar istensin mi?", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Evet", View.OnClickListener {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        })
                        .show()

                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            } else {

                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }


    private fun pickImageProfile() {

        if (context?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Snackbar.make(fragmentSettingsbinding!!.profileImageSettings, "İzin tekrar istensin mi?", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Evet", View.OnClickListener {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    })
                    .show()

            } else {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        } else {

            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
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

    private fun registerLauncherProfile(){
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { sonuc ->

                if (sonuc.resultCode == AppCompatActivity.RESULT_OK) {
                    // kullanıcı galeriye gidip işlemi yaptıysa
                    val intentFromResult = sonuc.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        selectedPicture?.let {
                            fragmentSettingsbinding!!.profileImageSettings.setImageURI(selectedPicture)
                        }
                    }
                }else if (sonuc.resultCode == AppCompatActivity.RESULT_OK){
                    val intentFromResult = sonuc.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        selectedPicture?.let {
                            fragmentSettingsbinding!!.coverImageSettings.setImageURI(selectedPicture)
                        }
                    }
                }
            }


        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { sonuc ->

                if (sonuc) {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    activityResultLauncher.launch(intentToGallery)

                } else {
                    Toast.makeText(requireContext(), "İzine ihtiyacım var!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun registerLauncherCover(){
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { sonuc ->

                if (sonuc.resultCode == AppCompatActivity.RESULT_OK) {
                    val intentFromResult = sonuc.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        selectedPicture?.let {
                            fragmentSettingsbinding!!.coverImageSettings.setImageURI(selectedPicture)
                        }
                    }
                }
            }


        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { sonuc ->

                if (sonuc) {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    activityResultLauncher.launch(intentToGallery)

                } else {
                    Toast.makeText(requireContext(), "İzine ihtiyacım var!", Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        fragmentSettingsbinding = null
    }
}