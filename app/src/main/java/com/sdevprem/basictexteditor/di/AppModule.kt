package com.sdevprem.basictexteditor.di

import android.content.Context
import androidx.room.Room
import com.sdevprem.basictexteditor.data.db.NotesDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            NotesDB::class.java,
            "NotesDB"
        ).build()

    @Singleton
    @Provides
    fun provideNotesDao(db: NotesDB) = db.getNotesDao()
}