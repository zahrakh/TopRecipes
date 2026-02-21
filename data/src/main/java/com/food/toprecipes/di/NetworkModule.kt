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
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
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
            .addInterceptor { chain ->
                val url = chain.request().url.newBuilder()
                    .addQueryParameter("apiKey", BuildConfig.API_KEY)
                    .build()
                chain.proceed(chain.request().newBuilder().url(url).build())
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitApi(client: OkHttpClient): ApiService {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
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
