package com.example.snapchatclone

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class ImageSelectActivity : AppCompatActivity() {

    lateinit var note: String
    lateinit var snap: Bitmap
    val REQUEST_IMAGE_READ = 2

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun upload(view: View){
        startActivityForResult(Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_IMAGE_READ)
    }

    val REQUEST_IMAGE_CAPTURE = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            snap = data?.extras?.get("data") as Bitmap
            findViewById<ImageView>(R.id.snap).setImageBitmap(snap)
        }
        if(requestCode == REQUEST_IMAGE_READ && resultCode == RESULT_OK){
            snap = MediaStore.Images.Media.getBitmap(this.contentResolver,data?.data)
            findViewById<ImageView>(R.id.snap).setImageBitmap(snap)
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }
    }

    fun takeSnap(view: View){
        dispatchTakePictureIntent()
    }

    fun sendSnap(view: View){
        if(findViewById<ImageView>(R.id.snap).drawable==null){
            Toast.makeText(this,"No Snap Uploaded",Toast.LENGTH_SHORT).show()
            return
        }
        note=findViewById<EditText>(R.id.note).text.toString()
        var intent = Intent(this,SelectUser::class.java).apply{
            putExtra("snap",snap)
            putExtra("note",note)
        }
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_select)
    }
}