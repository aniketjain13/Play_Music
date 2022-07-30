package com.aniketjain.playmusic

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aniketjain.playmusic.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {

    //BINDING DECLARATION
    lateinit var binding: ActivitySettingsBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //SETTING THEME FOR THE ACTIVITY
        setTheme(MainActivity.currentThemeNav[MainActivity.themeindex])

        //INITIALIZING BINDING
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Settings"

        when(MainActivity.themeindex) {
            0 -> binding.holoBlueDarkTheme.setBackgroundColor(Color.YELLOW)
            1 -> binding.purple500Theme.setBackgroundColor(Color.YELLOW)
            2 -> binding.coolGreenTheme.setBackgroundColor(Color.YELLOW)
            3 -> binding.coolPinkTheme.setBackgroundColor(Color.YELLOW)
            4 -> binding.blackTheme.setBackgroundColor(Color.YELLOW)
        }
        binding.holoBlueDarkTheme.setOnClickListener { saveTheme(0)}
        binding.purple500Theme.setOnClickListener { saveTheme(1)}
        binding.coolGreenTheme.setOnClickListener { saveTheme(2)}
        binding.coolPinkTheme.setOnClickListener { saveTheme(3)}
        binding.blackTheme.setOnClickListener { saveTheme(4)}

        binding.sortBtn.setOnClickListener{
            var currentSort = MainActivity.sortOrder
            val menuList = arrayOf("Recently Added", "Name", "Size")
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Sort")
                .setPositiveButton("OK") { _, _ ->
                    val editor = getSharedPreferences("SORTING", MODE_PRIVATE).edit()
                    editor.putInt("sortOrder", currentSort)
                    editor.apply()
                }
                .setSingleChoiceItems(menuList, currentSort){ _, which->
                    currentSort = which
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        }

    }

    //FUNCTION TO STORE THE CURRENT THEME OF THE APP USING SHARED PREFERENCES
    private fun saveTheme(index: Int){
        if(MainActivity.themeindex != index){
            val editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("themeindex", index)
            editor.apply()
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Apply Theme").setMessage("Do you want to change theme?")
                .setPositiveButton("YES") { _, _ ->
                    exitApplication()
                }
                .setNegativeButton("NO"){dialog, _->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }
    }
}