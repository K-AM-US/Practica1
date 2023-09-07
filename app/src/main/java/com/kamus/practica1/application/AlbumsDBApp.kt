package com.kamus.practica1.application

import android.app.Application
import com.kamus.practica1.data.AlbumRepository
import com.kamus.practica1.data.db.AlbumDatabase

class AlbumsDBApp(): Application() {

    private val database by lazy{
        AlbumDatabase.getDatabase(this@AlbumsDBApp)
    }

    val repository by lazy{
        AlbumRepository(database.albumDao())
    }

}