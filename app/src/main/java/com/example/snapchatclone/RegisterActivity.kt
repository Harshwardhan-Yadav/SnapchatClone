package com.example.snapchatclone

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    fun register(view: View){
        var username = findViewById<EditText>(R.id.registerEmail).getText().toString().trim()
        var password = findViewById<EditText>(R.id.registerPassword).getText().toString().trim()
        var confirmPassword = findViewById<EditText>(R.id.registerConfirmPassword).getText().toString().trim()
        if(username.length==0){
            findViewById<EditText>(R.id.registerEmail).setError("Email mustn't be empty")
            findViewById<EditText>(R.id.registerEmail).requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            findViewById<EditText>(R.id.registerEmail).setError("Email isn't formated properly")
            findViewById<EditText>(R.id.registerEmail).requestFocus()
            return
        }
        if(password.length<=7){
            findViewById<EditText>(R.id.registerPassword).setError("Password mustn't be less than 8 characters")
            findViewById<EditText>(R.id.registerPassword).requestFocus()
            return
        }
        if(password.compareTo(confirmPassword)!=0){
            findViewById<EditText>(R.id.registerConfirmPassword).setError("Passwords do not match")
            findViewById<EditText>(R.id.registerConfirmPassword).requestFocus()
            return
        }
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    // Write a message to the database
                    var ob = ManageLists()
                    ob.addUser(auth.uid,username)
                    var intent = Intent(this,HomeActiity::class.java).apply {  }
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed - "+task.exception.toString().split(": ")[1],
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun login(view: View){
        var intent = Intent(this,MainActivity::class.java).apply {  }
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth= Firebase.auth
    }
}