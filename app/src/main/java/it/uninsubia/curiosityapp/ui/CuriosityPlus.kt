package it.uninsubia.curiosityapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import it.uninsubia.curiosityapp.*
import it.uninsubia.curiosityapp.databinding.ActivityCuriosityPlusBinding
import kotlin.random.Random

class CuriosityPlus : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityCuriosityPlusBinding
    private lateinit var countdown: CountDownTimer

    private val tag = "Curiosity Plus"

    private val defaulttext = "Hai finito le curiosità!\nSeleziona un altro topic!"
    private val repository: CuriositiesRepository = CuriositiesRepository()
    private lateinit var curiositiesList: ArrayList<CuriosityData>
    private lateinit var totalcuriositiesMap: HashMap<String, Int>

    private lateinit var currentCuriosity: CuriosityData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuriosityPlusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getResponseUsingCallback(object : FirebaseCallback {
            override fun onResponse(response: Response) {
                curiositiesList = convertResponse(response)
                // scarico dal db la mappa con il numero massimo di curiosità per ogni topic
                totalcuriositiesMap = Utility.getMapOfTopicsCuriosities(curiositiesList)
                Log.e(tag, "PRIMA")
            }
        })

        binding.imageViewPlay.setOnClickListener(this)

        binding.buttonExit.setOnClickListener(this)

        binding.buttonSapevo.setOnClickListener(this)

        binding.buttonNonSapevo.setOnClickListener(this)

    }

    private fun getRandomString(): String {
        val stringList = listOf("Bravo!", "Genio!", "Ne sai eh!", "Continua così!", "Vai!", "Wow!")
        return stringList[(stringList.indices).random()]
    }

    private fun getResponseUsingCallback(callback: FirebaseCallback) {
        repository.getResponseFromRealtimeDatabaseUsingCallback(callback)
    }

    private fun convertResponse(response: Response): ArrayList<CuriosityData> {
        // Inizializzo una lista di Curiosity Data verrà usata per memorizzare tutte le curiosità di un topic
        val curiosityList = arrayListOf<CuriosityData>()

        response.curiosities?.let { curiosities ->
            curiosities.forEach { curiosity ->
                curiosityList.add(curiosity)
            }
        }


        response.exception?.let { exception ->
            exception.message?.let {
                Log.e(tag, it)
            }
        }

        return curiosityList
    }

    private fun generateCuriosity(
        context: Context,
        curiositiesList: ArrayList<CuriosityData>,
        totalcuriositiesMap: HashMap<String, Int>
    ): CuriosityData {

        // leggo le curiosità già ricevute
        val receivedCuriositiesmap = Utility.readKnownCuriosities(context).knowncuriosities

        Log.e(tag, totalcuriositiesMap.toString())

        // stessa mappa ma con quante curiosità sono già state ricevute  la utilizzo per fare il confronto con quella precedente
        val alreadyInReceivedCounter = hashMapOf<String, Int>()
        for (topic in Utility.getTopicsOnDb(curiositiesList)) {
            alreadyInReceivedCounter[topic] =
                if (!receivedCuriositiesmap[topic].isNullOrEmpty()) receivedCuriositiesmap[topic]!!.count()
                else 0
        }
        Log.e(tag, alreadyInReceivedCounter.toString())


        // creo la lista con i topic selezionati dall'utente
        val possibleTopics = listChosenTopics(context)
        // per ognuno di essi controllo che non siano già state ricevute tutte le curiosità
        // se sono già state ricevute tutte levo il topic dai topic possibili
        totalcuriositiesMap.forEach() {
            val key = it.key
            if (it.value == alreadyInReceivedCounter[key]) {
                possibleTopics.remove(key)
            }
        }

        // init variabili per selezionare una curiosità random dalla lista con le curiosità
        var randomIndex: Int
        var chosenTopic: String?
        var randitemcode: Int
        var chosenCuriosity: CuriosityData

        // se vi sono topic che hanno curiosità non già inviate cerco una tra esse
        if (possibleTopics.size == 0) {
            // inizializzo un dato per dopo
            chosenCuriosity = CuriosityData()
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val prefEditor = prefs.edit()
            prefEditor.putBoolean("notification", false)
            prefEditor.apply()
        } else {
            do {
                do {
                    randomIndex = Random.nextInt(curiositiesList.size)
                    chosenCuriosity = curiositiesList[randomIndex]
                    chosenTopic = chosenCuriosity.topic
                    // ripeto la generazione finchè non trovo la curiosià di un topic possibile
                } while (!possibleTopics.contains(chosenTopic))
                // genero il codice della curiosità
                randitemcode =
                    "${chosenCuriosity.title} ${chosenCuriosity.text} ${chosenCuriosity.topic}".hashCode()
                // se vi sono curiosità sul file e tra le curiosità già ricevute non vi è quella generata posso andare avanti
            } while (!receivedCuriositiesmap[chosenTopic].isNullOrEmpty() &&
                receivedCuriositiesmap[chosenTopic]!!.contains(randitemcode)
            )
        }
        return chosenCuriosity
    }

    private fun listChosenTopics(context: Context): ArrayList<String> {
        // leggo tutti i topics esistenti
        val topics = Utility.readTopicsFile(context)

        // in chosenfield  metto i topics checkati dall'utente
        val chosenFields = ArrayList<String>()

        // controllo quali sono checkati
        topics.forEach {
            if (it.checked)
                chosenFields.add(it.topicName)
        }

        return chosenFields
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_sapevo -> loSapevoListener()
            R.id.button_non_sapevo -> nonLoSapevoListener()
            R.id.button_exit -> exitListener()
            R.id.imageView_play -> playListener()
        }
    }

    private fun playListener() {


        //if play_imageView is clicked then progress bar starts
        binding.startLayout.visibility = View.GONE
        binding.progressBarLayout.visibility = View.VISIBLE


        countdown = object : CountDownTimer(1000, 20) {
            override fun onTick(p0: Long) {//progress bar fills up
                binding.progressBar.progress += 2
            }

            override fun onFinish() { //progress bar is no longer needed
                binding.progressBarLayout.visibility = View.GONE
                binding.gameProgressBar.visibility = View.VISIBLE
                binding.gameContainer.visibility = View.VISIBLE

                currentCuriosity = generateCuriosity(baseContext, curiositiesList, totalcuriositiesMap)
                if (currentCuriosity != CuriosityData()) {
                    binding.tvGame.text = currentCuriosity.text
                } else {
                    binding.tvGame.text = defaulttext
                    binding.buttonSapevo.visibility = View.GONE
                    binding.buttonNonSapevo.visibility = View.GONE
                }
            }
        }.start()
    }

    private fun exitListener() {
        //return to home fragment
        startActivity(
            Intent(this, nav_drawer::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    private fun loSapevoListener() {
        Utility.writeKnownCuriositiesFile(
            this,
            arrayListOf(currentCuriosity.title, currentCuriosity.text, currentCuriosity.topic),
            false
        )

        currentCuriosity = generateCuriosity(this, curiositiesList, totalcuriositiesMap)
        if (currentCuriosity != CuriosityData()) {
            binding.tvGame.text = currentCuriosity.text
        } else {
            binding.tvGame.text = defaulttext
            binding.buttonSapevo.visibility = View.GONE
            binding.buttonNonSapevo.visibility = View.GONE
        }


        /*if (currentCuriosity != CuriosityData()) {
            Utility.writeKnownCuriositiesFile(
                this,
                arrayListOf(currentCuriosity.title, currentCuriosity.text, currentCuriosity.topic),
                false
            )
            currentCuriosity = generateCuriosity(this, curiositiesList, totalcuriositiesMap)
            binding.tvGame.text = currentCuriosity.text
        } else {
            binding.tvGame.text = "Hai finito le curtiosità!!"
        }*/

        //set known curiosity
        binding.gameProgressBar.setIndicatorColor(
            ContextCompat.getColor(
                this,
                R.color.teal_200
            )
        )
        binding.gameProgressBar.incrementProgressBy(1)
        if (binding.gameProgressBar.progress == 10) {
            binding.gameProgressBar.progress = 0

            Thread {
                val colorDefault = binding.gameTitle.textColors
                binding.gameTitle.text = getRandomString()
                binding.gameTitle.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.primary_light
                    )
                )
                Thread.sleep(2000)
                binding.gameTitle.setTextColor(colorDefault)
                binding.gameTitle.text = resources.getString(R.string.game_title)
            }.start()
        }

        //save on local file

        //fetch next curiosity
    }

    private fun nonLoSapevoListener() {

        Utility.writeKnownCuriositiesFile(
            this,
            arrayListOf(currentCuriosity.title, currentCuriosity.text, currentCuriosity.topic),
            false
        )

        currentCuriosity = generateCuriosity(this, curiositiesList, totalcuriositiesMap)
        if (currentCuriosity != CuriosityData()) {
            binding.tvGame.text = currentCuriosity.text
        } else {
            binding.tvGame.text = defaulttext
            binding.buttonSapevo.visibility = View.GONE
            binding.buttonNonSapevo.visibility = View.GONE
        }


        //set unknown curiosity
        if (binding.gameProgressBar.progress > 0) {
            binding.gameProgressBar.setIndicatorColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.holo_red_light
                )
            )
            binding.gameProgressBar.progress -= 1
        }
    }

}

/* Items IDs
    * layout generale: game_general_layout
    * title: game_title
    * cardView container: game_container_card
    * frame layout start: start_layout
    * imageView clickable: imageView_play
    * progressBar layout: progress_bar_layout
    * wait progress bar: progress_bar
    * game layout: game_container
    * image view: iv_game
    * textview: tv_game
    * game progress bar: game_progress_bar
    * layout buttons: container_buttons
    * green button: button_sapevo
    * red button: button_non_sapevo
    * exit button: button_exit*/