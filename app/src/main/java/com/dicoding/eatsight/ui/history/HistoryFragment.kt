package com.dicoding.eatsight.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.eatsight.data.AppDatabase
import com.dicoding.eatsight.data.HistoryRepository
import com.dicoding.eatsight.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = HistoryRepository(database.historyDao())
        HistoryViewModelFactory(repository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val adapter = HistoryAdapter { selectedHistory ->
            historyViewModel.deleteSingleHistory(selectedHistory)
        }

        binding.rvHistory.apply {
            layoutManager = GridLayoutManager(context, 2) // 2 kolom
            this.adapter = adapter
        }

        historyViewModel.allHistory.observe(viewLifecycleOwner) { historyList ->
            adapter.submitList(historyList)
        }

        binding.clearHistoryButton.setOnClickListener {
            historyViewModel.deleteAllHistory()
        }

        return binding.root
    }
}