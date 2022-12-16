package com.example.slipfinal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class RegisterUser : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance()

        val registerScreen = findViewById<TextView>(R.id.ux_registerSlip) //gets R Ids of register form vars
        val registerButton = findViewById<Button>(R.id.ux_registerButton)

        val registerName = findViewById<EditText>(R.id.ux_registerName)
        val registerPhoneNumber = findViewById<EditText>(R.id.ux_registerPhoneNumber)
        val registerEmail = findViewById<EditText>(R.id.ux_registerEmail)
        val registerPassword = findViewById<EditText>(R.id.ux_registerPassword)
        val registerProgressBar = findViewById<ProgressBar>(R.id.ux_progressBar)

        registerButton.setOnClickListener { // sends users back to login screen
            registerUser(registerName,registerPhoneNumber,registerEmail,registerPassword,registerProgressBar)
        }
        registerScreen.setOnClickListener { // sends users back to login screen
            val intent = Intent(this,LoginScreen::class.java)
            startActivity(intent)
        }

    }

    @SuppressLint("NotConstructor") // explicitly not constructor for Kotlin
    private fun registerUser(user:EditText, phone:EditText, emailAddress:EditText, password:EditText, Bar:ProgressBar)
    {
        // grabs input fields
        val username = user.text.toString().trim()
        val phoneNumber = phone.text.toString().trim()
        val email = emailAddress.text.toString().trim()
        val pw = password.text.toString().trim()

        // check for valid values
        if (username.isEmpty()){
            user.error=("Please Provide Username")
            user.requestFocus()
            return
        }
        if (phoneNumber.isEmpty()){
            phone.error=("Please Provide Phone Number")
            phone.requestFocus()
            return
        }
        if (email.isEmpty()){
            emailAddress.error=("Please Provide Email")
            emailAddress.requestFocus()
            return
        }
        if (pw.isEmpty()){
            password.error=("Please Provide Password")
            password.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) // email provided is not valid
        {
            emailAddress.error= "Invalid Email Address"
            emailAddress.requestFocus()
            return
        }
        if(pw.length < 6){
            password.error = "Password Minimum 6 Characters"
            password.requestFocus()
            return
        }
        Bar.visibility = View.VISIBLE
        // this actually authenticates, and adds the user to the authenticated user list
        mAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
            if(task.isSuccessful){ // successful auth
                val userJava = UserJava(username, email,phoneNumber) // creates user
                FirebaseAuth.getInstance().currentUser?.let { // let required. Else unable to get ID in case of null
                    database.getReference("UserJava").child( // puts us in the UserJava part of the DB
                        it.uid).setValue(userJava).addOnCompleteListener { task -> // sets the value of the auth token to a new user - listener for error check
                        if (task.isSuccessful) {
                            FirebaseAuth.getInstance().currentUser?.sendEmailVerification();
                            Toast.makeText(this, "Please Verify Email", Toast.LENGTH_LONG).show()
                            Bar.visibility = View.GONE
                            finish()

                        }
                        else
                        {
                            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                            Bar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


}