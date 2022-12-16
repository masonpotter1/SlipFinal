package com.example.slipfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserHome : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    // private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance()

        val user: FirebaseUser? = mAuth.currentUser

        val myDB: DatabaseReference = database.getReference("UserJava") // database
        val userID: String = user!!.uid //asserted not null

        val logoutButton = findViewById<Button>(R.id.ux_homeLogOut)
        val profileButton = findViewById<Button>(R.id.ux_homeProfile)
        val connectButton = findViewById<Button>(R.id.ux_homeConnect)
        val contactsButton = findViewById<Button>(R.id.ux_homeContacts)

        val Name = findViewById<TextView>(R.id.ux_homeName)
        val Phone = findViewById<TextView>(R.id.ux_homePhoneNumber)

        myDB.child(userID).get().addOnSuccessListener {
            if (it.exists()) {
                val name = it.child("Username").value.toString()
                val phone = it.child("PhoneNumber").value.toString()
                Name.text = name
                Phone.text = phone
            }
        }


        profileButton.setOnClickListener {
            val intent = Intent(this,UserProfile::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this,LoginScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        connectButton.setOnClickListener {
            val intent = Intent(this,ConnectScreen::class.java)
            startActivity(intent)
        }
        contactsButton.setOnClickListener {
            val intent = Intent(this,ContactScreen::class.java)
            startActivity(intent)
        }

    }
}