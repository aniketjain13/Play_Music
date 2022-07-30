package com.aniketjain.playmusic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aniketjain.playmusic.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    //BINDING DECLARATION
    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //SETTING THE THEME OF THE ACTIVITY
        setTheme(MainActivity.currentThemeNav[MainActivity.themeindex])

        //INITIALIZING BINDING
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "About"

        binding.abouttxt.text = aboutText()
    }

    //FUNCTION TO PRINT TEXT IN ABOUT ACTIVITY
    private fun aboutText(): String{
        return "Developed by: Aniket Jain" +
                "\n\nWelcome to Play Music" + "\nThe app would let you manage all the songs available on your phone." +
                "\n\n\nIf you want to provide any feedback, I would love to hear that."
    }

}