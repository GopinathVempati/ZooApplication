package com.teksystems.zooapplication.di

import android.content.Context
import androidx.room.Room
import com.teksystems.zooapplication.BuildConfig
import com.teksystems.zooapplication.data.local.AnimalDao
import com.teksystems.zooapplication.data.local.AnimalDatabase
import com.teksystems.zooapplication.data.remote.AnimalApiService
import com.teksystems.zooapplication.data.repository.AnimalRepositoryImpl
import com.teksystems.zooapplication.domain.repository.AnimalRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val headerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("X-Api-Key", BuildConfig.API_KEY)
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(newRequest)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headerInterceptor)
            .build()
    }

    @Provides @Singleton
    fun provideAnimalApiService(client: OkHttpClient): AnimalApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnimalApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideAnimalDatabase(@ApplicationContext context: Context): AnimalDatabase {
        return Room.databaseBuilder(context, AnimalDatabase::class.java, "animals.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAnimalDao(db: AnimalDatabase): AnimalDao {
        return db.animalDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindAnimalRepository(
        impl: AnimalRepositoryImpl
    ): AnimalRepository
}
