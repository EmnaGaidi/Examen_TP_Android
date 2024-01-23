package com.tp.examentp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tp.examentp.Models.Movie
import com.tp.examentp.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity(), MovieAdapter.OnItemClickListener {
    private lateinit var viewModel: MovieViewModel
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchView: SearchView
    private lateinit var languageSpinner: Spinner
    private var filteredList: List<Movie> = emptyList()
    //private var filteredList: List<Movie> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel =
            ViewModelProvider(this, MovieViewModelFactory(this)).get(MovieViewModel::class.java)

        // Initialize adapter with an empty list
        movieAdapter = MovieAdapter(emptyList(), this)

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = movieAdapter
        observeViewModel()

        languageSpinner = binding.languageSpinner
        val languages = resources.getStringArray(R.array.language_array)
        languageSpinner.adapter = ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,languages)
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val selectedLanguage = languages[position]
                filterMoviesByLanguage(selectedLanguage)
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
        /*
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the list when text changes
                movieAdapter.filter.filter(newText)
                return true
            }
        })
        searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this implementation
                observeViewModel()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for this implementation
            }

            override fun afterTextChanged(s: Editable?) {
                movieAdapter.filter(s.toString())
            }
        })*/
        //observeViewModel()

        viewModel.connectionStatus.observe(this) { isConnected ->
            if (!isConnected) {
                showConnectionError()
            }
        }
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


    private fun observeViewModel() {
        viewModel.movies.observe(this) { movies ->
            // Update the adapter with the new data
            movieAdapter.setMovies(movies)
            // Notify the adapter that the data has changed
            movieAdapter.notifyDataSetChanged()
            filteredList = viewModel.getCurrentMoviesList()
        }
    }

    private fun showConnectionError() {
        val dialogView = layoutInflater.inflate(R.layout.no_internet, null)
        val retryButton = dialogView.findViewById<Button>(R.id.retryButton)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        retryButton.setOnClickListener {
            viewModel.loadMovies()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onItemClick(movieId: Int) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("movieId", movieId)
        startActivity(intent)
    }
    private fun filter(query: String?) {
        if (query.isNullOrEmpty()) {
            filteredList = viewModel.getCurrentMoviesList()
        } else {
            val lowerCaseQuery = query.toLowerCase(Locale.ROOT)
            filteredList = viewModel.getCurrentMoviesList().filter {
                it.title.toLowerCase(Locale.ROOT).contains(lowerCaseQuery)
            }
        }
        //movieAdapter.moviesFiltered = filteredList
        movieAdapter.setMovies(filteredList)
        movieAdapter.notifyDataSetChanged()
    }
    private fun filterMoviesByLanguage(language: String) {
        viewModel.movies.observe(this) { movies ->
            val filteredMovies = movies.filter { it.original_language == language }
            movieAdapter.setMovies(filteredMovies)
            movieAdapter.notifyDataSetChanged()
        }
    }

}
