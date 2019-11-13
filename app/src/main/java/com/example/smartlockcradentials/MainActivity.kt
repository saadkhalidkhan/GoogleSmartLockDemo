package com.example.smartlockcradentials

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.*
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var credentail_client: CredentialsClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        btn_submit.setOnClickListener {
            val email = et_email.text.toString().trim()
            val password = et_password.text.toString().trim()

            val credential = Credential.Builder(email)
                .setPassword(password)
                .build()

            credentail_client!!.save(credential).addOnCompleteListener(object : OnCanceledListener,
                OnCompleteListener<Void> {
                override fun onCanceled() {

                }

                override fun onComplete(task: Task<Void>) {
                    if (task.isSuccessful) {
                        Log.d("loginActivity", "SAVE: OK")
                        Toast.makeText(
                            this@MainActivity,
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
                            rae!!.startResolutionForResult(this@MainActivity, 103)
                        } catch (e: IntentSender.SendIntentException) {
                            // Could not resolve the request
                            Log.e("loginActivity", "Failed to send resolution.", e)
                            Toast.makeText(
                                this@MainActivity,
                                "Save failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        // Request has no resolution
                        Toast.makeText(this@MainActivity, "Save failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            })

        }
    }

    private fun autoFillHint() {

    }

    private fun init() {
        credentail_client = Credentials.getClient(this)

        val credential_request = CredentialRequest.Builder()
            .setPasswordLoginSupported(true)
            .setAccountTypes("com.example.smartlockcradentials")
            .build()

        credentail_client!!.request(credential_request).addOnCompleteListener(
            OnCompleteListener<CredentialRequestResponse> { task ->
                if (task.isSuccessful) {
                    // See "Handle successful credential requests"
                    onCredentialRetrieved(task.result!!.credential)
                    return@OnCompleteListener
                }
                val e = task.exception
                if (e is ResolvableApiException) {
                    val rae = e as ResolvableApiException
                    resolveResult(rae, 101)
                } else if (e is ApiException) {
                    // The user must create an account or sign in manually.
                    Log.e("mainActivity", "Unsuccessful credential request.", e)

                    val ae = e as ApiException
                    val code = ae.statusCode
                }
            })

       /* val hintRequest = HintRequest.Builder()
            .setHintPickerConfig(
                CredentialPickerConfig.Builder()
                    .setShowCancelButton(true)
                    .build()
            )
            .setEmailAddressIdentifierSupported(true)
            .setAccountTypes("com.example.smartlockcradentials")
            .build()

        val intent = credentail_client!!.getHintPickerIntent(hintRequest)
        try {
            startIntentSenderForResult(intent.intentSender, 102, null, 0, 0, 0)
        } catch (e: IntentSender.SendIntentException) {
            Log.e("mainActivity", "Could not start hint picker Intent", e)
        }*/
    }

    private fun onCredentialRetrieved(credential: Credential) {
        val account_type = credential.accountType

        if (account_type == null) {
            et_email.setText(credential.id)
            et_password.setText(credential.id)
//            signInWithPassword(credential.id, credential.password)
        } else if (account_type == "com.example.smartlockcradentials") {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val signin_client = GoogleSignIn.getClient(this, gso)
            val task = signin_client.silentSignIn()
        }
    }

    private fun resolveResult(rae: ResolvableApiException, requestCode: Int) {
        try {
            rae.startResolutionForResult(this, requestCode)
        } catch (e: IntentSender.SendIntentException) {
            Log.e("mainActivity", "Failed to send resolution.", e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            if (resultCode === Activity.RESULT_OK) {
                val credential: Credential = data!!.getParcelableExtra(Credential.EXTRA_KEY)
                onCredentialRetrieved(credential)
            } else {
                Log.e("mainActivity", "Credential Read: NOT OK")
                Toast.makeText(this, "Credential Read Failed", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == 102) {
            if (resultCode === Activity.RESULT_OK) {
                val credential: Credential = data!!.getParcelableExtra(Credential.EXTRA_KEY)
                val intent: Intent
                // Check for the user ID in your user database.
                /*if (userDatabaseContains(credential.id)) {
                    intent = Intent(this, SigninActivity::class.java)
                } else {
                    intent = Intent(this, SignUpActivity::class.java)
                }*/
//                intent.putExtra("com.example.smartlockcradentials.SIGNIN_HINTS", credential)
//                startActivity(intent)
            } else {
                Log.e("mainActivity", "Hint Read: NOT OK")
                Toast.makeText(this, "Hint Read Failed", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == 103) {
            if (resultCode == RESULT_OK) {
                Log.d("mainActivity", "SAVE: OK")
                Toast.makeText(this, "Credentials saved", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("mainActivity", "SAVE: Canceled by user")
            }
        }
    }
}
