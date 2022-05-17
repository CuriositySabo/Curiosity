package it.uninsubia.curiosityapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import it.uninsubia.curiosityapp.databinding.ActivityLoginBinding
import it.uninsubia.curiosityapp.databinding.ActivityRegisterBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var layout : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        layout = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        setContentView(layout.root)


    }
}