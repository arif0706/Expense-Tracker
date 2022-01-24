package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.expensetracker.ui.home.MainActivity
import com.example.expensetracker.ui.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_on_boarding_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnBoardingActivity : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN=9001
    }

    private val viewModel:OnBoardingActivityViewModel by viewModel()

    private val auth : FirebaseAuth by lazy {
        Firebase.auth
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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
                    firebaseAuthWithGoogle(account.idToken)
                }?:run{
                    toast("Unable to Sign in")
                }
            }
            catch (ex:Exception){
                toast(ex.localizedMessage.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential=GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){task->
                if(task.isSuccessful) {
                    viewModel.updateUser()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                else
                    toast("Login failed")
            }
    }

    override fun onStart() {
        super.onStart()

        val currentUser=auth.currentUser
        if(currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}