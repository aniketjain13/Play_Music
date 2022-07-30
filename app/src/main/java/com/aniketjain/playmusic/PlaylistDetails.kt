package com.aniketjain.playmusic

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aniketjain.playmusic.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetails : AppCompatActivity() {

    //VARIABLE INITIALIZATION
    lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var musicAdapter: MusicAdapter

    companion object{
        var currentPlaylistPos = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //SETTING THEME FOR THE ACTIVITY
        setTheme(MainActivity.currentTheme[MainActivity.themeindex])
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPlaylistPos = intent.extras?.getInt("index") as Int

        PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist = checkPlaylist(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist)

        //RECYCLER VIEW INITIALIZATION
        binding.recyclerviewPlaylistd.setItemViewCacheSize(10)
        binding.recyclerviewPlaylistd.setHasFixedSize(true)
        binding.recyclerviewPlaylistd.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this, PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist, true)
        binding.recyclerviewPlaylistd.adapter = musicAdapter

        binding.backBtnPd.setOnClickListener { finish() }

        binding.playlistShuffleBtnPd.setOnClickListener{
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }

        binding.playlistAddBtnPd.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }

        binding.playlistRemoveBtnPd.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove All").setMessage("Do you want to Remove all the songs from this playlist?")
                .setPositiveButton("YES") { dialog, _ ->
                    PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    musicAdapter.refreshPlaylist()
                    dialog.dismiss()
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePd.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name
        if(musicAdapter.itemCount > 0){
            binding.playlistShuffleBtnPd.visibility = View.VISIBLE
        }
        musicAdapter.notifyDataSetChanged()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("Playlists", jsonStringPlaylist)
        editor.apply()
    }
}