package com.aniketjain.playmusic

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aniketjain.playmusic.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    // VARIABLE DECLARATION
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object{
        lateinit var MusicList: ArrayList<MusicFile>
        lateinit var musicListSearch: ArrayList<MusicFile>
        var search = false
        var themeindex = 0
        val currentTheme = arrayOf(R.style.holo_blue_dark, R.style.purple_500, R.style.cool_green, R.style.cool_pink, R.style.black)
        val currentThemeNav = arrayOf(R.style.holo_blue_dark_nav, R.style.purple_500_nav, R.style.cool_green_nav
            , R.style.cool_pink_nav, R.style.black_nav)
        val currentGradient = arrayOf(R.drawable.gradient_blue, R.drawable.gradient_purple, R.drawable.gradient_green
                ,R.drawable.gradient_pink, R.drawable.gradient_black)
        var sortOrder = 0
        var sortMethods = arrayOf(MediaStore.Audio.Media.DATE_ADDED + " DESC"
            , MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.SIZE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //FUNCTION TO REQUEST PERMISSION DURING RUNTIME
        requestRuntimePermission()

        //FOR RETRIEVING THE CURRENT THEME USING SHARED PREFERENCES
        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeindex = themeEditor.getInt("themeindex", 0)
        setTheme(currentThemeNav[themeindex])


        //BINDING INITIALIZATION
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //NAVIGATION DRAWER INITIALIZATION
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //INITIALIZING THE LAYOUT
        initializeLayout()

        //FOR RETRIEVING FAVOURITE SONGS DATA USING SHARED PREFERENCES
        FavouriteActivity.favouriteSongs = ArrayList()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
        val jsonString = editor.getString("Favouritesongs", null)
        val typetoken = object: TypeToken<ArrayList<MusicFile>>(){}.type
        if(jsonString != null){
            val data: ArrayList<MusicFile> = GsonBuilder().create().fromJson(jsonString, typetoken)
            FavouriteActivity.favouriteSongs.addAll(data)
        }

        //FOR RETRIEVING PLAYLIST DATA USING SHARED PREFERENCES
        PlaylistActivity.musicPlaylist = MusicPlaylist()
        val jsonStringPlaylist = editor.getString("Playlists", null)
        if(jsonStringPlaylist != null){
            val dataPlaylist: MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlaylist::class.java)
            PlaylistActivity.musicPlaylist = dataPlaylist
        }

        //SHUFFLE BUTTON
        binding.shuffleBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }

        //FAVOURITE BUTTON
        binding.favouriteBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, FavouriteActivity::class.java)
            startActivity(intent)
        }

        //PLAYLIST BUTTON
        binding.playlistButton.setOnClickListener {
            val intent = Intent(this@MainActivity, PlaylistActivity::class.java)
            startActivity(intent)
        }

        //NAV DRAWER
        binding.navDrawer.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navFeedback -> {  //FEEDBACK ACTIVITY
                    startActivity(Intent(this, FeedbackActivity::class.java))
                }
                R.id.navSettings -> {   //SETTINGS ACTIVITY
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.navAbout -> {      //ABOUT ACTIVITY
                    startActivity(Intent(this, AboutActivity::class.java))
                }
                R.id.navExit -> {
                    //ALERT DIALOG TO CONFIRM
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit").setMessage("Do you want to Exit app?")
                        .setPositiveButton("YES") { _, _ ->
                            if(PlayerActivity.musicService != null) {
                                PlayerActivity.musicService!!.stopForeground(true)
                                PlayerActivity.musicService!!.mediaPlayer!!.release()
                                PlayerActivity.musicService = null
                            }
                            exitProcess(1)
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
            true
        }

    }

    //NAV HEADER FUNCTION
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        findViewById<LinearLayout>(R.id.navheader)?.setBackgroundResource(currentGradient[themeindex])

        //SEARCH VIEW
        val searchView = menu?.findItem(R.id.search_view)?.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            @SuppressLint("SetTextI18n")
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if(newText != null){
                    val userinput = newText.lowercase()
                    for(song in MusicList){
                        if(song.title.lowercase().contains(userinput))
                            musicListSearch.add(song)
                        search = true
                        musicAdapter.updateMusicList(musicListSearch)
                        binding.totalSongs.text = resources.getString(R.string.total_songs) + musicAdapter.itemCount
                    }
                }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    //FUNCTION TO INITIALIZE THE LAYOUT OF THE MAIN ACTIVITY
    @SuppressLint("SetTextI18n")
    private fun initializeLayout() {

        search = false

        //SORTING PREFERENCES
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        sortOrder = sortEditor.getInt("sortOrder", 0)

        //MUSIC LIST INITIALIZATION
        MusicList = getAllAudioFiles()

        //RECYCLER VIEW INITIALIZATION
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.setItemViewCacheSize(10)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this, MusicList)
        binding.recyclerview.adapter = musicAdapter

        binding.totalSongs.text = resources.getString(R.string.total_songs) + musicAdapter.itemCount
    }

    //NAV HEADER SELECTION FUNCTION
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    // FUNCTION FOR REQUESTING RUNTIME PERMISSION
    private fun requestRuntimePermission(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    //FUNCTION TO HANDLE THE RESULT AFTER REQUESTING PERMISSION
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initializeLayout()
            else
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    //FUNCTION TO GET SONGS FROM THE DEVICE
    @SuppressLint("Recycle", "Range")
    private fun getAllAudioFiles(): ArrayList<MusicFile>{
        val templist = ArrayList<MusicFile>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_ADDED
            , MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection,
        null, sortMethods[sortOrder], null)
        if(cursor != null) {
            if (cursor.moveToFirst())
                do {
                    val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)).toLong()
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val albumid = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()

                    //SETTING URI TO GET THE ALBUM ART OF TH SONG
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val arturi = Uri.withAppendedPath(uri, albumid).toString()

                    val musicFile = MusicFile(id, title, album, artist, duration, path, arturi)

                    //TO CHECK WHETHER THE SONG EXIST OR NOT
                    val file = File(musicFile.path)
                    if(file.exists()) {
                        //ONLY ADD SONGS IN THE LIST BY REMOVING THE CALL RECORDINGS
                        if (!path.contains("sound_rec"))
                            templist.add(musicFile)
                    }
                } while (cursor.moveToNext())
            cursor.close()
        }
        return templist
    }

    //FUNCTION EXECUTES AS SOON AS THE APP IS CLOSED
    override fun onDestroy() {
        super.onDestroy()
        exitApplication()
    }

    //FUNCTION EXECUTES AFTER THE ACTIVITY RESUMES
    override fun onResume() {
        super.onResume()

        //FOR STORING FAVOURITE AND PLAYLIST DATA USING SHARED PREFERENCES
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.favouriteSongs)
        editor.putString("Favouritesongs", jsonString)
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("Playlists", jsonStringPlaylist)
        editor.apply()

        //FOR STORING THE SORTING DATA USING SHARED PREFERENCES
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        val sortVal = sortEditor.getInt("sortOrder", 0)
        if(sortVal != sortOrder){
            sortOrder = sortVal
            MusicList = getAllAudioFiles()
            musicAdapter.updateMusicList(MusicList)
        }
    }
}