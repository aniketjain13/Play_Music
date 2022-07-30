package com.aniketjain.playmusic

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aniketjain.playmusic.databinding.FragmentNowPlayingBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NowPlaying : Fragment() {

    //VARIABLE DECLARATION
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeindex], true)
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.playBtnNp.setOnClickListener{
            if(PlayerActivity.isplaying)
                pauseMusic()
            else
                playMusic()
        }
        binding.root.setOnClickListener{
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.currentsong)
            intent.putExtra("class", "nowplaying")
            ContextCompat.startActivity(requireContext(), intent, null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(PlayerActivity.musicService != null){
            binding.root.visibility = View.VISIBLE
            binding.songNameNp.isSelected = true
            Glide.with(this)
                .load(PlayerActivity.musiclistplayer[PlayerActivity.currentsong].arturi)
                .apply(RequestOptions().placeholder(R.drawable.music_note_icon).centerCrop())
                .into(binding.songImageNp)
            binding.songNameNp.text = PlayerActivity.musiclistplayer[PlayerActivity.currentsong].title
            if(PlayerActivity.isplaying){
                binding.playBtnNp.setImageResource(R.drawable.pause_icon)
            }
            else{
                binding.playBtnNp.setImageResource(R.drawable.play_icon)
            }
        }
    }

    private fun playMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playBtnNp.setImageResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
        PlayerActivity.isplaying = true
    }
    private fun pauseMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playBtnNp.setImageResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.play_icon)
        PlayerActivity.isplaying = false
    }
}