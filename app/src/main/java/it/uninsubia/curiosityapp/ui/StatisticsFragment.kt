package it.uninsubia.curiosityapp.ui

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import it.uninsubia.curiosityapp.*
import it.uninsubia.curiosityapp.Utility.Companion.getTopicsOnDb
import it.uninsubia.curiosityapp.Utility.Companion.readKnownCuriosities
import it.uninsubia.curiosityapp.databinding.FragmentStatisticsBinding
import kotlin.math.roundToInt

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private var statType = "general" //variabile che contiene la tipologia di statistica scelta
    private lateinit var radio: RadioButton
    private lateinit var countdown: CountDownTimer //usato per animare le progress bar
    private lateinit var fragmentContext: Context //contiene il context del fragment
    private var numCuriosityGeneral = 0 //numero di curiosità in generale presenti sul db
    private lateinit var curiositiesForEachTopic: HashMap<String, Int> //contiene la mappa di "topic, numero"
    private lateinit var receivedCuriosities: HashMap<String, Int> //contiene la mappa precedente presente su file
    private lateinit var knownCuriositiesmap: HashMap<String,HashMap<Int, Boolean> > //contiene la mappa : "topic, "id, boolean"
    private var currentTopic = "" //contiene il topic scelto dall'utente
    private lateinit var curiosityList: ArrayList<CuriosityData> //contiene tutte le curiosità sul db
    private val repository: CuriositiesRepository = CuriositiesRepository() //contiene il db
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        fragmentContext = requireContext()
        //rendo non cliccabili i pulsanti all'inizio poichè l'app potrebbe non aver caricato ancora i dati dal db
        binding.radioGeneral.isClickable = false
        binding.radioKnown.isClickable = false
        binding.radioUnknown.isClickable = false
        //listener for radio buttons
        //binding.radioGroup.isClickable = false
        binding.radioGroup.setOnCheckedChangeListener { _, i ->
            radio = requireView().findViewById(i)
            statType = radio.contentDescription.toString()
            resetView()
        }
        //salvataggio delle curiosità dal db
        getResponseUsingCallback(object : FirebaseCallback {
            override fun onResponse(response: Response) {
                //salvataggio nella lista ->
                curiosityList = convertResponse(response)
                //numero di curiosità per topic sul DATABASE
                curiositiesForEachTopic = Utility.getMapOfTopicsCuriosities(curiosityList)
                numCuriosityGeneral = curiosityList.size
                //numero di curiosità ricevute sul FILE locale
                knownCuriositiesmap = readKnownCuriosities(fragmentContext).knowncuriosities
                receivedCuriosities = hashMapOf()
                for(topic in getTopicsOnDb(curiosityList)) {
                    if(knownCuriositiesmap[topic] != null)
                        receivedCuriosities[topic] = knownCuriositiesmap[topic]!!.count()
                    else
                        receivedCuriosities[topic] = 0
                }
                //initialize view
                initView()
                //listener for drop down menu
                setMenu()
                binding.radioGeneral.isClickable = true
                binding.radioKnown.isClickable = true
                binding.radioUnknown.isClickable = true
            }
        })
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        resetProgressBars()
    }

    override fun onStop() {
        super.onStop()
        resetProgressBars()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setMenu() {
        //prendo i valori da mettere dentro il menu
        val topics = resources.getStringArray(R.array.dropdown_menu_topics)
        //creo l'adapter
        val adapter = ArrayAdapter(fragmentContext, R.layout.dropdown_menu_item, topics)
        binding.dropdownMenu.adapter = adapter
        //aggiungo il listener
        binding.dropdownMenu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.progressTopic.progress = 0
            }
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //fill bar with selected topic
                currentTopic = topics[position]
                fillProgressBar(
                    binding.progressTopic,
                    binding.valuesTopic,
                    getDoneCuriositiesTopic(statType,currentTopic),
                    getMaxNumberTopic()
                )
            }
        }
    }

    private fun initView() {
        //inizializzo la view: disegno solo la prima progress bar, la seconda viene disegnata dal menu
        fillProgressBar(binding.progressGeneral, binding.valuesGeneral,
            getDoneCuriositiesGeneral(statType), getMaxNumberGeneral())
    }

    private fun resetView() {
        //set general bar
        fillProgressBar(binding.progressGeneral, binding.valuesGeneral,
            getDoneCuriositiesGeneral(statType), getMaxNumberGeneral())
        //set topic bar
        fillProgressBar(binding.progressTopic, binding.valuesTopic,
            getDoneCuriositiesTopic(statType,currentTopic), getMaxNumberTopic())
    }

    private fun getDoneCuriositiesGeneral(type: String):Int {
        //usata per ottenere il numero di curiosità a cui si ha risposto e perciò quelle che si trovano sul file
        //viene fatta distinzione per quelle sapute e quelle non sapute
        //vengono prese in considerazione le curiosità di tutti i topic
        var flag = 1
        when(type){
            "general" -> {flag = 1}
            "known" -> { flag = 2 }
            "unknown" -> { flag = 3 }
        }
        var t = 0
        var f = 0
        //scorro ogni topic presente sul db
        for(topic in getTopicsOnDb(curiosityList)) {
            if(knownCuriositiesmap[topic] != null) {
                //scorro tutte le curiosità ricavate dal file per singolo topic
                knownCuriositiesmap[topic]!!.forEach {
                    //incremento la variabile t se la curiosità era saputa, f altrimenti
                    if (knownCuriositiesmap[topic] != null)
                        if (it.value) t++
                        else if (!it.value) f++
                }
            }
        }
        //restituisco la variabile in base alle scelte dell'utente
        if(flag == 2)
            return t
        else if(flag == 3)
            return f
        else
            return t + f
    }

    private fun getDoneCuriositiesTopic(type: String, topic: String):Int {
        //usata per ottenere il numero di curiosità a cui si ha risposto
        //vengono prese in considerazione solo le curiosità del topic desiderato
        var flag = 1
        when(type){
            "general" -> {flag = 1}
            "known" -> { flag = 2 }
            "unknown" -> { flag = 3 }
        }
        var t = 0
        var f = 0
        //scorro la mappa del topic selezionato
        if(knownCuriositiesmap[topic] != null) {
            knownCuriositiesmap[topic]!!.forEach {
                if (it.value) t++
                else if (!it.value) f++
            }
        }
        if(flag == 2)
            return t
        else if(flag == 3)
            return f
        else
            return t + f
    }

    private fun getMaxNumberGeneral(): Int{
        //ritorna il numero totale di curiosità
        //se statType=general allora abbiamo già il numero
        if(statType == "general")
            return numCuriosityGeneral
        var i = 0
        //per ogni topic incremento la variabile
        for(topic in getTopicsOnDb(curiosityList)) {
            if(!knownCuriositiesmap[topic].isNullOrEmpty()) {
                i+=knownCuriositiesmap[topic]!!.count()
            }
        }
        return i
    }

    private fun getMaxNumberTopic(): Int{
        //ritorna il numero totale di curiosità del topic scelto
        if(statType == "general")
            return curiositiesForEachTopic[currentTopic]!!
        var i = 0
        if(!knownCuriositiesmap[currentTopic].isNullOrEmpty()) {
            i+=knownCuriositiesmap[currentTopic]!!.count()
        }
        return i
    }

    private fun fillProgressBar(
        progressBar: com.google.android.material.progressindicator.CircularProgressIndicator,
        tv: TextView,
        doneCuriosities:Int,
        totalCuriosities: Int
    ) {
        //funzione per riempire la progress bar in modo animato di una certa percentuale
        //decisione del colore
        val loadingColor = ContextCompat.getColor(fragmentContext, R.color.teal_200) //colore di caricamento della progress bar
        val color = when (statType) {
            "general" -> { ContextCompat.getColor(fragmentContext, R.color.primary_light) }
            "known" -> { ContextCompat.getColor(fragmentContext, R.color.teal_700) }
            "unknown" -> { ContextCompat.getColor(fragmentContext, android.R.color.holo_red_dark) }
            else -> { ContextCompat.getColor(fragmentContext, R.color.primary_light) }
        }
        binding.radioGeneral.isClickable = false
        binding.radioKnown.isClickable = false
        binding.radioUnknown.isClickable = false
        //get percentage -> x:100 = doneCur:totalCur   0:10 000= done:total  add 1*100 per sec
        var perc = 1f
        if(totalCuriosities != 0)
        {
            perc = ((doneCuriosities*100)/totalCuriosities).toFloat()
            perc*=10f
        }
        var percInt = 0f
        //setting del timer
        ContextCompat.getMainExecutor(fragmentContext).execute {
            progressBar.progress = 0
            countdown = object : CountDownTimer(
                1000,  //->1 secondo
                100) {
                override fun onTick(millisUntilFinished: Long) {//progress bar fills up
                    progressBar.progress += perc.toInt() //incremento
                    progressBar.setIndicatorColor(loadingColor)
                    tv.setTextColor(loadingColor)
                    if(perc!=0f)
                        percInt = perc/10f
                    //tv.text = "${percInt.roundToInt()}% - $doneCuriosities/$totalCuriosities"
                    tv.text = getString(
                        R.string.string_with_placeholders,
                        "${percInt.roundToInt()}%",
                        doneCuriosities,
                        totalCuriosities
                    )
                }

                override fun onFinish() {
                    //resetto i colori
                    progressBar.setIndicatorColor(color)
                    tv.setTextColor(color)
                    binding.radioGeneral.isClickable = true
                    binding.radioKnown.isClickable = true
                    binding.radioUnknown.isClickable = true
                }
            }.start() //avvio il timer che disegna le bars
        }
    }

    private fun resetProgressBars() {
        //usata per resettare il progresso di ogni bar
        binding.progressTopic.progress = 0
        binding.progressGeneral.progress = 0
    }

    private fun getResponseUsingCallback(callback: FirebaseCallback) {
        //mette i valori dal db in repository
        repository.getResponseFromRealtimeDatabaseUsingCallback(callback)
    }

    private fun convertResponse(response: Response): ArrayList<CuriosityData> {
        //Inizializzo una lista di Curiosity Data verrà usata per memorizzare tutte le curiosità di un topic
        val curiosityList = arrayListOf<CuriosityData>()

        response.curiosities?.let { curiosities ->
            curiosities.forEach { curiosity ->
                curiosityList.add(curiosity)
            }
        }

        response.exception?.let { exception ->
            exception.message?.let {
            }
        }
        return curiosityList
    }

}