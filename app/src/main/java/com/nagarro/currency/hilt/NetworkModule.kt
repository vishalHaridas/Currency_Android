package com.nagarro.currency.hilt

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.nagarro.currency.BuildConfig
import com.nagarro.currency.common.Constants
import com.nagarro.currency.data.NoConnectivityException
import com.nagarro.currency.data.remote.PricesAPI
import com.nagarro.currency.misc.NetworkHelper
import com.nagarro.currency.misc.annotation.NoConnectivityInterceptor
import com.nagarro.currency.misc.annotation.PriceRateApiKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val FIXER_API_KEY_QUERY_PARAM = "access_key"

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @NoConnectivityInterceptor
    fun provideNetworkStateInterceptor(networkHelper: NetworkHelper): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            if (!networkHelper.isConnected())
                throw NoConnectivityException()

            val request: Request = chain
                .request()
                .newBuilder()
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    @PriceRateApiKey
    fun provideKeyInterceptor(): Interceptor {
        Log.d("ConvertViewModel", "API_KEY: ${FIXER_API_KEY_QUERY_PARAM} - ${BuildConfig.FIXER_API_KEY}")
        return Interceptor { chain: Interceptor.Chain ->
            var original: Request = chain.request()
            val url = original.url
                .newBuilder()
                .addQueryParameter(FIXER_API_KEY_QUERY_PARAM, BuildConfig.FIXER_API_KEY)
                .build()

            original = original
                .newBuilder()
                .url(url)
                .build()


            chain.proceed(original)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @PriceRateApiKey priceInterceptor: Interceptor,
        @NoConnectivityInterceptor noConnectivityInterceptor: Interceptor
    ) =
        if (BuildConfig.DEBUG)
            OkHttpClient
                .Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(priceInterceptor)
                .addInterceptor(noConnectivityInterceptor)
                .build()
        else
            OkHttpClient
                .Builder()
                .addInterceptor(priceInterceptor)
                .addInterceptor(noConnectivityInterceptor)
                .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun providePriceRateApi(retrofit: Retrofit): PricesAPI =
        retrofit.create(PricesAPI::class.java)

}

