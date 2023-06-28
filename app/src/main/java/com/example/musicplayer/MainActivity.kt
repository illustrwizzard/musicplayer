package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import android.Manifest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var audioList: MutableList<AudioItem>
    private lateinit var adapter: ArrayAdapter<AudioItem>
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        private const val REQUEST_PERMISSION_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.listView)
        audioList = mutableListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, audioList)
        listView.adapter = adapter

        if (isReadStoragePermissionGranted()) {
            fetchAudioFiles()
        } else {
            requestStoragePermission()
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val audioItem = audioList[position]
            Toast.makeText(this, "Playing: ${audioItem.title}", Toast.LENGTH_SHORT).show()
            playAudio(audioItem.path)
        }
    }

    @SuppressLint("Range")
    private fun fetchAudioFiles() {
        val contentResolver = contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = contentResolver.query(uri, null, selection, null, sortOrder)

        cursor?.use {
            while (it.moveToNext()) {
                val title = it.getString(it.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = it.getString(it.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val duration = it.getLong(it.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val path = it.getString(it.getColumnIndex(MediaStore.Audio.Media.DATA))

                val audioItem = AudioItem(title, artist, duration, path)
                audioList.add(audioItem)
            }
        }

        adapter.notifyDataSetChanged()
    }

    private fun isReadStoragePermissionGranted(): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAudioFiles()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied. Unable to fetch audio files.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun playAudio(path: String) {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(path)
            prepare()
            start()
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}