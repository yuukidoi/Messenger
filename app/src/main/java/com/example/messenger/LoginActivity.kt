package com.example.messenger

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener{
            Log.d("loginActivity", "login")

            val email = email_edit.text.toString()
            val password = password_edit.text.toString()

            val lAuth = FirebaseAuth.getInstance()
            lAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener()
//                .add
        }


        finishText.setOnClickListener{
            finish()
        }

    }

}