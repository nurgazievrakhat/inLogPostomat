package com.example.sampleusbproject.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.sampleusbproject.BuildConfig.URL_BASE
import com.example.sampleusbproject.data.PostomatInfoMapper
import com.example.sampleusbproject.data.local.AppDatabase
import com.example.sampleusbproject.data.local.dao.PostomatInfoDao
import com.example.sampleusbproject.data.remote.BaseService
import com.example.sampleusbproject.data.remote.PostomatService
import com.example.sampleusbproject.data.remote.interceptors.TokenInterceptor
import com.example.sampleusbproject.domain.interfaces.LockerBoardInterface
import com.example.sampleusbproject.domain.remote.socket.SocketRepository
import com.example.sampleusbproject.lockBoards.LockBoardFactory
import com.example.sampleusbproject.usecases.PostomatSocketUseCase
import com.example.sampleusbproject.utils.CommonPrefs
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
    @Named("Base")
    fun provideBaseOkHttpClint(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    @Named("Authorized")
    fun provideAuthOkHttpClint(tokenInterceptor: TokenInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    @Singleton
    fun providePostomatService(
        @Named("Authorized") okHttpClient: OkHttpClient
    ): PostomatService {
        return createRetrofitClient(URL_BASE, okHttpClient).create(PostomatService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(@Named("Base") okHttpClient: OkHttpClient): BaseService {
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
    fun providePostomatInfoMapper(postomatInfoDao: PostomatInfoDao): PostomatInfoMapper {
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

    @Provides
    @Singleton
    fun provideBoard(@ApplicationContext context: Context): LockerBoardInterface {
        return LockBoardFactory.createBoard(context, LockBoardFactory.BOARD_TYPE_NEW)
    }

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