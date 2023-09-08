package com.kamus.practica1.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kamus.practica1.R
import com.kamus.practica1.data.db.model.AlbumEntity
import com.kamus.practica1.databinding.AlbumElementBinding

class AlbumAdapter(private val onAlbumClick: (AlbumEntity) -> Unit): RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    private var albums: List<AlbumEntity> = emptyList()

    class ViewHolder(private val binding: AlbumElementBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(album: AlbumEntity){
            binding.apply {
                albumTitle.text = album.albumTitle
                albumArtist.text = album.albumArtist
                albumGenre.text = album.albumGenre
                albumYear.text = album.albumYear
                albumSongs.text = album.albumSongs
            }
            binding.albumImage.setImageResource(imageSelection(album.albumGenre))
        }

        fun imageSelection(image: String): Int{
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AlbumElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = albums.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albums[position])


        holder.itemView.setOnClickListener {
            onAlbumClick(albums[position])
        }
    }

    fun updateList(list: List<AlbumEntity>){
        albums = list
        notifyDataSetChanged()
    }



}