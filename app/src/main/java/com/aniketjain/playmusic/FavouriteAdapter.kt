package com.aniketjain.playmusic

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aniketjain.playmusic.databinding.MusicViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

//ADAPTER TO STORE THE SONGS IN FAVOURITE ACTIVITY
class FavouriteAdapter(private val context: Context, private var musiclist: ArrayList<MusicFile>): RecyclerView.Adapter<FavouriteAdapter.MyVHolder>() {
    class MyVHolder(binding: MusicViewBinding): RecyclerView.ViewHolder(binding.root) {
        //VARIABLE REFERENCING
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
        holder.root.setOnClickListener{
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "FavouriteActivity")
                ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return musiclist.size
    }
}