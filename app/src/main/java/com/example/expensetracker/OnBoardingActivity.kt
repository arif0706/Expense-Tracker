package com.example.expensetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.expensetracker.ui.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_on_boarding_activity.*

class OnBoardingActivity : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN=9001
    }

    private val googleSignInClient : GoogleSignInClient by lazy {

        GoogleSignIn.getClient(
            this,GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
            )
                .requestEmail()
                .requestIdToken(getString(R.string.google_sign_in_client_id))
                .build()
        )

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding_activity)

        google_sign_in.setOnClickListener{
            signInWithGoogle()
        }

    }
    private fun signInWithGoogle(){

        googleSignInClient.signOut().addOnCompleteListener{
            startActivityForResult(googleSignInClient.signInIntent,RC_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== RC_SIGN_IN){
            try{
                val task=GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let {
                    Toast.makeText(this,"success",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this,MainActivity::class.java))
                }?:run{
                    Toast.makeText(this,"Unable to sign in",Toast.LENGTH_LONG).show()
                }
            }
            catch (ex:Exception){
                Toast.makeText(this, ex.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}