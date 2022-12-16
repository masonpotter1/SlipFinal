package com.example.slipfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.slipfinal.R
import com.example.slipfinal.databinding.ActivityContactDetailsBinding
import com.example.slipfinal.databinding.ActivityContactScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var database: DatabaseReference // database ref
private lateinit var mAuth: FirebaseAuth;
private  lateinit var user: FirebaseUser // logged in user
private  lateinit var userID: String // logged in user id

class ContactDetails : AppCompatActivity() {
    private lateinit var binding: ActivityContactDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.currentUser!!
        database = FirebaseDatabase.getInstance().getReference("UserJava") // sets DB reference
        userID = user.uid // current user ID
        binding = ActivityContactDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contactID = intent.getIntExtra(CONTACT_ID_EXTRA,-1)
        val contact = contactFromID(contactID)
        if(contact !=null ){
            binding.uxCardName.text = contact.Name.toString()
            binding.uxCardUniqueKey.text = contact.UserID.toString()
            binding.uxCardFacebook.text =contact.Facebook.toString()
            binding.uxCardInsta.text = contact.Instagram.toString()
            binding.uxCardSnap.text = contact.SnapChat.toString()
        }
    }

    private fun contactFromID(contactID: Int): Contact? {
        for (contact in ContactList){
            if (contact.id == contactID){return contact}
        }
        return null

    }
}