package it.uninsubia.curiosityapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import it.uninsubia.curiosityapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var  db : DatabaseReference = Firebase.database.reference

    private lateinit var layout: ActivityMainBinding
    private lateinit var logoutBtn: Button
    private lateinit var testBtn: Button
    private lateinit var endtestBtn: Button

    private val channelid = "notifyCuriosity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        auth = FirebaseAuth.getInstance()

        logoutBtn = layout.logoutBtn
        logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val timetoNotification = 5
        // TODO: get timetoNotification from slider!
        testBtn = layout.testBtn
        testBtn.setOnClickListener {
            val intent = Intent(this, NotificationService::class.java)
            intent.putExtra("timetoNotification", timetoNotification)
            startService(intent)
        }

        endtestBtn = layout.endtestBtn
        endtestBtn.setOnClickListener {
            stopService(Intent(this, NotificationService::class.java))
        }


    }

    override fun onStart() {
        super.onStart()

        var title = "Trenini"
        var text =
            "La Biblioteca pubblica di New York ha installato dei piccoli treni per poter consegnare i libri. Questi trenini corrono dal magazzino fino al terzo piano"
        var code = "$title $text".hashCode().toLong()
        var curiosityData = CuriosityData(title, text)
        db.child("curiosità").child("Tecnologia").child(code.toString()).setValue(curiosityData)

        title = "Piano per Mosca"
        text =
            "Il piano di Hitler per Mosca prevedeva di uccidere tutti i suoi residenti cioè 4 milioni di abitanti e coprire l’intera città con un lago artificiale"
        code = "$title $text".hashCode().toLong()
        curiosityData = CuriosityData(title, text)
        db.child("curiosità").child("Storia").child(code.toString()).setValue(curiosityData)


        title = "Diploma Jennifer"
        text =
            "Jennifer Lawrence ha abbandonato la scuola alle medie. Di conseguenza non ha un diploma di scuola superiore"
        code = "$title $text".hashCode().toLong()
        curiosityData = CuriosityData(title, text)
        db.child("curiosità").child("Cinema").child(code.toString()).setValue(curiosityData)

        title = "Prima Pizza"
        text =
            "La pizza ha comunque origine antichissime, addirittura si pensa che già gli Etruschi ne facessero delle grandi scorpacciate. Il termine pizza deriverebbe da pinsa, participio passato del verbo latino pinsare che significa pestare, schiacciare, riferito dunque alla forma. La primissima attestazione del vocabolo pizza è datata prima dell'anno Mille, come \"pizza de pane\": una focaccia che accompagnava carne, pesce o verdure, citata da parecchi autori cinquecenteschi."
        code = "$title $text".hashCode().toLong()
        curiosityData = CuriosityData(title, text)
        db.child("curiosità").child("Pizza").child(code.toString()).setValue(curiosityData)

        title = "Nostra Pizza"
        text =
            "L’archetipo della pizza che conosciamo oggi nasce a Napoli. Però era ancora bianca, condita con aglio, strutto e sale grosso nella versione economica oppure con caciocavallo e basilico in quella premium."
        code = "$title $text".hashCode().toLong()
        curiosityData = CuriosityData(title, text)
        db.child("curiosità").child("Pizza").child(code.toString()).setValue(curiosityData)

        title = "Pomodoro sulla Pizza"
        text =
            "Nel Settecento, quando il pomodoro fa il suo ingresso trionfale nella cucina campana e italiana in generale, la pizza diventa rossa e il suo profumo invade prepotentemente la città dei Borbone."
        code = "$title $text".hashCode().toLong()
        curiosityData = CuriosityData(title, text)
        db.child("curiosità").child("Pizza").child(code.toString()).setValue(curiosityData)

        title = "Pizza più costosa"
        text =
            "il primato per il conto della pizza più salata va a Laszlo Hanyecz, un programmatore che nel 2009 ha pagato per due pizze a domicilio la bellezza di 10mila bitcoin"
        code = "$title $text".hashCode().toLong()
        curiosityData = CuriosityData(title, text)
        db.child("curiosità").child("Pizza").child(code.toString()).setValue(curiosityData)
    }



}