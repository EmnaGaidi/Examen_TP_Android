package com.tp.examentp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.tp.examentp.databinding.ActivityLoginBinding
import com.tp.examentp.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class login : AppCompatActivity() {
    //private lateinit var binding: ActivityLoginBinding
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var progressBar: ProgressBar
    private lateinit var textView: TextView
    private lateinit var intent : Intent

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this@login, NewMainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //binding = ActivityLoginBinding.inflate(layoutInflater)
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        buttonLogin = findViewById(R.id.btn_login)
        mAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)
        textView = findViewById(R.id.registerNow)
        textView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val intent = Intent(this@login, Register::class.java)
                startActivity(intent)
                finish()
            }
        })
        /*
        val btnRegister: Button = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val intent = Intent(this@login, Register::class.java)
            startActivity(intent)
            finish()
        }*/

        val btnLogin: Button = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener{
            email = editTextEmail.text.toString()
            password = editTextPassword.text.toString()

            Log.d("email",email)
            Log.d("password", password)
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@login, "Enter Email and Password", Toast.LENGTH_SHORT).show()
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@login) { task ->
                        progressBar.visibility = View.GONE
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(
                                this@login,
                                "Authentication successful.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            Log.d("authentification", "c bon")
                            intent = Intent(this@login, NewMainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                this@login,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
        }

    }
}