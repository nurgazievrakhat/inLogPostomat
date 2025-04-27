package com.example.sampleusbproject.di

import com.example.sampleusbproject.data.remote.MainNetworkRepositoryImpl
import com.example.sampleusbproject.data.remote.socket.SocketRepositoryImpl
import com.example.sampleusbproject.domain.remote.MainNetworkRepository
import com.example.sampleusbproject.domain.remote.socket.SocketRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideAuthRepository(mainNetworkRepositoryImpl: MainNetworkRepositoryImpl): MainNetworkRepository
    @Binds
    abstract fun provideSocketRepository(socketRepositoryImpl: SocketRepositoryImpl): SocketRepository
}