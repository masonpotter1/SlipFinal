package com.example.slipfinal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.slipfinal.databinding.ActivityEditSocialsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var database: DatabaseReference // database ref
private lateinit var mAuth: FirebaseAuth;
private lateinit var binding: ActivityEditSocialsBinding
private  lateinit var user: FirebaseUser // logged in user
private  lateinit var userID: String // logged in user id

class EditSocials : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_socials)

        binding = ActivityEditSocialsBinding.inflate(layoutInflater) // sets binding location
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.currentUser!!
        database = FirebaseDatabase.getInstance().getReference("UserJava") // sets DB reference
        userID = user.uid // current user ID


        val info_Snap = findViewById<EditText>(R.id.uxEdits_Snap)
        val info_Facebook = findViewById<View>(R.id.uxEdits_Facebook)
        val info_Insta = findViewById<View>(R.id.uxEdits_Insta)
        val saveButton = findViewById<Button>(R.id.uxEdits_SaveButton)

        val backButton = findViewById<Button>(R.id.uxEdits_BackButton)




        binding.uxEditsSaveButton.setOnClickListener {
            val snap = binding.uxEditsSnap.text.toString().trim()
            val instagram = binding.uxEditsInsta.text.toString().trim()
            val facebook = binding.uxEditsFacebook.text.toString().trim()
            database.child(userID).child("Social Media").child("Instagram").setValue(instagram)
            database.child(userID).child("Social Media").child("Facebook").setValue(facebook)
            database.child(userID).child("Social Media").child("Snapchat").setValue(snap).addOnCompleteListener {
                if (it.isSuccessful)
                {
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                    //uploadProfilePic()
                }
                else{
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()}
            }
        }

                // user back button - returns to profile
        backButton.setOnClickListener {
            val intent = Intent(this,UserProfile::class.java)
            startActivity(intent)
        }




    }
}