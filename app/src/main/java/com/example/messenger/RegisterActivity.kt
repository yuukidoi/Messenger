package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

   // private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //auth = FirebaseAuth.getInstance()


        val button = findViewById<Button>(R.id.register_button)
        button.setOnClickListener{
          performRegister()
        }

        val selectPhotoButton = findViewById<Button>(R.id.select_photo_button)
        selectPhotoButton.setOnClickListener{
            Log.d("Main", "Try to show photo")

            val intent= Intent(Intent.ACTION_PICK)
            intent.type = "image/:"
            startActivityForResult(intent, 0)
        }


        val account = findViewById<TextView>(R.id.already_account_text)
        account.setOnClickListener{
            Log.d("MainActivity", "text")

            //LoginActivityへ
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    public override fun onStart() {
        super.onStart()
        Log.d("main", "onStart")
        //check if user is signed in
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //proceed and check what image is
            Log.d("Register", "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)

            select_photo_image_register.setImageBitmap(bitmap)

            //上野に乗ってるボタンの透明度を上げて隠す＝画像が見える様にする
            select_photo_button.alpha = 0f
//
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            select_photo_button.setBackgroundDrawable(bitmapDrawable)

        }
    }

    private fun performRegister(){


        val email = email_edit.text.toString()
        val password = password_edit.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter",Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MainActivity", "email ${email} ")
        Log.d("MainActivity", "password ${password}")

        //FireBaseAuth
        var auth = FirebaseAuth.getInstance()
        Log.d("MainActivity", "auth")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->

               // if(!it.isSuccessful) return@addOnCompleteListener
                Log.d("Main", "create user successful uid: ${task.result?.user?.uid}")
                uploadImageToStorage()
            }
            .addOnFailureListener{
                Log.d("main", "failed to create user; ${it.message}")
                Toast.makeText(this, "failed to create user; ${it.message}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun uploadImageToStorage(){

        if(selectedPhotoUri == null) return

       val fStorage = FirebaseStorage.getInstance()
        val filename =UUID.randomUUID().toString()
        val ref = fStorage.getReference("/images$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Register", "Successfully upload image ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("Register", " File location $it") //itはUriの保存場所＝Uri

                    saveUserToFireBaseDatabase(it.toString())
                }
                    .addOnFailureListener{
                        //do some login here
                    }
            }
    }

    private fun saveUserToFireBaseDatabase(profileImageUri: String){

        val uid =  FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")

        val user = User(uid, user_name_edit.text.toString(), profileImageUri)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Register", "Finally we saved")

                val intent = Intent(this,LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }

}
@Parcelize
class User(val uid: String, val username: String, val profileImageUri: String): Parcelable{
    //引数コンストラクタの指定　
    constructor(): this("","","")
}
