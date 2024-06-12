package com.kinisi.trailtracker.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kinisi.trailtracker.R
import kotlinx.android.synthetic.main.activity_update_profile.*
import java.util.*
import com.google.firebase.storage.*
import android.widget.RadioGroup.*
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.squareup.okhttp.ResponseBody

import com.kinisi.trailtracker.MainActivity

import com.squareup.okhttp.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File


class UpdateProfile : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    val storage = Firebase.storage
    var storageRef = storage.reference

    lateinit var imageView: ImageView
    private val pickImage = 100
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.kinisi.trailtracker.R.layout.activity_update_profile)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser


        //Set maximum date to current date
        dob.setMaxDate(Date().time)


        btnUpdateProfile.setOnClickListener {
            //Collect info
            val name = Name.text.toString()
            val DOB = Timestamp(Date(dob.year - 1900, dob.month, dob.dayOfMonth))
            val height = height.text.toString()
            val weight = weight.text.toString()

            val selectedOption: Int = sex_RadioGroup!!.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(selectedOption)
            var sex=""
            //sets default sex to noSex
            if(sex_RadioGroup.getCheckedRadioButtonId() == -1){
                sex= noSex.text.toString()
            }else {
                sex = radioButton.text.toString()
            }

            if(name==""||height==""||weight==""){
                Toast.makeText(this, "Error: Please enter all values", Toast.LENGTH_SHORT).show()
            }
            else{
                //Set info to user
                val userInfo = hashMapOf(
                    "name" to name,
                    "height" to height,
                    "weight" to weight,
                    "dob" to DOB,
                    "sex" to sex
                )
                auth.currentUser?.let { it1 ->
                    db.collection("users").document(user!!.uid)
                        .set(userInfo,SetOptions.merge())
                        .addOnSuccessListener {
                            Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_SHORT).show()
                            Log.d(
                                "docSnippets",
                                "DocumentSnapshot successfully written!"
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.w(
                                "docSnippets",
                                "Error writing document",
                                e
                            )
                        }
                }
            }

        }


        imageView = findViewById(R.id.imageView)
        btnUpdateImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)

//            imageView.isDrawingCacheEnabled = true
//            imageView.buildDrawingCache()
//            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
//            val baos = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//            val data = baos.toByteArray()
//
//            var uploadTask = storageRef.putBytes(data)
//            uploadTask.addOnFailureListener {
//                // Handle unsuccessful uploads
//            }.addOnSuccessListener { taskSnapshot ->
//                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
//                // ...
//            }
        }


    }

}