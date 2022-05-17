package it.uninsubia.curiosityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import it.uninsubia.curiosityapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var layout : ActivityRegisterBinding
    private lateinit var etnome: EditText
    private lateinit var etcognome: EditText
    private lateinit var etemail: EditText
    private lateinit var etpassword : EditText
    private lateinit var button: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        layout = ActivityRegisterBinding.inflate(layoutInflater)



        etnome = layout.editTextName
        etcognome = layout.editTextCognome
        etemail = layout.editTextEmail
        etpassword = layout.editTextPassword
        button = layout.button


        button.setOnClickListener() {
            registerUser()
        }



        setContentView(layout.root)

    }

    private fun registerUser( ){
        val nome = etnome.text.toString().trim()
        val cognome = etcognome.text.toString().trim()
        val email = etemail.text.toString().trim()
        val password =  etpassword.text.toString().trim()


        if(nome.isEmpty()) {
            etnome.error = "é richiesto il tuo nome"
            etnome.requestFocus()
            return
        }

        if(cognome.isEmpty()) {
            etcognome.setError("é richiesto il tuo cognome")
            etcognome.requestFocus()
            return
        }

        if(email.isEmpty()) {
            etemail.error = "é richiesta una email"
            etemail.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.error = "É necessario inserire una email esistente"
            etemail.requestFocus()
        }

        if(password.isEmpty()) {
            etpassword.error = "é richiesta una password"
            etpassword.requestFocus()
            return
        }

        if(password.length < 6){
            etpassword.error = "é richiesta una password di più di 6 caratteri"
            etpassword.requestFocus()
            return
        }


        val db = FirebaseDatabase.getInstance("https://curiosity-db178-default-rtdb.firebaseio.com")
        val ref = db.getReference("users").child("$nome $cognome")
        ref.setValue("$nome, $cognome, $email, $password")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }
}