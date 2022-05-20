package it.uninsubia.curiosityapp

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.uninsubia.curiosityapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var tvForgot: TextView
    private lateinit var layout: ActivityLoginBinding
    private lateinit var button: Button
    private lateinit var tvRegistrati: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        layout = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(layout.root)
        etEmail = layout.editTextEmail
        etPassword = layout.editTextPassword
        progressBar = layout.progressbar

        tvForgot = layout.forgotTv
        tvForgot.setOnClickListener(this)

        button = layout.loginbutton
        button.setOnClickListener(this)

        tvRegistrati = layout.registerTv
        tvRegistrati.setOnClickListener(this)

    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()



        if (email.isEmpty()) {
            setErrorOnSearchView(etEmail,"Inserisci una mail")
            //etEmail.setError(Html.fromHtml("<font bgcolor='white' color='black'>Inserisci una email</font>"))
            //etEmail.error = "Inserisci una email"
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setErrorOnSearchView(etEmail,"É necessario inserire una email esistente")
            //etEmail.error = "É necessario inserire una email esistente"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            setErrorOnSearchView(etPassword,"É richiesta una password")
            //etPassword.error = "é richiesta una password"
            etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            setErrorOnSearchView(etPassword,"É richiesta una password di più di 6 caratteri")
            //etPassword.error = "é richiesta una password di più di 6 caratteri"
            etPassword.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser

                    if (user!!.isEmailVerified) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        showToast("Login effettuato con successo")
                        progressBar.visibility = View.VISIBLE
                    } else {
                        showToast("Controlla la tua email per verificare il tuo account")
                    }
                } else {
                    showToast("Email o password errate")
                }
            }
    }
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.loginbutton -> {
                loginUser()
            }

            R.id.registerTv -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }

            R.id.forgotTv -> {
                startActivity(Intent(this, ForgotPswActivity::class.java))
            }
        }
    }
    private fun showToast(message: String)
    {
        val inflater: LayoutInflater = layoutInflater
        val layoutToast = inflater.inflate(R.layout.custom_toast,(findViewById<ViewGroup?>(R.id.toast_root)))
        val toastText = layoutToast.findViewById<TextView>(R.id.toast_text)
        toastText.text = message
        val toast2 = Toast(applicationContext)
        toast2.setGravity(Gravity.CENTER,0,0)
        toast2.duration = Toast.LENGTH_SHORT
        toast2.view = layoutToast
        toast2.show()
    }
    private fun setErrorOnSearchView(editText: EditText, errorMessage : String)
    {
        val errorColor = ContextCompat.getColor(this,R.color.white)
        val errorBackgroundColor = ContextCompat.getColor(this,R.color.white)
        val fgcspan = ForegroundColorSpan(errorColor)
        val bgcspan = BackgroundColorSpan(errorBackgroundColor)
        val builder = SpannableStringBuilder(errorMessage)
        builder.setSpan(bgcspan, 0, errorMessage.length, 4)
        editText.error = builder
    }
}