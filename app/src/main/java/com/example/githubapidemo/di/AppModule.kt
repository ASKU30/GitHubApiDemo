/*
 * Copyright 2024 Ashok Nayak.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.githubapidemo.di

import android.content.Context
import com.example.githubapidemo.network.ApiInterface
import com.example.githubapidemo.repository.GithubUserRepository
import com.example.githubapidemo.utils.Constants
import com.example.githubapidemo.utils.dispachperProvider.DispatcherProvider
import com.example.githubapidemo.utils.dispachperProvider.DispatcherProviderImpl
import com.example.githubapidemo.utils.networkhelper.NetworkHelper
import com.example.githubapidemo.utils.networkhelper.NetworkHelperImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun networkService(okHttpClient: OkHttpClient): ApiInterface {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(ApiInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .callTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)

        return okHttpClient.build()
    }

    @Provides
    @Singleton
    fun providesRepository(apiInterface: ApiInterface): GithubUserRepository =
        GithubUserRepository(apiInterface)

    @Provides
    @Singleton
    fun providesDispatcher(): DispatcherProvider = DispatcherProviderImpl()

    @Provides
    @Singleton
    fun providesNetworkHelper(
        @ApplicationContext context: Context
    ): NetworkHelper = NetworkHelperImpl(context)
}
