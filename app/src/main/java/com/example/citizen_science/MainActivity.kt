// MADE BY:
// Christopher Fiala
// Ian Vinkler
// Amirmohammad Mirgeloybayat

package com.example.citizen_science

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    // Declare an instance of FirebaseAuth
    // private var mAuth: FirebaseAuth? = null
    private lateinit var auth: FirebaseAuth


    // declare validator object
    private var validator: Validators? = null

    // buttons
    private lateinit var buttonLogin: Button
    private lateinit var buttonCreate: Button

    // textViews
    // login
    private lateinit var textLoginEmail: TextView
    private lateinit var textLoginPassword: TextView
    // create account
    private lateinit var textCreateEmail: TextView
    private lateinit var textCreatePassword: TextView
    private lateinit var textCreatePasswordConfirm: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // initializes the buttons and sets onClickListeners based on their functionality
        initializeButtons()
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            Log.i("USERINFO",currentUser!!.uid)

            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }
    }

    private fun initializeButtons() {
        // buttons
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonCreate = findViewById(R.id.buttonCreate)

        // textViews
        // login
        textLoginEmail = findViewById(R.id.editTextLoginEmail)
        textLoginPassword = findViewById(R.id.editTextLoginPassword)

        // create account
        textCreateEmail = findViewById(R.id.editTextCreateEmail)
        textCreatePassword = findViewById(R.id.editTextCreatePassword)
        textCreatePasswordConfirm = findViewById(R.id.editTextCreateConfirmPassword)

        buttonLogin.setOnClickListener {
            loginUserAccount()
        }

        buttonCreate.setOnClickListener {
            registerNewUser()
        }
    }


    private fun loginUserAccount() {
        val email: String = textLoginEmail.text.toString()
        val password: String = textLoginPassword.text.toString()

        validator = Validators()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    Log.i("USERINFO",user!!.uid)
                    startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    // startActivity(Intent(this@MainActivity, TestActivity::class.java))
                }
            }
    }

    private fun registerNewUser() {
        val email: String = textCreateEmail.text.toString()
        val password: String = textCreatePassword.text.toString()
        val passwordConfirm: String = textCreatePasswordConfirm.text.toString()

        validator = Validators()

        // email validation based on class Validators
        if (!validator!!.validEmail(email)) {
            Toast.makeText(applicationContext, "Please enter valid email...", Toast.LENGTH_LONG).show()
            return
        }

        if (!validator!!.validPassword(password)) {
            Toast.makeText(applicationContext, "Password must contain 8 characters with one letter and one number!", Toast.LENGTH_LONG).show()
            return
        }

        if (password != passwordConfirm) {
            Toast.makeText(applicationContext, "Password and its confirmation don't match!", Toast.LENGTH_LONG).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    // startActivity(Intent(this@MainActivity, TestActivity::class.java))
                }
            }

    }

    companion object {
        const val TAG = "MainActivity"
    }
}