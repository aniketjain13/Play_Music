@file:Suppress("DEPRECATION")

package com.aniketjain.playmusic

import android.media.MediaMetadataRetriever
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

//GLOBAL FILE CONTAINING ALL THE GLOBAL CLASSES AND FUNCTIONS

//CLASS TO STORE DETAILS OF THE SONG
data class MusicFile(val id:String, val title:String, val album:String, val artist:String, val duration:Long = 0, val path:String, val arturi: String)

//CLASS TO STORE DETAILS OF THE PLAYLIST
class Playlist{
    lateinit var name: String
    lateinit var playlist: ArrayList<MusicFile>
}

//CLASS TO STORE PLAYLISTS
class MusicPlaylist{
    var ref: ArrayList<Playlist> = ArrayList()
}

//FUNCTION TO FORMAT THE DURATION OF THE SONG
fun formatDuration(duration: Long):String{
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes*TimeUnit.SECONDS.convert(1,  TimeUnit.MINUTES))
    return String.format("%02d:%02d", minutes, seconds)
}

//FUNCTION TO EXIT APPLICATION
fun exitApplication(){
    if(!PlayerActivity.isplaying && PlayerActivity.musicService != null) {
        PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        PlayerActivity.musicService = null
    }
    exitProcess(1)
}

//FUNCTION TO GET THE ALBUM ART FROM THE SONG
fun getImageArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

//FUNCTION TO DETERMINE THE SONG TO BE PLAYED BY RETURNING A POSITION FOR THE LIST
fun setSongPosition(b: Boolean){
    if(!PlayerActivity.repeat) {
        if (b) {
            if (PlayerActivity.currentsong == PlayerActivity.musiclistplayer.size - 1)
                PlayerActivity.currentsong = 0
            else
                ++PlayerActivity.currentsong
        } else {
            if (PlayerActivity.currentsong == 0)
                PlayerActivity.currentsong = PlayerActivity.musiclistplayer.size - 1
            else
                --PlayerActivity.currentsong
        }
    }
}

//FUNCTION TO CHECK WHETHER THE SONG IS IN FAVOURITES LIST OR NOT
fun checkFavourite(id: String): Int{
    PlayerActivity.isfavourite = false
    FavouriteActivity.favouriteSongs.forEachIndexed{ index, musicFile ->
        if(id == musicFile.id){
            PlayerActivity.isfavourite = true
            return index
        }
    }
    return -1
}

//FUNCTION TO CHECK PLAYLIST EXISTENCE
fun checkPlaylist(playlist: ArrayList<MusicFile>): ArrayList<MusicFile>{
    playlist.forEachIndexed { index, music ->
        val file = File(music.path)
        if(!file.exists())
            playlist.removeAt(index)
    }
    return playlist
}