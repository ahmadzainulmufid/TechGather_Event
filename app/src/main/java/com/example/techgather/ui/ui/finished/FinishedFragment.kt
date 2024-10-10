package com.example.techgather.ui.ui.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.techgather.databinding.FragmentFinishedBinding
import com.example.techgather.ui.DetailActivity
import com.example.techgather.ui.EventAdapter
import com.example.techgather.ui.EventViewModel
import com.example.techgather.ui.ui.setting.SettingPreferences
import com.example.techgather.ui.ui.setting.dataStore

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventAdapter: EventAdapter
    private lateinit var settingPreferences: SettingPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        // Initialize RecyclerView
        setupRecyclerView()

        // Observe finished events list
        eventViewModel.listEvent.observe(viewLifecycleOwner) { eventList ->
            eventAdapter.submitList(eventList)
            updateUI(eventList.isEmpty())
        }

        // Observe error message
        eventViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!eventViewModel.isFinishedEventsLoaded) {  // Only show error if data not loaded
                showError(message)
            }
        }

        // Observe loading status
        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!eventViewModel.isFinishedEventsLoaded) {  // Only show loading if data not loaded
                showLoading(isLoading)
            }
        }

        // Setup search functionality
        setupSearchBar()

        // Load finished events if they have not been loaded before
        if (!eventViewModel.isFinishedEventsLoaded) {
            eventViewModel.loadFinishedEvents()
        }

        settingPreferences = SettingPreferences.getInstance(requireContext().dataStore)

        settingPreferences.getThemeSetting().asLiveData().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setupSearchBar() {
        binding.searchView.setupWithSearchBar(binding.searchBar)

        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchView.text.toString()
            if (query.isNotEmpty()) {
                eventViewModel.searchFinishedEvents(query)
            } else {
                eventViewModel.loadFinishedEvents()
            }
            binding.searchBar.setText(binding.searchView.text)
            binding.searchView.hide()
            false
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
            // Handle event click to show event details
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id.toString()) // Mengirimkan eventId sebagai string
            startActivity(intent)
        }
        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = eventAdapter
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvEvent.visibility = View.GONE // Hide RecyclerView while loading
        } else {
            binding.progressBar.visibility = View.GONE
            binding.rvEvent.visibility = View.VISIBLE // Show RecyclerView once loading is finished
        }
    }

    private fun updateUI(isEmpty: Boolean) {
        if (isEmpty) {
            binding.rvEvent.visibility = View.GONE
            binding.lottieView.visibility = View.VISIBLE
        } else {
            binding.rvEvent.visibility = View.VISIBLE
            binding.lottieView.visibility = View.GONE
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
