package com.kamus.practica1.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kamus.practica1.application.AlbumsDBApp
import com.kamus.practica1.data.AlbumRepository
import com.kamus.practica1.data.db.model.AlbumEntity
import com.kamus.practica1.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var albums: List<AlbumEntity> = emptyList()
    private lateinit var  repository: AlbumRepository
    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = (application as AlbumsDBApp).repository

        albumAdapter = AlbumAdapter() {
            album -> albumClicked(album)
        }

        binding.rvAlbum.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = albumAdapter
        }
        updateUI()
    }

    private fun updateUI(){
        lifecycleScope.launch {
            albums = repository.getAllAlbums()
            if(albums.isNotEmpty()){
                binding.tvEmpty.visibility = View.INVISIBLE
            }else{
                binding.tvEmpty.visibility = View.VISIBLE
            }
            albumAdapter.updateList(albums)
        }
    }

    fun click(view: View){
        val dialog = AlbumDialog(updateUI = {
            updateUI()
        }, message = { text ->
            message(text)
        })
        dialog.show(supportFragmentManager, "dialog")
    }



    private fun albumClicked(album: AlbumEntity){
        val dialog = AlbumDialog(newAlbum = false, album = album, updateUI = {
            updateUI()
        }, message = { text ->
            message(text)
        })
        dialog.show(supportFragmentManager, "dialog")
    }

    private fun message(text: String){
        Snackbar.make(binding.cl, text, Snackbar.LENGTH_SHORT)
            .setTextColor(Color.parseColor("#FFFFFF"))
            .setBackgroundTint(Color.parseColor("#9E1734"))
            .show()
    }
}