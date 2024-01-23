package com.tp.examentp

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.SpinnerAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tp.examentp.Models.Movie
import com.tp.examentp.databinding.ItemMovieBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale




class MovieAdapter(private var movies: List<Movie>?,public val listener: OnItemClickListener?) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    var moviesFiltered: List<Movie> = movies.orEmpty().toMutableList()
    init {
        moviesFiltered = movies.orEmpty()
    }

    interface OnItemClickListener {
        fun onItemClick(movieId: Int)
    }

    class ViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.title
        val posterImage = binding.posterImage
        val date = binding.releaseDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        if (movies != null)
            return movies!!.size
        else return 0
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieItem = movies?.get(position)
        holder.title.text = movieItem?.title
        val date = formatDate(movieItem?.release_date)
        holder.date.text = "Release Date: ${date.toString()}"
        val imageUrl = "https://image.tmdb.org/t/p/w500/${movieItem?.poster_path}"
        Glide.with(holder.posterImage.context)
            .load(imageUrl)
            .into(holder.posterImage)

        holder.itemView.setOnClickListener {
            val movieId = movies?.get(position)?.id ?: -1
            listener?.onItemClick(movieId)
        }
    }

    private fun formatDate(inputDate: String?): String {
        if (inputDate.isNullOrEmpty()) {
            return ""
        }
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return inputDate
    }



    fun setMovies(movies: List<Movie>?) {
        this.movies = movies
        notifyDataSetChanged()
    }


    /*
    fun filter(query: String) {
        moviesFiltered = if (query.isEmpty()) {
            movies.orEmpty()
        } else {
            movies.orEmpty().filter { movie ->
                movie.title.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))
            }

        }
        notifyDataSetChanged()
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString().toLowerCase(Locale.ROOT)
                moviesFiltered = if (charSearch.isEmpty()) {
                    movies.orEmpty()
                } else {
                    movies.orEmpty().filter { movie ->
                        movie.title.toLowerCase(Locale.ROOT).contains(charSearch)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = moviesFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                moviesFiltered = results?.values as List<Movie>
                notifyDataSetChanged()
            }
        }
    }*/

}

