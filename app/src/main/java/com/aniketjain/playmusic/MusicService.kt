package com.aniketjain.playmusic

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

class MusicService: Service(), AudioManager.OnAudioFocusChangeListener{

    //VARIABLE DECLARATION
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(p0: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder:Binder(){
        fun currentservice(): MusicService{
            return this@MusicService
        }
    }

    //FUNCTION TO SHOW NOTIFICATION ONCE SONG START PLAYING
    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playpausebtn: Int){

        val intent = Intent(baseContext, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val previntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevpendingintent = PendingIntent.getBroadcast(baseContext, 0, previntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playintent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playpendingintent = PendingIntent.getBroadcast(baseContext, 0, playintent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextintent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextpendingintent = PendingIntent.getBroadcast(baseContext, 0, nextintent, PendingIntent.FLAG_UPDATE_CURRENT)

        val exitintent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitpendingintent = PendingIntent.getBroadcast(baseContext, 0, exitintent, PendingIntent.FLAG_UPDATE_CURRENT)

        val imageart = getImageArt(PlayerActivity.musiclistplayer[PlayerActivity.currentsong].path)
        val image = if(imageart != null)
                        BitmapFactory.decodeByteArray(imageart, 0, imageart.size)
                    else
                        BitmapFactory.decodeResource(resources, R.drawable.earbuds)

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.channel_id)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayerActivity.musiclistplayer[PlayerActivity.currentsong].title)
            .setContentText(PlayerActivity.musiclistplayer[PlayerActivity.currentsong].artist)
            .setSmallIcon(R.drawable.earbuds)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous_icon, "Previous", prevpendingintent)
            .addAction(playpausebtn, "PlayPause", playpendingintent)
            .addAction(R.drawable.next_icon, "Next", nextpendingintent)
            .addAction(R.drawable.exit_icon, "Exit", exitpendingintent)
            .build()

        startForeground(13, notification)
    }

    //FUNCTION TO CREATE THE MEDIA PLAYER
    fun createMediaPlayer(){
        try{
            if(PlayerActivity.musicService!!.mediaPlayer == null)
                PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musiclistplayer[PlayerActivity.currentsong].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            PlayerActivity.binding.startTextPa.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.endTextPa.text = formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekbarPa.progress = 0
            PlayerActivity.binding.seekbarPa.max = PlayerActivity.musicService!!.mediaPlayer!!.duration
            PlayerActivity.nowPlayingingid = PlayerActivity.musiclistplayer[PlayerActivity.currentsong].id
        }
        catch (e: Exception){
            return
        }
    }

    //FUNCTION TO SETUP THE SEEKBAR
    fun seekBarSetup(){
        runnable = Runnable {
            PlayerActivity.binding.startTextPa.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekbarPa.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    //FUNCTION TO HANDLE FOCUS CHANGE
    override fun onAudioFocusChange(focusChange: Int) {
        if(focusChange <= 0){
            PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.play_icon)
            NowPlaying.binding.playBtnNp.setImageResource(R.drawable.pause_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
            PlayerActivity.isplaying = false
            mediaPlayer!!.pause()
        }
        else{
            PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
            NowPlaying.binding.playBtnNp.setImageResource(R.drawable.play_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            PlayerActivity.isplaying = true
            mediaPlayer!!.start()
        }
    }
}