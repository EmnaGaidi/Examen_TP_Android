import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.tp.examentp.Models.MovieDetails
import com.tp.examentp.Models.MoviesResponse
import com.tp.examentp.Models.ShowDetails
import com.tp.examentp.Models.discoverResponse
import com.tp.examentp.MovieApiService
import com.tp.examentp.OnMovieDetailsFetchedListener
import com.tp.examentp.OnMoviesFetchedListener
import com.tp.examentp.OnShowDetailsFetchedListener
import com.tp.examentp.OnShowsFetchedListener
import com.tp.examentp.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieRepository(private val context: Context) {
    private val movieApiService: MovieApiService = Retrofit.createApiService()

    fun getPopularMovies(apiKey: String, listener: OnMoviesFetchedListener) {
        if (isNetworkAvailable()) {
            val call = movieApiService.getPopularMovies(apiKey)

            call.enqueue(object : Callback<MoviesResponse> {
                override fun onResponse(call: Call<MoviesResponse>, response: Response<MoviesResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Log.d("MovieViewModel", "Movies fetched successfully")
                            listener.onMoviesFetched(it.results)
                        }
                    } else {
                        Log.e("MovieRepository", "Error response: ${response.code()}")
                        listener.onError("Error fetching movies")
                    }
                }

                override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                    listener.onError("Connection error")
                }
            })
        } else {
            listener.onError("No internet connection")
        }
    }

    fun getShows(apiKey: String, listener: OnShowsFetchedListener) {
        if (isNetworkAvailable()) {
            val call = movieApiService.getTvShows(apiKey)

            call.enqueue(object : Callback<discoverResponse> {
                override fun onResponse(call: Call<discoverResponse>, response: Response<discoverResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Log.d("MovieViewModel", "Movies fetched successfully")
                            listener.onShowsFetched(it.results)
                        }
                    } else {
                        Log.e("MovieRepository", "Error response: ${response.code()}")
                        listener.onError("Error fetching movies")
                    }
                }

                override fun onFailure(call: Call<discoverResponse>, t: Throwable) {
                    listener.onError("Connection error")
                }
            })
        } else {
            listener.onError("No internet connection")
        }
    }

    fun getMovieDetails(apiKey: String, movieId: Int, listener: OnMovieDetailsFetchedListener) {
        if (isNetworkAvailable()) {
            val call = movieApiService.getMovieDetails(movieId, apiKey)

            call.enqueue(object : Callback<MovieDetails> {
                override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            listener.onMovieDetailsFetched(it)
                        }
                    } else {
                        listener.onError("Error fetching movie details")
                    }
                }

                override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                    listener.onError("Connection error")
                }
            })
        } else {
            listener.onError("No internet connection")
        }
    }

    fun getShowDetails(apiKey: String, showId: Int, listener: OnShowDetailsFetchedListener) {
        if (isNetworkAvailable()) {
            val call = movieApiService.getShowDetails(showId, apiKey)

            call.enqueue(object : Callback<ShowDetails> {
                override fun onResponse(call: Call<ShowDetails>, response: Response<ShowDetails>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            listener.onShowDetailsFetched(it)
                        }
                    } else {
                        listener.onError("Error fetching show details")
                    }
                }

                override fun onFailure(call: Call<ShowDetails>, t: Throwable) {
                    listener.onError("Connection error")
                }
            })
        } else {
            listener.onError("No internet connection")
        }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}