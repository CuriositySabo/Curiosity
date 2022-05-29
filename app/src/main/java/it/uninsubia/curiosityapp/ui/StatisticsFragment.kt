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
    private var statType = "general"
    private lateinit var radio: RadioButton
    private lateinit var countdown: CountDownTimer
    private lateinit var fragmentContext: Context

    private var numCuriosityGeneral = 0
    private lateinit var curiositiesForEachTopic: HashMap<String, Int>
    private lateinit var receivedCuriosities: HashMap<String, Int>
    private lateinit var knownCuriositiesmap: HashMap<String,HashMap<Int, Boolean> >
    private var currentTopic = ""

    private lateinit var curiosityList: ArrayList<CuriosityData>
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
        //get known curiosities from its file -> return a list with all
        var flag = 1
        when(type){
            "general" -> {flag = 1}
            "known" -> { flag = 2 }
            "unknown" -> { flag = 3 }
        }
        var t = 0
        var f = 0
        for(topic in getTopicsOnDb(curiosityList)) {
            if(knownCuriositiesmap[topic] != null) {
                knownCuriositiesmap[topic]!!.forEach {
                    if (knownCuriositiesmap[topic] != null)
                        if (it.value) t++
                        else if (!it.value) f++
                }
            }
        }
        if(flag == 2)
            return t
        else if(flag == 3)
            return f
        else
            return t + f
    }

    private fun getDoneCuriositiesTopic(type: String, topic: String):Int {
        //get known curiosities from its file -> return a list with all
        var flag = 1
        when(type){
            "general" -> {flag = 1}
            "known" -> { flag = 2 }
            "unknown" -> { flag = 3 }
        }
        var t = 0
        var f = 0
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
        if(statType == "general")
            return numCuriosityGeneral
        var t = 0
        var f = 0
        for(topic in getTopicsOnDb(curiosityList)) {
            if(!knownCuriositiesmap[topic].isNullOrEmpty()) {
                knownCuriositiesmap[topic]!!.forEach {
                    if (it.value) t++
                    else if (!it.value) f++
                }
            }
        }
        return f+t
    }

    private fun getMaxNumberTopic(): Int{
        if(statType == "general")
            return curiositiesForEachTopic[currentTopic]!!
        var t = 0
        var f = 0
        if(!knownCuriositiesmap[currentTopic].isNullOrEmpty()) {
            knownCuriositiesmap[currentTopic]!!.forEach {
                if (it.value) t++
                else if (!it.value) f++
            }
        }
        return f+t
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
        var perc = 1f
        if(totalCuriosities != 0)
        {
            perc = ((doneCuriosities*100)/totalCuriosities).toFloat()
            perc*=10f
        }
        var percInt = 0f
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
                    if(perc!=0f)
                        percInt = perc/10f
                    tv.text = "${percInt.roundToInt()}% - $doneCuriosities/$totalCuriosities"
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