package com.aniketjain.playmusic

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.aniketjain.playmusic.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {

    //VARIABLE DECLARATION
    private lateinit var binding: ActivitySelectionBinding
    private lateinit var musicAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //SETTING THEME FOR THE ACTIVITY
        setTheme(MainActivity.currentTheme[MainActivity.themeindex])

        //INITIALIZING BINDING
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnSel.setOnClickListener { finish() }

        binding.recyclerviewSel.setItemViewCacheSize(10)
        binding.recyclerviewSel.setHasFixedSize(true)
        binding.recyclerviewSel.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this, MainActivity.MusicList, selectionActivity = true)
        binding.recyclerviewSel.adapter = musicAdapter

        binding.searchviewSel.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            @SuppressLint("SetTextI18n")
            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
                if (newText != null) {
                    val userinput = newText.lowercase()
                    for (song in MainActivity.MusicList) {
                        if (song.title.lowercase().contains(userinput))
                            MainActivity.musicListSearch.add(song)
                        MainActivity.search = true
                        musicAdapter.updateMusicList(MainActivity.musicListSearch)
                    }
                }
                return true
            }
        })
    }
}