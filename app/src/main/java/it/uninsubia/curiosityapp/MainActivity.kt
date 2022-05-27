package it.uninsubia.curiosityapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import it.uninsubia.myfirebasetest.FirebaseCallback

class MainActivity : AppCompatActivity() {
    private var db: DatabaseReference = Firebase.database.reference

    private val tag = "Main"
    private val repository: CuriositiesRepository = CuriositiesRepository()
    private lateinit var btn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById<Button>(R.id.testBtn)

        btn.setOnClickListener() {
            filldb()
        }

        getResponseUsingCallback(object : FirebaseCallback {
            override fun onResponse(response: Response) {
                print(response)
            }
        })
    }

    private fun filldb() {
        val child = "curiosities"
        // TECNOLOGIA
        var topic = "Tecnologia"

        var title = "Trenini"
        var text =
            "La Biblioteca pubblica di New York ha installato dei piccoli treni per poter consegnare i libri. Questi trenini corrono dal magazzino fino al terzo piano"
        var code = "$title $text $topic".hashCode()
        var curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "AI Halo"
        text =
            "L’assistente personale fornito con i Pc Windows è chiamato Cortana, in onore della AI del videogico Halo"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "Esports Fever"
        text =
            "Gli eSport hanno un pubblico di seguaci molto vasto, un numero di circa 350.000 persone lo seguono quotidianamente"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "Abbigliamento Apple"
        text =
            "Nel 1986 la Apple ha lanciato la sua linea di abbigliamento: Apple Collection. Non ebbe alcun successo, e rimase un semplice esperimento fallito"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "Facebook Energivoro"
        text =
            "Gli utenti Android segnalano un aumento del 20% della durata della batteria dopo aver eliminato l’app di Facebook dai loro telefoni"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        //SPORT
        topic = "Sport"

        title = "Sumo Spaventoso"
        text =
            "In Giappone è considerato un segno di buona fortuna quando un lottatore di sumo spaventa un bambino e lo fa piangere"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "Colori Olimpici"
        text =
            "I colori della bandiera olimpica sono sempre rossi, neri, blu, verdi e gialli su un campo di bianco. Questo perché almeno uno di quei colori appaiono sulla bandiera di ogni nazione del pianeta"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "Lincoln Suplex"
        text =
            "Abraham Lincoln era un abile giocatore di wrestling, partecipò a circa 300 partite e perse una sola volta"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "Svago Lunare"
        text =
            "È possibile trovare ben 3 palline da golf sulla Luna lasciate li dagli astronauti"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        //STORIA
        topic = "Storia"

        title = "Piano per Mosca"
        text =
            "Il piano di Hitler per Mosca prevedeva di uccidere tutti i suoi residenti cioè 4 milioni di abitanti e coprire l’intera città con un lago artificiale"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "Monete in nickel"
        text =
            "Nel 235 a.C. in Iran vengono realizzate le prime monete in Nickel per ordine del Re Eutidemo II"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "Vetro trasparentee"
        text =
            "Già dal tempo degli antichi egizi si conosceva l’esistenza del vetro come materiale. Nel 50 a.C. però sono stati i Siriani ad aver inventato il vetro trasparente"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        //CINEMA
        topic = "Cinema"

        title = "Diploma Jennifer"
        text =
            "Jennifer Lawrence ha abbandonato la scuola alle medie. Di conseguenza non ha un diploma di scuola superiore"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "Ritorno agli studi"
        text =
            "La sceneggiatura del film “Ritorno al futuro” è stata respinta più di 40 volte da tutti i principali studi cinematografici. Alcuni studi li hanno rifiutati più di una volta"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        //CUCINA
        topic = "Cucina"

        title = "Prima Pizza"
        text =
            "La pizza ha comunque origine antichissime, addirittura si pensa che già gli Etruschi ne facessero delle grandi scorpacciate. Il termine pizza deriverebbe da pinsa, participio passato del verbo latino pinsare che significa pestare, schiacciare, riferito dunque alla forma. La primissima attestazione del vocabolo pizza è datata prima dell'anno Mille, come \"pizza de pane\": una focaccia che accompagnava carne, pesce o verdure, citata da parecchi autori cinquecenteschi."
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "Nostra Pizza"
        text =
            "L’archetipo della pizza che conosciamo oggi nasce a Napoli. Però era ancora bianca, condita con aglio, strutto e sale grosso nella versione economica oppure con caciocavallo e basilico in quella premium."
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "Pomodoro sulla Pizza"
        text =
            "Nel Settecento, quando il pomodoro fa il suo ingresso trionfale nella cucina campana e italiana in generale, la pizza diventa rossa e il suo profumo invade prepotentemente la città dei Borbone."
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)

        title = "Pizza più costosa"
        text =
            "il primato per il conto della pizza più salata va a Laszlo Hanyecz, un programmatore che nel 2009 ha pagato per due pizze a domicilio la bellezza di 10mila bitcoin"
        code = "$title $text $topic".hashCode()
        curiosityData = CuriosityData(title, text, topic)
        db.child(child).child(code.toString()).setValue(curiosityData)
    }

    private fun print(response: Response) {
        response.curiosities?.let { curiosities ->
            curiosities.forEach { curiosity ->
                curiosity.title.let {
                    Log.i(tag, it)
                }
            }
        }

        response.exception?.let { exception ->
            exception.message?.let {
                Log.e(tag, it)
            }
        }
    }

    private fun getResponseUsingCallback(callback: FirebaseCallback) {
        repository.getResponseFromRealtimeDatabaseUsingCallback(callback)
    }
}

