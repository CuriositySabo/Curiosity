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
        initView()
        //listener for radio buttons
        binding.radioGroup.setOnCheckedChangeListener { _, i ->
            radio = requireView().findViewById(i)
            statType = radio.contentDescription.toString()
            initView()
        }
        val topics = resources.getStringArray(R.array.dropdown_menu_topics)
        val adapter = ArrayAdapter(fragmentContext,R.layout.dropdown_menu_item,topics)
        binding.dropdownMenu.adapter = adapter
        //listener for drop down menu
        binding.dropdownMenu.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override  fun onNothingSelected(parent: AdapterView<*>?) {
                //fill bar with cinema curiosities
            }
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //fill bar with selected topic
                val topicSelected = topics[position]
                getCuriosities()
                getDoneCuriosities()
                fillProgressBar(binding.progressTopic)
            }
        }
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.progressTopic.progress = 0
        binding.progressGeneral.progress = 0
    }

    override fun onStop() {
        super.onStop()
        binding.progressTopic.progress = 0
        binding.progressGeneral.progress = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        //set general bar
        getCuriosities()
        getDoneCuriosities()
        fillProgressBar(binding.progressGeneral)
        //set topic bar
        getCuriosities()
        getDoneCuriosities()
        fillProgressBar(binding.progressTopic)
    }

    private fun getDoneCuriosities() {
        //get known curiosities from its file -> return a list with all
    }

    private fun getCuriosities() {
        //get all curiosities from firebase -> return a list with all
    }

    private fun fillProgressBar(progressBar: com.google.android.material.progressindicator.CircularProgressIndicator) {
        //deciding the color
        val loadingColor = ContextCompat.getColor(fragmentContext, R.color.teal_200)
        val color = when(statType){
            "general" -> { ContextCompat.getColor(fragmentContext, R.color.primary_light) }
            "known" -> { ContextCompat.getColor(fragmentContext, R.color.teal_700) }
            "unknown" -> { ContextCompat.getColor(fragmentContext, android.R.color.holo_red_dark) }
            else ->{ ContextCompat.getColor(fragmentContext, R.color.primary_light) }
        }
        //fill the progress bar with the data retrieved
        ContextCompat.getMainExecutor(fragmentContext).execute{
            countdown = object : CountDownTimer(1000, 100) {
                override fun onTick(p0: Long) {//progress bar fills up
                    progressBar.progress += 1
                    progressBar.setIndicatorColor(loadingColor)
                }
                override fun onFinish() {
                    progressBar.setIndicatorColor(color)
                }
            }.start()
        }
    }

}