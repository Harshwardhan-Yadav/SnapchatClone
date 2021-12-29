package com.example.snapchatclone

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var loginButton: Button
    lateinit var loginUsername: EditText
    lateinit var loginPassword: EditText
    private lateinit var auth: FirebaseAuth

    fun login(view: View){
        var username=loginUsername.getText().toString().trim()
        var password=loginPassword.getText().toString().trim()
        if(username.length==0){
            loginUsername.setError("Email mustn't be empty")
            loginUsername.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            loginUsername.setError("Email isn't formated properly")
            loginUsername.requestFocus()
            return
        }
        if(password.length<=7){
            loginPassword.setError("Password mustn't be less than 8 characters")
            loginPassword.requestFocus()
            return
        }
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    var intent = Intent(this,HomeActiity::class.java).apply {  }
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun register(view: View){
        //goto Register Activity.
        var intent = Intent(this,RegisterActivity::class.java).apply {  }
        startActivity(intent)
        finish()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            // Go to Home Activity.
            var intent = Intent(this,HomeActiity::class.java).apply {  }
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginButton = findViewById(R.id.loginButton)
        loginUsername = findViewById(R.id.loginEmail)
        loginPassword = findViewById(R.id.loginPassword)
        auth = Firebase.auth
    }
}