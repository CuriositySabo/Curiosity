package it.uninsubia.curiosityapp

import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import it.uninsubia.curiosityapp.databinding.ActivityForgotPswBinding

class ForgotPswActivity : AppCompatActivity() {
    private lateinit var layout: ActivityForgotPswBinding
    private lateinit var resetBtn: Button
    private lateinit var etEmail: EditText
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityForgotPswBinding.inflate(layoutInflater)
        setContentView(layout.root)
        auth = FirebaseAuth.getInstance()

        progressBar = layout.progressbar
        etEmail = layout.editTextEmail
        resetBtn = layout.resetBtn
        resetBtn.setOnClickListener {
            resetPassword()
        }

    }

    private fun resetPassword() {
        val email = etEmail.text.toString().trim()

        if (email.isEmpty()) {
            Utility.setErrorOnSearchView(etEmail,"Inserisci una mail",this)
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Utility.setErrorOnSearchView(etEmail,"É necessario inserire una email esistente",this)
            etEmail.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE

        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                showToast("Controlla la tua email per resettare la password!")
            } else {
                showToast("Qualcosa non è risucito. Riprova!")
            }
        }
        progressBar.visibility = View.GONE
    }
    private fun showToast(message: String)
    {
        val inflater: LayoutInflater = layoutInflater
        val layoutToast = inflater.inflate(R.layout.custom_toast,(findViewById(R.id.toast_root)))
        val toastText = layoutToast.findViewById<TextView>(R.id.toast_text)
        toastText.text = message
        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.BOTTOM,0,0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layoutToast
        toast.show()
    }
}