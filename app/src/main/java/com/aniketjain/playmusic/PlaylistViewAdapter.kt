package com.aniketjain.playmusic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aniketjain.playmusic.databinding.PlaylistViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

//CLASS TO HOLD THE PLAYLISTS
class PlaylistViewAdapter(private val context: Context, private var playlistlist: ArrayList<Playlist>): RecyclerView.Adapter<PlaylistViewAdapter.MyVHolder>() {
    class MyVHolder(binding: PlaylistViewBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.songImagePlaylist
        val name = binding.songNamePlaylist
        val root = binding.root
        val delete = binding.deleteBtnPlaylist
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVHolder {
        return MyVHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyVHolder, position: Int) {
        holder.name.text = playlistlist[position].name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistlist[position].name).setMessage("Do you want to delete this playlist?")
                .setPositiveButton("YES") { dialog, _ ->
                    PlaylistActivity.musicPlaylist.ref.removeAt(position)
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
        holder.root.setOnClickListener{
            val intent = Intent(context, PlaylistDetails::class.java)
            intent.putExtra("index", position)
            ContextCompat.startActivity(context, intent, null)
        }
        if(PlaylistActivity.musicPlaylist.ref[position].playlist.size > 0){
            Glide.with(context)
                .load(PlaylistActivity.musicPlaylist.ref[position].playlist[0].arturi)
                .apply(RequestOptions().placeholder(R.drawable.music_note_icon).centerCrop())
                .into(holder.image)
        }
   }

    override fun getItemCount(): Int {
        return playlistlist.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlaylist(){
        playlistlist = ArrayList()
        playlistlist.addAll(PlaylistActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }
}