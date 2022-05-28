package it.uninsubia.curiosityapp.ui

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
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
import it.uninsubia.curiosityapp.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private var statType = ""
    private lateinit var radio: RadioButton
    private lateinit var countdown: CountDownTimer
    private lateinit var fragmentContext: Context

    private var numCuriosityGeneral = 0
    private var numCuriosityCinema = 0
    private var numCuriosityCucina = 0
    private var numCuriositySport = 0
    private var numCuriosityTecnologia = 0
    private var numCuriosityStoria = 0

    private var currentTopic = ""

    private val repository: CuriositiesRepository = CuriositiesRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        fragmentContext = requireContext()

        //listener for radio buttons
        binding.radioGroup.setOnCheckedChangeListener { _, i ->
            radio = requireView().findViewById(i)
            statType = radio.contentDescription.toString()
            resetView()
        }
        getResponseUsingCallback(object : FirebaseCallback {
            override fun onResponse(response: Response) {
                val curiosityList = convertResponse(response)
                numCuriosityGeneral = curiosityList.size
                numCuriosityCinema = getCuriosities("Cinema", curiosityList)
                numCuriosityCucina = getCuriosities("Cucina", curiosityList)
                numCuriositySport = getCuriosities("Sport", curiosityList)
                numCuriosityTecnologia = getCuriosities("Tecnologia", curiosityList)
                numCuriosityStoria = getCuriosities("Storia", curiosityList)
                //initialize view
                initView()
                //listener for drop down menu
                setMenu()
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
        val topics = resources.getStringArray(R.array.dropdown_menu_topics)
        val adapter = ArrayAdapter(fragmentContext, R.layout.dropdown_menu_item, topics)
        binding.dropdownMenu.adapter = adapter
        binding.dropdownMenu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //fill bar with cinema curiosities
            }

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //fill bar with selected topic
                currentTopic = topics[position]
                val doneCuriosities = getDoneCuriosities(currentTopic)
                fillProgressBar(
                    binding.progressTopic,
                    binding.valuesTopic,
                    doneCuriosities,
                    findTopicVariable(currentTopic)
                )
            }
        }
    }

    private fun initView() {
        val doneCuriosities = getDoneCuriosities("general")
        fillProgressBar(binding.progressGeneral, binding.valuesGeneral, doneCuriosities, numCuriosityGeneral)
    }

    private fun resetView() {
        //set general bar
        var doneCuriosities = getDoneCuriosities("general")
        fillProgressBar(binding.progressGeneral, binding.valuesGeneral, doneCuriosities, numCuriosityGeneral)
        //set topic bar
        doneCuriosities = getDoneCuriosities(currentTopic)
        fillProgressBar(binding.progressTopic, binding.valuesTopic, doneCuriosities, findTopicVariable(currentTopic))
    }

    private fun getDoneCuriosities(topic: String):Int {
        if(topic == "general"){
            //return list.size  -> knownCuriositiesData.getSize()
        }
        //get known curiosities from its file -> return a list with all
        val knownCuriositiesData = Utility.readKnownCuriosities(fragmentContext)
        return knownCuriositiesData.getSize()
    }

    private fun getCuriosities(topic: String, list: ArrayList<CuriosityData>): Int {
        //get all curiosities from firebase -> return number of curiosities of that topic
        var num = 0
        for (curiosityData in list) {
            if (curiosityData.topic == topic)
                num++
        }
        return num
    }

    private fun fillProgressBar(
        progressBar: com.google.android.material.progressindicator.CircularProgressIndicator,
        tv: TextView,
        doneCuriosities:Int,
        totalCuriosities: Int
    ) {
        //deciding the color
        val loadingColor = ContextCompat.getColor(fragmentContext, R.color.teal_200)
        val color = when (statType) {
            "general" -> {
                ContextCompat.getColor(fragmentContext, R.color.primary_light)
            }
            "known" -> {
                ContextCompat.getColor(fragmentContext, R.color.teal_700)
            }
            "unknown" -> {
                ContextCompat.getColor(fragmentContext, android.R.color.holo_red_dark)
            }
            else -> {
                ContextCompat.getColor(fragmentContext, R.color.primary_light)
            }
        }
        //get percentage -> x:100 = doneCur:totalCur   0:10 000= done:total  add 1*100 per sec
        var perc = (doneCuriosities*100)/totalCuriosities.toFloat()
        perc*=100f
        Log.e("perc","$perc")
        //fill the progress bar with the data retrieved
        ContextCompat.getMainExecutor(fragmentContext).execute {
            progressBar.progress = 0
            countdown = object : CountDownTimer(
                1000,
                100) {
                override fun onTick(millisUntilFinished: Long) {//progress bar fills up
                    progressBar.progress += perc.toInt()
                    progressBar.setIndicatorColor(loadingColor)
                    tv.setTextColor(loadingColor)
                    tv.text = "$doneCuriosities/$totalCuriosities"
                }

                override fun onFinish() {
                    progressBar.setIndicatorColor(color)
                    tv.setTextColor(color)
                }
            }.start()
        }
    }

    private fun resetProgressBars() {
        binding.progressTopic.progress = 0
        binding.progressGeneral.progress = 0
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

    private fun findTopicVariable(topic: String): Int {
        return when (topic) {
            "Cinema" -> numCuriosityCinema
            "Cucina" -> numCuriosityCucina
            "Tecnologia" -> numCuriosityTecnologia
            "Sport" -> numCuriositySport
            "Storia" -> numCuriosityStoria
            else -> 0
        }
    }

}