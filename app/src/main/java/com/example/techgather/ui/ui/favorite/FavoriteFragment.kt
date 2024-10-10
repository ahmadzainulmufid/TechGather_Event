package com.example.techgather.ui.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.techgather.data.event.ListEventsItem
import com.example.techgather.data.repository.FavoriteRepository
import com.example.techgather.databinding.FragmentFavoriteBinding
import com.example.techgather.ui.DetailActivity
import com.example.techgather.ui.EventAdapter
import com.example.techgather.ui.ui.setting.SettingPreferences
import com.example.techgather.ui.ui.setting.dataStore

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: EventAdapter
    private lateinit var settingPreferences: SettingPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi repository dan ViewModel menggunakan factory
        val repository = FavoriteRepository(requireActivity().application)
        val factory = FavoriteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        adapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id.toString())
            startActivity(intent)
        }

        // Setup RecyclerView
        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoriteFragment.adapter
        }

        // Observe loading state dan tampilkan progress bar jika loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("FavoriteFragment", "Loading state: $isLoading")
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvEvent.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.rvEvent.visibility = View.VISIBLE
            }
        }

        // Observe favoriteEvents untuk menampilkan data
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { favoriteEvents ->
            if (favoriteEvents.isNullOrEmpty()) {
                showNoFavoritesMessage()
            } else {
                val items = favoriteEvents.map { event ->
                    ListEventsItem(
                        id = event.id.toInt(),
                        name = event.name,
                        imageLogo = event.mediaCover,
                        summary = event.summary ?: "No description available"
                    )
                }

                // Find which item was removed, if necessary
                val oldList = adapter.currentList
                adapter.submitList(items) {
                    // Get the index of the removed item
                    if (oldList.size > items.size) {
                        val removedItemIndex = oldList.indexOfFirst { it !in items }
                        if (removedItemIndex >= 0) {
                            adapter.notifyItemRemoved(removedItemIndex)
                        }
                    }
                }
            }
        }


        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                showError(message)
            }
        }

        settingPreferences = SettingPreferences.getInstance(requireContext().dataStore)

        settingPreferences.getThemeSetting().asLiveData()
            .observe(viewLifecycleOwner) { isDarkModeActive ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
    }

    private fun showNoFavoritesMessage() {
        binding.rvEvent.visibility = View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

