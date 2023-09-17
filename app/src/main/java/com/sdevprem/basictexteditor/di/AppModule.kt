package com.sdevprem.basictexteditor.di

import android.content.Context
import androidx.room.Room
import com.sdevprem.basictexteditor.common.provider.DrawableProvider
import com.sdevprem.basictexteditor.common.provider.DrawableProviderImpl
import com.sdevprem.basictexteditor.data.db.NotesDB
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class AppModule {

    companion object {
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

    @Singleton
    @Binds
    abstract fun provideDrawableProvider(provider: DrawableProviderImpl): DrawableProvider
}