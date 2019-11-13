package com.example.smartlockcradentials

import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_sign_up.*


class SigninActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        var credentail_client: CredentialsClient = Credentials.getClient(this)

//        val intent = intent
//        val credential =
//            intent.getParcelableExtra<Credential>("com.example.smartlockcradentials.SIGNIN_HINTS")
//
//        et_email.setText(credential.id)


        btn_submit.setOnClickListener {
            val email = et_email.text.toString().trim()
            val password = et_password.text.toString().trim()

            val credential = Credential.Builder(email)
                .setPassword(password)
                .build()

            credentail_client.save(credential).addOnCompleteListener(object : OnCanceledListener,
                OnCompleteListener<Void> {
                override fun onCanceled() {

                }

                override fun onComplete(task: Task<Void>) {
                    if (task.isSuccessful) {
                        Log.d("loginActivity", "SAVE: OK")
                        Toast.makeText(
                            this@SigninActivity,
                            "Credentials saved",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val e = task.exception
                    if (e is ResolvableApiException) {
                        // Try to resolve the save request. This will prompt the user if
                        // the credential is new.
                        val rae = e as ResolvableApiException?
                        try {
                            rae!!.startResolutionForResult(this@SigninActivity, 103)
                        } catch (e: IntentSender.SendIntentException) {
                            // Could not resolve the request
                            Log.e("loginActivity", "Failed to send resolution.", e)
                            Toast.makeText(
                                this@SigninActivity,
                                "Save failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        // Request has no resolution
                        Toast.makeText(this@SigninActivity, "Save failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            })

        }
    }
}
