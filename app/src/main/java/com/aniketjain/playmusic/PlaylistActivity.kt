package com.aniketjain.playmusic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aniketjain.playmusic.databinding.ActivityPlaylistBinding
import com.aniketjain.playmusic.databinding.AddPlaylistDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistActivity : AppCompatActivity() {

    //VARIABLE DECLARATION
    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var playlistAdapter: PlaylistViewAdapter
    
    companion object{
        var musicPlaylist= MusicPlaylist()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //SETTING THEME FOR THE ACTIVITY
        setTheme(MainActivity.currentTheme[MainActivity.themeindex])
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //RECYCLER VIEW INITIALIZATION
        binding.recyclerviewPlaylist.setHasFixedSize(true)
        binding.recyclerviewPlaylist.setItemViewCacheSize(10)
        binding.recyclerviewPlaylist.layoutManager = GridLayoutManager(this@PlaylistActivity, 2)
        playlistAdapter = PlaylistViewAdapter(this, musicPlaylist.ref)
        binding.recyclerviewPlaylist.adapter = playlistAdapter

        binding.backBtnPla.setOnClickListener { finish() }
        binding.playlistAddBtn.setOnClickListener{ customAlertDialog() }
    }

    //FUNCTION TO SHOW ALERT DIALOG
    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this).inflate(R.layout.add_playlist_dialog, binding.root, false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("ADD") { dialog, _ ->
                val playlistname = binder.playlistName.text
                if(playlistname != null){
                    if(playlistname.isNotEmpty()){
                        addPlaylist(playlistname.toString())
                    }
                }
                dialog.dismiss()
                }.show()
    }

    //FUNCTION TO ADD PLAYLIST
    private fun addPlaylist(name: String) {
        var playlistexists = false
        for(i in musicPlaylist.ref){
            if(name == i.name) {
                playlistexists = true
                break
            }
        }
        if(playlistexists)
            Toast.makeText(this, "Playlist Exists", Toast.LENGTH_SHORT).show()
        else{
            val tempplaylist = Playlist()
            tempplaylist.name = name
            tempplaylist.playlist = ArrayList()
            musicPlaylist.ref.add(tempplaylist)
            playlistAdapter.refreshPlaylist()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        playlistAdapter.notifyDataSetChanged()
    }
}