package com.kamus.practica1.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kamus.practica1.data.db.model.AlbumEntity
import com.kamus.practica1.util.Constants

@Database(
    entities = [AlbumEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AlbumDatabase: RoomDatabase() {

    abstract fun albumDao(): AlbumDAO

    companion object{
        @Volatile
        private var INSTANCE: AlbumDatabase? = null

        fun getDatabase(context: Context): AlbumDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlbumDatabase::class.java,
                    Constants.DATABASE_NAME
                ).fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}