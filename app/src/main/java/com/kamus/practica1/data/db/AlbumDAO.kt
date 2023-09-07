package com.kamus.practica1.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kamus.practica1.data.db.model.AlbumEntity
import com.kamus.practica1.util.Constants.DATABASE_ALBUM_TABLE

@Dao
interface AlbumDAO {
    /* CREATE */
    @Insert
    suspend fun insertAlbum(album: AlbumEntity)

    @Insert
    suspend fun insertAlbum(album: List<AlbumEntity>)

    /* READ */
    @Query("SELECT * FROM $DATABASE_ALBUM_TABLE")
    suspend fun getAllAlbums(): List<AlbumEntity>

    /* UPDATE */
    @Update
    suspend fun updateAlbum(album: AlbumEntity)

    /* DELETE */
    @Delete
    suspend fun deleteAlbum(album: AlbumEntity)
}