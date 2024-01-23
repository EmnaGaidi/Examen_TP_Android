package com.tp.examentp

import com.tp.examentp.Models.Movie
import com.tp.examentp.Models.MovieDetails
import com.tp.examentp.Models.MoviesResponse
import com.tp.examentp.Models.ShowDetails
import com.tp.examentp.Models.discoverResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.Path


interface MovieApiService {
    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String): Call<MoviesResponse>

    @GET("discover/tv")
    fun getTvShows(@Query("api_key") apiKey: String): Call<discoverResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") movieId: Int, @Query("api_key") apiKey: String): Call<MovieDetails>

    @GET("tv/{tv_id}")
    fun getShowDetails(@Path("tv_id") tv_id: Int, @Query("api_key") apiKey: String): Call<ShowDetails>
}