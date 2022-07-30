package com.aniketjain.playmusic

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aniketjain.playmusic.databinding.ActivityPlayerBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object{
        lateinit var musiclistplayer: ArrayList<MusicFile>
        var currentsong = 0
        var isplaying = false
        var musicService: MusicService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeat = false
        var nowPlayingingid = ""
        var isfavourite = false
        var favindex = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeindex])
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeLayout()
        binding.playPauseBtn.setOnClickListener{
            if(isplaying)
                pauseMusic()
            else
                playMusic()
        }
        binding.previousBtn.setOnClickListener{ prevNextSong(false)}
        binding.nextBtn.setOnClickListener{ prevNextSong(true)}
        binding.seekbarPa.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromuser: Boolean) {
                if(fromuser){
                    musicService!!.mediaPlayer!!.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
        binding.repeatBtn.setOnClickListener{
            if(!repeat){
                repeat = true
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.holo_blue_dark))
            }
            else{
                repeat = false
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.black))
            }
        }
        binding.backButtonPa.setOnClickListener { finish() }
        binding.favBtnPa.setOnClickListener{
            if(isfavourite){
                binding.favBtnPa.setImageResource(R.drawable.favourite_empty_icon)
                isfavourite = false
                FavouriteActivity.favouriteSongs.removeAt(favindex)
            }
            else{
                binding.favBtnPa.setImageResource(R.drawable.favourite_icon)
                isfavourite = true
                FavouriteActivity.favouriteSongs.add(musiclistplayer[currentsong])
                favindex = checkFavourite(musiclistplayer[currentsong].id)
            }
        }
    }

    private fun initializeLayout(){
        currentsong = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){
            "FavouriteActivity"->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistplayer = ArrayList()
                musiclistplayer.addAll(FavouriteActivity.favouriteSongs)
                setLayout()
            }
            "MusicAdapterSearch"->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistplayer = ArrayList()
                musiclistplayer.addAll(MainActivity.musicListSearch)
                setLayout()
            }
            "MusicAdapter" ->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistplayer = ArrayList()
                musiclistplayer.addAll(MainActivity.MusicList)
                setLayout()
            }
            "MainActivity"->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistplayer = ArrayList()
                musiclistplayer.addAll(MainActivity.MusicList)
                musiclistplayer.shuffle()
                setLayout()
            }
            "FavouriteShuffle"->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistplayer = ArrayList()
                musiclistplayer.addAll(FavouriteActivity.favouriteSongs)
                musiclistplayer.shuffle()
                setLayout()
            }
            "nowplaying"->{
                setLayout()
                binding.startTextPa.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.endTextPa.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekbarPa.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekbarPa.max = musicService!!.mediaPlayer!!.duration
                if(isplaying)   binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
                else    binding.playPauseBtn.setImageResource(R.drawable.play_icon)
            }
            "PlaylistDetailsAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistplayer = ArrayList()
                musiclistplayer.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                setLayout()
            }
            "PlaylistDetailsShuffle" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistplayer = ArrayList()
                musiclistplayer.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                musiclistplayer.shuffle()
                setLayout()
            }
        }

    }

    private fun setLayout(){

        favindex = checkFavourite(musiclistplayer[currentsong].id)
        Glide.with(baseContext)
            .load(musiclistplayer[currentsong].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_note_icon).centerCrop())
            .into(binding.songImagePa)
        binding.songNamePa.text = musiclistplayer[currentsong].title
        if(repeat)
            binding.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.holo_blue_dark))

        if(musicService != null){
            NowPlaying.binding.root.visibility = View.VISIBLE
            NowPlaying.binding.songNameNp.isSelected = true
            Glide.with(baseContext)
                .load(musiclistplayer[currentsong].arturi)
                .apply(RequestOptions().placeholder(R.drawable.music_note_icon).centerCrop())
                .into(NowPlaying.binding.songImageNp)
            NowPlaying.binding.songNameNp.text = musiclistplayer[currentsong].title
            if(isplaying){
                NowPlaying.binding.playBtnNp.setImageResource(R.drawable.pause_icon)
            }
            else{
                NowPlaying.binding.playBtnNp.setImageResource(R.drawable.play_icon)
            }
        }
        if(isfavourite){
            binding.favBtnPa.setImageResource(R.drawable.favourite_icon)
        }
        else{
            binding.favBtnPa.setImageResource(R.drawable.favourite_empty_icon)
        }

    }

    private fun createMediaPlayer(){
        try{
            if(musicService!!.mediaPlayer == null)
                musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musiclistplayer[currentsong].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isplaying = true
            binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon)
            binding.startTextPa.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.endTextPa.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekbarPa.progress = 0
            binding.seekbarPa.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingingid = musiclistplayer[currentsong].id
        }
        catch (e: Exception){
            return
        }
    }

    private fun playMusic(){
        binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.pause_icon)
        isplaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playPauseBtn.setImageResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon)
        isplaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNextSong(b: Boolean) {
        setSongPosition(b)
        setLayout()
        createMediaPlayer()
    }

    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentservice()
        createMediaPlayer()
        musicService!!.seekBarSetup()
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(true)
        setLayout()
        createMediaPlayer()
    }
}