package com.food.toprecipes.di

import android.content.Context
import android.util.Log
import com.food.toprecipes.ApiService
import com.food.toprecipes.ApiService.Companion.BASE_URL
import com.food.toprecipes.data.BuildConfig
import com.food.toprecipes.repository.SpoonacularRepositoryImpl
import com.food.toprecipes.repository.SpoonacularRepository
import com.food.toprecipes.remotedata.DomainErrorMapper
import com.food.toprecipes.spoonacularapi.SpoonacularRemoteDataSource
import com.food.toprecipes.spoonacularapi.SpoonacularRemoteDataSourceImp
import com.food.toprecipes.spoonacularapi.StringProvider
import com.food.toprecipes.spoonacularapi.StringProviderImpl
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor { message ->
            Log.d("okHttpLog", message)
        }.apply {
            setLevel(
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BASIC
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            )
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitApi(client: OkHttpClient): ApiService {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    fun provideStringProvider(@ApplicationContext appContext: Context): StringProvider {
        return StringProviderImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideDomainErrorMapper(stringProvider: StringProvider): com.food.toprecipes.remotedata.DomainErrorMapper {
        return DomainErrorMapper(stringProvider)
    }

    @Provides
    @Singleton
    fun provideSpoonacularRemoteDataSource(
        api: ApiService,
        errorMapper: DomainErrorMapper
    ): SpoonacularRemoteDataSource {
        return SpoonacularRemoteDataSourceImp(api, errorMapper)
    }

    @Provides
    @Singleton
    fun provideSpoonacularRepository(
        remoteDataSource: SpoonacularRemoteDataSource
    ): SpoonacularRepository {
        return SpoonacularRepositoryImpl(remoteDataSource)
    }

}
