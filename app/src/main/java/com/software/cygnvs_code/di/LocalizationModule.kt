package com.software.cygnvs_code.di

import android.util.Log
import com.nobrandorg.securenotes.common.di.IoDispatcher
import com.software.cygnvs_code.LocalizationHelper
import com.software.cygnvs_code.domain.LocalizationAPI
import com.software.cygnvs_code.domain.LocalizationRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object LocalizationModule {

    @Singleton
    @Provides
    fun provideLocalizationHelper(): LocalizationHelper {
        return LocalizationHelper()
    }

    @Provides
    fun provideBaseUrl() =
        "https://raw.githubusercontent.com/Sangeethacygnvs/android_test/main/"

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = Interceptor { chain ->
            val builder = chain.request().newBuilder()

            var response: Response? = null

            try {
                response = chain.proceed(builder.build())
            } catch (e: Exception) {
                Log.e("Handled Exception" , e.message.toString())

                return@Interceptor Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_2)
                    .code(401) // status code
                    .message("")
                    .body(
                        ResponseBody.create(
                            "application/json; charset=utf-8".toMediaType() ,
                            "{}"
                        )
                    )
                    .build()
            }

            return@Interceptor response
        }

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .readTimeout(10 , TimeUnit.SECONDS)
            .writeTimeout(10 , TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient , BASE_URL: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    fun provideLocalizationRepo(
        retrofit: Retrofit ,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): LocalizationRepo {
        return LocalizationRepo(
            retrofit.create(LocalizationAPI::class.java) ,
            ioDispatcher)
    }

}