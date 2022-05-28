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
import it.uninsubia.curiosityapp.R
import it.uninsubia.curiosityapp.databinding.FragmentStatisticsBinding

class StatisticsFragment: Fragment() {

    private  var _binding : FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private var statType = ""
    private lateinit var radio: RadioButton
    private lateinit var countdown: CountDownTimer
    private lateinit var fragmentContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        fragmentContext = requireContext()
        //initialize view
        initiView()
        //listener for radio buttons
        binding.radioGroup.setOnCheckedChangeListener { _, i ->
            radio = requireView().findViewById(i)
            statType = radio.contentDescription.toString()
            resetView()
        }
        //listener for drop down menu
        setMenu()
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
        val adapter = ArrayAdapter(fragmentContext,R.layout.dropdown_menu_item,topics)
        binding.dropdownMenu.adapter = adapter
        binding.dropdownMenu.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override  fun onNothingSelected(parent: AdapterView<*>?) {
                //fill bar with cinema curiosities
            }
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //fill bar with selected topic
                val topicSelected = topics[position]
                getCuriosities()
                getDoneCuriosities()
                fillProgressBar(binding.progressTopic,binding.valuesTopic)
            }
        }
    }

    private fun initiView() {
        getCuriosities()
        getDoneCuriosities()
        fillProgressBar(binding.progressGeneral,binding.valuesGeneral)
    }

    private fun resetView() {
        //set general bar
        getCuriosities()
        getDoneCuriosities()
        fillProgressBar(binding.progressGeneral,binding.valuesGeneral)
        //set topic bar
        getCuriosities()
        getDoneCuriosities()
        fillProgressBar(binding.progressTopic,binding.valuesTopic)
    }

    private fun getDoneCuriosities() {
        //get known curiosities from its file -> return a list with all
    }

    private fun getCuriosities() {
        //get all curiosities from firebase -> return a list with all
    }

    private fun fillProgressBar(
        progressBar: com.google.android.material.progressindicator.CircularProgressIndicator,
        tv: TextView
    ) {
        //deciding the color
        val loadingColor = ContextCompat.getColor(fragmentContext, R.color.teal_200)
        val color = when(statType){
            "general" -> { ContextCompat.getColor(fragmentContext, R.color.primary_light) }
            "known" -> { ContextCompat.getColor(fragmentContext, R.color.teal_700) }
            "unknown" -> { ContextCompat.getColor(fragmentContext, android.R.color.holo_red_dark) }
            else ->{ ContextCompat.getColor(fragmentContext, R.color.primary_light) }
        }
        //initialize tv value
        //fill the progress bar with the data retrieved
        ContextCompat.getMainExecutor(fragmentContext).execute{
            progressBar.progress = 0
            countdown = object : CountDownTimer(1000, 100) {
                override fun onTick(p0: Long) {//progress bar fills up
                    progressBar.progress += 1
                    progressBar.setIndicatorColor(loadingColor)
                    tv.setTextColor(loadingColor)
                    tv.text = "${progressBar.progress}/100"
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

}