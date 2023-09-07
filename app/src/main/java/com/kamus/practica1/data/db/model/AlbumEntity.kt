package com.kamus.practica1.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kamus.practica1.util.Constants

@Entity(tableName = Constants.DATABASE_ALBUM_TABLE)
data class AlbumEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "album_id")
    val albumId: Long = 0,

    @ColumnInfo(name = "album_title")
    var albumTitle: String,

    @ColumnInfo(name = "album_artist")
    var albumArtist: String,

    @ColumnInfo(name = "album_year")
    var albumYear: String,

    @ColumnInfo(name = "album_songs")
    var albumSongs: Int,

    @ColumnInfo(name = "album_genre")
    var albumGenre: String
)
