package com.tp.examentp

import MovieRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import android.widget.Toast
import com.tp.examentp.Models.Movie
import com.tp.examentp.Models.Result
import com.tp.examentp.Models.ShowDetails

class ShowViewModel(private val context: Context) : ViewModel() {
    private val repository = MovieRepository(context)
    private val apiKey = "ea800ad24323df3e454f263fffdc2de4"



    private val _shows = MutableLiveData<List<Result>>()
    val shows: LiveData<List<Result>> get() = _shows
    private var currentShowsList: List<Result> = emptyList()
    fun getCurrentShowsList(): List<Result> {
        return currentShowsList
    }

    private val _showDetails = MutableLiveData<ShowDetails>()
    val showDetails: LiveData<ShowDetails> get() = _showDetails

    private val _connectionStatus = MutableLiveData<Boolean>()
    val connectionStatus: LiveData<Boolean> get() = _connectionStatus

    init {
        loadShows()
    }

    fun loadShows() {
        if (repository.isNetworkAvailable()) {
            repository.getShows(apiKey, object : OnShowsFetchedListener {
                override fun onShowsFetched(showsList: List<Result>) {
                    _shows.postValue(showsList)
                    currentShowsList = showsList
                }

                override fun onError(errorMessage: String) {
                    Toast.makeText(context, "Error fetching movies", Toast.LENGTH_SHORT).show()
                }
            })
        }
        else{
            _connectionStatus.postValue(false)
        }
    }
    fun getShowDetails(showId: Int) {
        if (repository.isNetworkAvailable()) {
            repository.getShowDetails(apiKey, showId, object : OnShowDetailsFetchedListener {
                override fun onShowDetailsFetched(showDetails: ShowDetails) {
                    _showDetails.postValue(showDetails)
                }

                override fun onError(errorMessage: String) {
                    Toast.makeText(context, "Error fetching show details", Toast.LENGTH_SHORT).show()

                }
            })
        }
        else{
            _connectionStatus.postValue(false)
        }
    }
}


interface OnShowsFetchedListener {
    fun onShowsFetched(resultList: List<Result>)
    fun onError(errorMessage: String)
}

interface OnShowDetailsFetchedListener {
    fun onShowDetailsFetched(showDetails: ShowDetails)
    fun onError(errorMessage: String)
}