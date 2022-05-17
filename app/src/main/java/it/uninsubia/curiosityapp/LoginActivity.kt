package it.uninsubia.curiosityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import it.uninsubia.curiosityapp.databinding.ActivityLoginBinding
import it.uninsubia.curiosityapp.databinding.ActivityRegisterBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var layout: ActivityLoginBinding

    private lateinit var button: Button
    private lateinit var tvRegistrati: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        layout = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        etEmail = layout.editTextEmail
        etPassword = layout.editTextPassword
        progressBar = layout.progressbar

        button = layout.loginbutton
        button.setOnClickListener(this)

        tvRegistrati = layout.registerTv
        tvRegistrati.setOnClickListener() {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        setContentView(layout.root)
    }

    private fun loginUser() {
//        Log.d("TAG", "==========FUNZIONA===========")
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.loginbutton -> {
                loginUser()
            }

            R.id.registerTv -> {

            }
        }
    }


}