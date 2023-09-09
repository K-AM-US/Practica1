package com.kamus.practica1.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kamus.practica1.R
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

        albumAdapter = AlbumAdapter( {
            album -> albumClicked(album)
        },{ image ->
            imageSelection(image)
        })

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
        }, imageSelection = {image ->
            imageSelection(image)
        })
        dialog.show(supportFragmentManager, "dialog")
    }

    private fun albumClicked(album: AlbumEntity){
        val dialog = AlbumDialog(newAlbum = false, album = album, updateUI = {
            updateUI()
        }, message = { text ->
            message(text)
        }, imageSelection = { image ->
            imageSelection(image)
        }
            )
        dialog.show(supportFragmentManager, "dialog")
    }

    private fun message(text: String){
        Snackbar.make(binding.cl, text, Snackbar.LENGTH_SHORT)
            .setTextColor(getColor(R.color.messageTextColor))
            .setBackgroundTint(getColor(R.color.messageBackground))
            .show()
    }

    private fun imageSelection(image: String): Int{
        return when(image){
            "Brit Pop" -> R.drawable.britpop
            "Rock" -> R.drawable.rock
            "Metal" -> R.drawable.metal
            "House" -> R.drawable.house
            "Electronic" -> R.drawable.electronic
            "Indie" -> R.drawable.indie
            "Jazz" -> R.drawable.jazz
            else -> R.drawable.headphones
        }
    }
}