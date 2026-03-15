package com.teksystems.zooapplication.di

import com.teksystems.zooapplication.data.repository.AnimalRepositoryImpl
import com.teksystems.zooapplication.domain.repository.AnimalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAnimalRepository(impl: AnimalRepositoryImpl): AnimalRepository
}