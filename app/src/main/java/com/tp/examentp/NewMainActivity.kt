package com.tp.examentp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.ToggleButton
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tp.examentp.databinding.ActivityMainBinding
import com.tp.examentp.databinding.ActivityNewMainBinding
import com.tp.examentp.ui.theme.ExamenTpTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewMainBinding
    private lateinit var darkModeToggle: ToggleButton
    private lateinit var switcher: Switch
    private lateinit var btn_logout : Button
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        binding = ActivityNewMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        btn_logout = binding.logout
        user = auth.currentUser!!
        if(user == null){
            val intent = Intent(this@NewMainActivity, login::class.java)
            startActivity(intent)
            finish()
        }
        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@NewMainActivity, login::class.java)
            startActivity(intent)
            finish()
        }
        switcher = binding.switcher
        switcher.isChecked = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        switcher.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Activer le mode nuit
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // Désactiver le mode nuit
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            recreate() // Recréer l'activité pour appliquer immédiatement le nouveau mode
        }



        /*
        darkModeToggle = binding.darkModeToggle
        darkModeToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Activer le mode nuit
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // Désactiver le mode nuit
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }*/


        binding.moviesButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(this@NewMainActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }

        binding.tvShowsButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(this@NewMainActivity, ShowsActivity::class.java)
                startActivity(intent)
            }
        }
    }


}