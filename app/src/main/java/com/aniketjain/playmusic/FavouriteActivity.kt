package com.aniketjain.playmusic

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aniketjain.playmusic.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {

    //VARIABLE DECLARATION
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var musicAdapter: FavouriteAdapter

    companion object{
        var favouriteSongs: ArrayList<MusicFile> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //SETTING THEME OF THE ACTIVITY
        setTheme(MainActivity.currentTheme[MainActivity.themeindex])

        //BINDING INITIALIZATION
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //CHECKING THE FAVOURITE DATA
        favouriteSongs = checkPlaylist(favouriteSongs)

        binding.backBtnFa.setOnClickListener { finish() }

        //RECYCLER VIEW INITIALIZATION
        binding.recyclerviewFavourite.setHasFixedSize(true)
        binding.recyclerviewFavourite.setItemViewCacheSize(10)
        binding.recyclerviewFavourite.layoutManager = LinearLayoutManager(this)
        musicAdapter = FavouriteAdapter(this, favouriteSongs)
        binding.recyclerviewFavourite.adapter = musicAdapter
        if(favouriteSongs.size < 1)
            binding.shuffleBtnFav.visibility = View.INVISIBLE
        else
            binding.shuffleBtnFav.visibility = View.VISIBLE

        binding.shuffleBtnFav.setOnClickListener{
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavouriteShuffle")
            startActivity(intent)
        }
    }
}