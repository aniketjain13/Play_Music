package com.aniketjain.playmusic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aniketjain.playmusic.databinding.MusicViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

//ADAPTER TO STORE SONGS IN THE MAIN ACTIVITY
class MusicAdapter(private val context: Context, private var musiclist: ArrayList<MusicFile>, private var playlistDetails: Boolean = false,
                   private val selectionActivity: Boolean = false): RecyclerView.Adapter<MusicAdapter.MyVHolder>() {
    class MyVHolder(binding: MusicViewBinding): RecyclerView.ViewHolder(binding.root) {
        val title = binding.songName
        val artist = binding.songArtist
        val albumart = binding.songImage
        val duration = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVHolder {
        return MyVHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyVHolder, position: Int) {
        holder.title.text = musiclist[position].title
        holder.artist.text = musiclist[position].artist
        holder.duration.text = formatDuration(musiclist[position].duration)
        Glide.with(context)
            .load(musiclist[position].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_note_icon).centerCrop())
            .into(holder.albumart)
        when{
            //PLAYLIST DETAILS ADAPTER
            playlistDetails -> {
                holder.root.setOnClickListener {
                        sendIntent("PlaylistDetailsAdapter", position)
                }
            }
            //SELECTION ADAPTER
            selectionActivity -> {
                holder.root.setOnClickListener {
                    if(addSong(musiclist[position]))
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.holo_blue_dark))
                    else
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                }
            }
            else -> {
                holder.root.setOnClickListener{
                    when{
                        MainActivity.search -> sendIntent("MusicAdapterSearch", position)
                        musiclist[position].id == PlayerActivity.nowPlayingingid ->
                            sendIntent("nowplaying", position)
                        else-> sendIntent("MusicAdapter", position)
                    }
                }
            }
        }

    }

    //FUNCTION TO ADD SONG IN THE PLAYLIST
    private fun addSong(musicFile: MusicFile): Boolean {
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(musicFile.id == music.id){
                PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(musicFile)
        return true

    }

    override fun getItemCount(): Int {
        return musiclist.size
    }

    //FUNCTION TO UPDATE MUSIC LISTS
    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList: ArrayList<MusicFile>){
        musiclist = ArrayList()
        musiclist.addAll(searchList)
        notifyDataSetChanged()
    }

    //FUNCTION TO START PLAYER ACTIVITY
    private fun sendIntent(ref: String, position: Int){
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", position)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }

    //FUNCTION TO REFRESH A PLAYLIST
    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlaylist(){
        musiclist = ArrayList()
        musiclist = PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }
}