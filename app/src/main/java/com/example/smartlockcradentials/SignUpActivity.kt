package com.example.smartlockcradentials

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credential
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val intent = intent
        val credential = intent.getParcelableExtra<Credential>("com.mycompany.myapp.SIGNIN_HINTS")

        // Pre-fill sign-up fields
        et_email.setText(credential!!.id)
    }
}
