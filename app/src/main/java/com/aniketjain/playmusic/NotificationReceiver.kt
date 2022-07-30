package com.aniketjain.playmusic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS ->{
                prevNextSong(false, context!!)
            }
            ApplicationClass.PLAY ->{
                if(PlayerActivity.isplaying)
                    pauseMusic()
                else
                    playMusic()
            }
            ApplicationClass.NEXT ->{
                prevNextSong(true, context!!)
            }
            ApplicationClass.EXIT ->{
                PlayerActivity.musicService!!.stopForeground(true)
                PlayerActivity.musicService!!.mediaPlayer!!.release()
                PlayerActivity.musicService = null
                exitProcess(1)
            }
        }
    }

    private fun playMusic(){
        PlayerActivity.isplaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
        NowPlaying.binding.playBtnNp.setImageResource(R.drawable.pause_icon)
    }
    private fun pauseMusic(){
        PlayerActivity.isplaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.play_icon)
        NowPlaying.binding.playBtnNp.setImageResource(R.drawable.play_icon)
    }
    private fun prevNextSong(b: Boolean, context: Context) {
        setSongPosition(b)
        PlayerActivity.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(PlayerActivity.musiclistplayer[PlayerActivity.currentsong].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_note_icon).centerCrop())
            .into(PlayerActivity.binding.songImagePa)
        PlayerActivity.binding.songNamePa.text = PlayerActivity.musiclistplayer[PlayerActivity.currentsong].title
        Glide.with(context)
            .load(PlayerActivity.musiclistplayer[PlayerActivity.currentsong].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_note_icon).centerCrop())
            .into(NowPlaying.binding.songImageNp)
        NowPlaying.binding.songNameNp.text = PlayerActivity.musiclistplayer[PlayerActivity.currentsong].title
        playMusic()
        PlayerActivity.favindex = checkFavourite(PlayerActivity.musiclistplayer[PlayerActivity.currentsong].id)
        if(PlayerActivity.isfavourite)  PlayerActivity.binding.favBtnPa.setImageResource(R.drawable.favourite_icon)
        else PlayerActivity.binding.favBtnPa.setImageResource(R.drawable.favourite_empty_icon)
    }
}