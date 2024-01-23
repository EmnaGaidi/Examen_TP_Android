package com.tp.examentp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tp.examentp.Models.Movie
import com.tp.examentp.Models.Result
import com.tp.examentp.databinding.ActivityMainBinding
import java.util.Locale

class ShowsActivity : AppCompatActivity(), ShowAdapter.OnItemClickListener {
    private lateinit var viewModel: ShowViewModel
    private lateinit var showAdapter: ShowAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchView: SearchView
    private lateinit var languageSpinner: Spinner
    private var filteredList: List<Result> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel =
            ViewModelProvider(this, MovieViewModelFactory(this)).get(ShowViewModel::class.java)

        // Initialize adapter with an empty list
        showAdapter = ShowAdapter(emptyList(), this)

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = showAdapter

        observeViewModel()

        languageSpinner = binding.languageSpinner
        val languages = resources.getStringArray(R.array.language_array)
        languageSpinner.adapter = ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,languages)
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val selectedLanguage = languages[position]
                filterShowsByLanguage(selectedLanguage)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Ne rien faire ici
            }
        }
        searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return false
            }
        })
        viewModel.connectionStatus.observe(this) { isConnected ->
            if (!isConnected) {
                showConnectionError()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.shows.observe(this) { shows ->
            // Update the adapter with the new data
            showAdapter.setShows(shows)
            // Notify the adapter that the data has changed
            showAdapter.notifyDataSetChanged()
        }
    }

    private fun showConnectionError() {
        val dialogView = layoutInflater.inflate(R.layout.no_internet, null)
        val retryButton = dialogView.findViewById<Button>(R.id.retryButton)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        retryButton.setOnClickListener {
            viewModel.loadShows()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Ajoutez ici le code que vous souhaitez exécuter lorsque le bouton de retour est cliqué
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onItemClick(showId: Int) {
        val intent = Intent(this, ShowDetailsActivity::class.java)
        intent.putExtra("showId", showId)
        startActivity(intent)
    }
    private fun filter(query: String?) {
        if (query.isNullOrEmpty()) {
            filteredList = viewModel.getCurrentShowsList()
        } else {
            val lowerCaseQuery = query.toLowerCase(Locale.ROOT)
            filteredList = viewModel.getCurrentShowsList().filter {
                it.name.toLowerCase(Locale.ROOT).contains(lowerCaseQuery)
            }
        }
        //movieAdapter.moviesFiltered = filteredList
        showAdapter.setShows(filteredList)
        showAdapter.notifyDataSetChanged()
    }
    private fun filterShowsByLanguage(language: String) {
        viewModel.shows.observe(this) { shows ->
            val filteredShows = shows.filter { it.original_language == language }
            showAdapter.setShows(filteredShows)
            showAdapter.notifyDataSetChanged()
        }
    }
}