package com.example.slipfinal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
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


private lateinit var binding: ActivityContactScreenBinding
class ContactScreen : AppCompatActivity(),ContactClick {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_screen)
        binding = ActivityContactScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.currentUser!!
        database = FirebaseDatabase.getInstance().getReference("UserJava") // sets DB reference
        userID = user.uid // current user ID

        populateContacts()

        val mainActivity = this
        binding.uxContactsRecycler.apply {
            layoutManager = GridLayoutManager(applicationContext,3)
            adapter = CardAdapter(ContactList,mainActivity)
        }

    }

    private fun populateContacts()
    {
        database.child(userID).child("Contact List").get().addOnCompleteListener {
            if(it.isSuccessful){
                val userInfo: DataSnapshot = it.result // make it a snapshot
                for (DataSnapshot in userInfo.children){
                    var tempID = userInfo.key.toString()
                    var tempName = DataSnapshot.child("Contact Name").value.toString()
                    var con = Contact(tempID,tempName)
                    ContactList.add(con)
                    con.SnapChat = DataSnapshot.child("Social Accounts").child("Snapchat").value.toString()
                    con.Instagram = DataSnapshot.child("Social Accounts").child("Instagram").value.toString()
                    con.Facebook = DataSnapshot.child("Social Accounts").child("Facebook").value.toString()


                }
            }
        }

    }

    override fun onClick(contact: Contact) {
        val intent = Intent(applicationContext,ContactDetails::class.java)
        intent.putExtra(CONTACT_ID_EXTRA,contact.id)
        startActivity(intent)
    }
}