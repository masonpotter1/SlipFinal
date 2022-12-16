package com.example.slipfinal

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaDrm.LogMessage
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginScreen : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance();

        val loginRegister = findViewById<Button>(R.id.ux_loginRegister) //gets R Id of login form
        val loginSignInButton = findViewById<Button>(R.id.ux_loginLogin)

        val loginEmail = findViewById<EditText>(R.id.ux_loginEmail)
        val loginPassword = findViewById<EditText>(R.id.ux_loginPassword)
        val loginForgotPassword = findViewById<Button>(R.id.ux_loginForgotPassword) // NOT IMPLEMENTED
        val loginProgressBar = findViewById<ProgressBar>(R.id.ux_loginProgressBar)

        loginRegister.setOnClickListener {
            val intent = Intent(this,RegisterUser::class.java)
            startActivity(intent)
        }
        loginSignInButton.setOnClickListener {
            LoginUser(loginEmail,loginPassword,loginProgressBar)
        }
    }
    @SuppressLint("NotConstructor") // explicitly not constructor for Kotlin
    private fun LoginUser(user:EditText, password:EditText,Bar:ProgressBar){
        val email = user.text.toString().trim()
        val pw = password.text.toString().trim()

        if (email.isEmpty()){
            user.setError("Please Provide Username")
            user.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) // email provided is not valid
        {
            user.setError("Invalid Email Address")
            user.requestFocus()
            return
        }
        if (pw.isEmpty()){
            password.setError("Please Provide Password")
            password.requestFocus()
            return
        }
        if(pw.length < 6){
            password.setError("Password Minimum 6 Characters")
            password.requestFocus()
            return
        }
        Bar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email,pw).addOnCompleteListener { task ->
            if(task.isSuccessful)
            {
                val user: FirebaseUser? = mAuth.currentUser
                if(user?.isEmailVerified == true) // user account is verified
                {
                    val intent = Intent(this,UserHome::class.java)
                    startActivity(intent)
                }
                else
                {
                    user?.sendEmailVerification()
                    Toast.makeText(this, "Please Verify Email", Toast.LENGTH_LONG).show()
                }
            }
            else{ Toast.makeText(this, "Failed Login", Toast.LENGTH_LONG).show()}
        }
        Bar.visibility = View.GONE
    }
}