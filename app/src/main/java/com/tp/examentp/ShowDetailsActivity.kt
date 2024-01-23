package com.tp.examentp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import androidx.lifecycle.Observer
import com.tp.examentp.databinding.ActivityMovieDetailsBinding

class ShowDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var viewModel: ShowViewModel
    var showId: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showId = intent.getIntExtra("showId",0)
        viewModel = ViewModelProvider(this, MovieViewModelFactory(this)).get(ShowViewModel::class.java)

        viewModel.getShowDetails(showId)

        viewModel.showDetails.observe(this) { showDetails ->
            val imageUrl = "https://image.tmdb.org/t/p/w500/${showDetails?.poster_path}"
            Glide.with(binding.imageView.context)
                .load(imageUrl)
                .into(binding.imageView)
            binding.title.text = showDetails.name
            binding.date.text = showDetails.first_air_date
            binding.overview.text = showDetails.overview
            binding.time.text = " ${formatRuntime(showDetails.episode_run_time[0])}"
            val genres = showDetails.genres
            val genreNames = genres.joinToString(", ") { it.name }.toString()
            binding.genres.text = genreNames
            val userRatingPercentage =
                (showDetails?.vote_average?.div(10.0)?.times(100))?.toInt()
            if (userRatingPercentage != null) {
                binding.progressBar.progress = userRatingPercentage
                binding.textViewProgress.text = "$userRatingPercentage%"
            }
        }

        viewModel.connectionStatus.observe(this, Observer { isConnected ->
            if (!isConnected) {
                showConnectionError()
            }
        })

    }

    private fun showConnectionError() {
        val dialogView = layoutInflater.inflate(R.layout.no_internet, null)
        val retryButton = dialogView.findViewById<Button>(R.id.retryButton)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        retryButton.setOnClickListener {
            viewModel.getShowDetails(showId)
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun formatRuntime(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60

        return if (hours > 0) {
            String.format("%dh%02d", hours, remainingMinutes)
        } else {
            String.format("%d min", remainingMinutes)
        }
    }


}