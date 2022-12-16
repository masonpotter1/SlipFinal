package com.example.slipfinal

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.slipfinal.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var database: DatabaseReference // database ref
private lateinit var mAuth: FirebaseAuth;
private lateinit var binding: ActivityUserProfileBinding
private  lateinit var user: FirebaseUser // loged in user
private  lateinit var userID: String // loged in user id

private  lateinit var Phones: String
//private lateinit var  storageRef: StorageReference // firebase storage
//private  lateinit var ImgUri: URI

class UserProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        binding = ActivityUserProfileBinding.inflate(layoutInflater) // sets binding location
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.currentUser!!
        database = FirebaseDatabase.getInstance().getReference("UserJava") // sets DB reference
        userID = user.uid // current user ID

        val homeButton = findViewById<Button>(R.id.ux_userProfileHome)
        val editButton = findViewById<TextView>(R.id.ux_userProfileEditButton)
        val profPhone = findViewById<TextView>(R.id.ux_userProfilePhone)


        database.child(userID).get().addOnSuccessListener{
            if (it.exists())
            {
                val name = it.child("Username").value
                val phone = it.child("PhoneNumber").value

                val snap = it.child("Social Media").child("Snapchat").value
                val insta = it.child("Social Media").child("Instagram").value
                val facebook = it.child("Social Media").child("Facebook").value

                binding.uxUserProfilePhone.text= phone.toString()
                binding.uxUserProfileName.text= name.toString()
                binding.uxUserProfileSnapChat.text= "Snapchat: ${snap.toString()}"
                binding.uxUserProfileInstagram.text= "Instagram: ${insta.toString()}"
                binding.uxUserProfileFacebook.text= "Facebook: ${facebook.toString()}"


            }
            else{Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()}
        }.addOnFailureListener {
            Toast.makeText(this, "Failed - Code 2", Toast.LENGTH_LONG).show()
        }.toString()



        //sends user to home screen
        homeButton.setOnClickListener {
            val intent = Intent(this,UserHome::class.java)
            startActivity(intent)
        }


        editButton.setOnClickListener {
            val intent = Intent(this,EditSocials::class.java)
            startActivity(intent)
        }



        /* what happens on save button click // edited to
        binding.uxUserProfileEditButton.setOnClickListener {
            val username = binding.uxUserProfileName.text.toString().trim()
            val phone = binding.uxUserProfilePhone.text.toString().trim()
            val snap = binding.uxUserProfileSnapChat.text.toString().trim()
            val instagram = binding.uxUserProfileInstagram.text.toString().trim()
            val facebook = binding.uxUserProfileFacebook.text.toString().trim()
            database.child(userID).child("Username").setValue(username)
            database.child(userID).child("PhoneNumber").setValue(phone)
            database.child(userID).child("Social Media").child("Instagram").setValue(instagram)
            database.child(userID).child("Social Media").child("Facebook").setValue(facebook)
            database.child(userID).child("Social Media").child("Snapchat").setValue(snap).addOnCompleteListener {
                if (it.isSuccessful)
                {
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                    //uploadProfilePic()
                }
                else{Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()}
            }
        }*/

    }

    private fun uploadProfilePic() {
        TODO("Not yet implemented")
    }
}