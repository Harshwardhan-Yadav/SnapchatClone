package com.example.snapchatclone

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DisplaySnap : AppCompatActivity() {

    var index=1
    lateinit var address: String

    override fun onBackPressed() {
        super.onBackPressed()
        var auth=Firebase.auth
        var database = Firebase.database
        var ref = database.getReference(auth.currentUser?.uid.toString())
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var fileRef = Firebase.storage.getReference(auth.currentUser?.uid.toString()).child("$address.png")
                fileRef.delete().addOnSuccessListener {
                    var file = snapshot.child("file").getValue<ArrayList<String>>()
                    var sender = snapshot.child("sender").getValue<ArrayList<String>>()
                    var uid = snapshot.child("uid").getValue<ArrayList<String>>()
                    file?.removeAt(index)
                    sender?.removeAt(index)
                    uid?.removeAt(index)
                    ref.child("file").setValue(file)
                    ref.child("sender").setValue(sender)
                    ref.child("uid").setValue(uid)
                    finish()
                }.addOnFailureListener{
                    println("File cannot be deleted at storage")
                    ManageLists.success=0
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_snap)
        var file = intent.getStringExtra("file")
        index = intent.getIntExtra("index",1)
        var note = file?.substring(33)
        findViewById<TextView>(R.id.noteDisplay).text = note
        address = file?.substring(0,33).toString()
        var ref=Firebase.storage.reference
        var auth = Firebase.auth
        var uid=auth.currentUser?.uid
        var fileRef = ref.child("$uid/$address.png")
        val imageView = findViewById<ImageView>(R.id.snapDisplay)
        var snap: Bitmap
        fileRef.getBytes(1024*1024).addOnSuccessListener {
            snap = BitmapFactory.decodeByteArray(it,0,it.size)
            imageView.setImageBitmap(snap)
        }.addOnFailureListener{
            Toast.makeText(this,it.message+". Try Again!",Toast.LENGTH_LONG).show()
            Thread.sleep(3000)
            ManageLists.success=0
            finish()
        }
    }
}