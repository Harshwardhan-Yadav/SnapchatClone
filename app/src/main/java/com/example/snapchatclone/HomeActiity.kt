package com.example.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class HomeActiity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater:MenuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun refresh(){
        fillList()
    }

    fun fillList(){
        var database=Firebase.database
        var ref = database.getReference(auth.currentUser?.uid.toString())
        var list: ArrayList<String> = ArrayList()
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var file = snapshot.child("file").getValue<ArrayList<String>>()
                var sender = snapshot.child("sender").getValue<ArrayList<String>>()
                if (sender != null) {
                    for(s in sender){
                        list.add(s)
                    }
                    list.removeAt(0)
                }
                var arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext,android.R.layout.simple_list_item_1,list)
                var listView=findViewById<ListView>(R.id.homeListView)
                listView.adapter=arrayAdapter
                listView.setOnItemClickListener { _, _, position, _ ->
                    var index = position+1
                    startActivity(Intent(applicationContext,DisplaySnap::class.java).apply{
                        putExtra("file",file?.get(index))
                        putExtra("index",index)
                    })
                    if(ManageLists.success==0){
                        ManageLists.success=1
                    }
                    else{
                    list.removeAt(index-1)
                    arrayAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.newsnap){
            startActivity(Intent(this,ImageSelectActivity::class.java).apply {  })
            Thread.sleep(3000)
            fillList()
        }else if(item.itemId==R.id.logout){
            Firebase.auth.signOut();
            startActivity(Intent(this,MainActivity::class.java).apply {  })
            finish()
        }
        else{
            fillList()
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_actiity)
        auth=Firebase.auth
        val user=auth.currentUser
        println(user?.uid+" "+user?.email)
        fillList()
    }
}