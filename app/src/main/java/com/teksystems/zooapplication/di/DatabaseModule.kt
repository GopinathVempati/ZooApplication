package com.teksystems.zooapplication.di

import android.content.Context
import androidx.room.Room
import com.teksystems.zooapplication.data.local.AnimalDao
import com.teksystems.zooapplication.data.local.AnimalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideAnimalDatabase(@ApplicationContext context: Context): AnimalDatabase =
        Room.databaseBuilder(context, AnimalDatabase::class.java, "animals.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideAnimalDao(db: AnimalDatabase): AnimalDao = db.animalDao()
}