package com.example.snapchatclone

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class SelectUser : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user)
        auth=Firebase.auth
        var database = Firebase.database
        var ref = database.getReference("users")
        var list = ArrayList<String>()
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<ArrayList<String>>()
                if (value != null) {
                    var count=0;
                    for(s in value){
                        if(count==0) {count++;continue;}
                        list.add(s.split("~")[1])
                    }
                }
                var arrayAdapter:ArrayAdapter<String> = ArrayAdapter<String>(applicationContext,android.R.layout.simple_list_item_1,list)
                var listView=findViewById<ListView>(R.id.selectUserListView)
                listView.adapter=arrayAdapter
                listView.setOnItemClickListener { parent, view, position, id ->
                    buidAndSend(value?.get(position+1),position+1)
                }
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun buidAndSend(userToSend: String?, index: Int) {
        var userToSendUid = userToSend?.split("~")?.get(0)
        var database = Firebase.database
        var ref = database.getReference(userToSendUid+"")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var uuid: String = UUID.randomUUID().toString().replace("-","")
                var snap: Bitmap? = intent.getParcelableExtra<Bitmap>("snap")
                // Upload snap under file name
                var storage = Firebase.storage
                var storageRef = storage.reference
                var fileRef = storageRef.child("$userToSendUid/f$uuid.png")
                val baos = ByteArrayOutputStream()
                snap?.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val data = baos.toByteArray()
                var uploadTask = fileRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    Toast.makeText(applicationContext,"Error while uploading image, Please try again",Toast.LENGTH_SHORT).show()
                    Thread.sleep(2000)
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                    Toast.makeText(applicationContext,"Snap sent successfully",Toast.LENGTH_SHORT).show()
                    var file = snapshot.child("file").getValue<ArrayList<String>>()
                    var sender = snapshot.child("sender").getValue<ArrayList<String>>()
                    var uid = snapshot.child("uid").getValue<ArrayList<String>>()
                    sender?.add(auth.currentUser?.email.toString())
                    uid?.add(auth.currentUser?.uid.toString())
                    ref.child("sender").setValue(sender)
                    ref.child("uid").setValue(uid)
                    var note: String = intent.getStringExtra("note").toString()
                    file?.add("f$uuid$note")//file name is 33 characters
                    ref.child("file").setValue(file)
                    Thread.sleep(2000)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"Error while uploading image, Please try again",Toast.LENGTH_SHORT).show()
            }
        })
    }
}