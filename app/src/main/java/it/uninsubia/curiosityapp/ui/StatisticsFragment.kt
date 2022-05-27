package it.uninsubia.curiosityapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import it.uninsubia.curiosityapp.R
import it.uninsubia.curiosityapp.databinding.FragmentStatisticsBinding

class StatisticsFragment: Fragment() {

    private  var _binding : FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        val topics = resources.getStringArray(R.array.dropdown_menu_topics)
        val adapter = ArrayAdapter(requireContext(),R.layout.dropdown_menu_item,topics)
        _binding!!.dropdownMenu.adapter = adapter
        _binding!!.dropdownMenu.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override  fun onNothingSelected(parent: AdapterView<*>?){

            }

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}