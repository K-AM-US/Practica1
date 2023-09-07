package com.kamus.practica1.data

import com.kamus.practica1.data.db.AlbumDAO
import com.kamus.practica1.data.db.model.AlbumEntity

class AlbumRepository(private val albumDAO: AlbumDAO) {

    suspend fun insertAlbum(album: AlbumEntity){
        albumDAO.insertAlbum(album)
    }

    suspend fun insertAlbum(title: String, artist: String, year: String, songs: Int, genre: String){
        albumDAO.insertAlbum(AlbumEntity(albumTitle = title, albumArtist = artist, albumYear = year, albumSongs = songs, albumGenre = genre))
    }

    suspend fun getAllAlbums(): List<AlbumEntity> = albumDAO.getAllAlbums()

    suspend fun updateAlbum(album: AlbumEntity){
        albumDAO.updateAlbum(album)
    }

    suspend fun deleteAlbum(album: AlbumEntity){
        albumDAO.deleteAlbum(album)
    }
}