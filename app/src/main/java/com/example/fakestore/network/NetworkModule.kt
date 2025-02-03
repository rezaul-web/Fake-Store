package com.example.fakestore.network

import com.example.fakestore.stripepay.StripePay
import com.example.fakestore.utils.Data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Create an OkHttpClient for general API requests
    @Provides
    @Singleton
    @Named("GeneralOkHttpClient")
    fun provideGeneralOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Create an OkHttpClient for Stripe API requests with authentication
    @Provides
    @Singleton
    @Named("StripeOkHttpClient")
    fun provideStripeOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val stripeAuthInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", "Bearer ${Data.SECRET_KEY}") // Use Bearer token
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Stripe-Version", Data.STRIPE_API_VERSION)
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(stripeAuthInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit instance for general API requests (FakeStore API)
    @Provides
    @Singleton
    @Named("GeneralRetrofit")
    fun provideGeneralRetrofit(@Named("GeneralOkHttpClient") client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit instance for Stripe API
    @Provides
    @Singleton
    @Named("StripeRetrofit")
    fun provideStripeRetrofit(@Named("StripeOkHttpClient") client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Data.STRIPE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    // Provide Stripe API instance
    @Provides
    @Singleton
    fun provideStripeApi(@Named("StripeRetrofit") retrofit: Retrofit): StripePay {
        return retrofit.create(StripePay::class.java)
    }

    // Provide FakeStore API instance
    @Provides
    @Singleton
    fun provideFakeStoreApi(@Named("GeneralRetrofit") retrofit: Retrofit): FakeStoreApi {
        return retrofit.create(FakeStoreApi::class.java)
    }

    // Provide Firebase Authentication instance
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    // Provide Firebase Firestore instance
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()


}