package it.uninsubia.curiosityapp.ui.topics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import it.uninsubia.curiosityapp.Utility
import it.uninsubia.curiosityapp.databinding.FragmentTopicSelectionBinding

class TopicSelectionFragment : Fragment() {

    private var _binding: FragmentTopicSelectionBinding? = null
    private lateinit var topicsAdapter: TopicsListAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = FragmentTopicSelectionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecyclerView()
        addDataSet()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView()
    {
        val recyclerView = _binding?.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
        topicsAdapter = TopicsListAdapter()
        recyclerView?.adapter = topicsAdapter
    }

    private fun addDataSet()
    {
        val data = Utility.readTopicsFile(requireContext())
        topicsAdapter.submitList(data)
    }
}