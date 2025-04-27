package com.example.sampleusbproject.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.work.WorkManager
import com.example.sampleusbproject.BuildConfig.URL_BASE
import com.example.sampleusbproject.data.PostomatInfoMapper
import com.example.sampleusbproject.data.local.AppDatabase
import com.example.sampleusbproject.data.local.dao.PostomatInfoDao
import com.example.sampleusbproject.data.remote.BaseService
import com.example.sampleusbproject.domain.remote.socket.SocketRepository
import com.example.sampleusbproject.domain.remote.socket.model.PostomatInfo
import com.example.sampleusbproject.usecases.PostomatSocketUseCase
import com.example.sampleusbproject.utils.CommonPrefs
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    private val prefName = "AppPref"

    @Provides
    fun provideBaseOkHttpClint(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY ))
            .build()
    }
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): BaseService {
        return createRetrofitClient(URL_BASE, okHttpClient)
            .create(BaseService::class.java)
    }

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideTokenPreference(sharedPreferences: SharedPreferences): CommonPrefs {
        return CommonPrefs(sharedPreferences)
    }

    @Provides
    @Singleton
    fun providePostomatSocketUseCase(socketRepository: SocketRepository): PostomatSocketUseCase {
        return PostomatSocketUseCase(socketRepository)
    }

    @Provides
    @Singleton
    fun providePostomatInfoMapper(postomatInfoDao: PostomatInfoDao) : PostomatInfoMapper {
        return PostomatInfoMapper(postomatInfoDao)
    }
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app-database"
        ).build()

    @Provides
    @Singleton
    fun providePostomatInfoDao(
        database: AppDatabase
    ) = database.postomatInfoDao()

    private fun createRetrofitClient(
        baseUrl: String,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

}