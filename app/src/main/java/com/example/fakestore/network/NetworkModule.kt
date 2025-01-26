package com.example.fakestore.network

import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
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
    fun provideOkHttpClient(): OkHttpClient {
        // Create a logging interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
        }

        // Add custom interceptor for detailed logging
        val customLoggingInterceptor = Interceptor { chain ->
            val request = chain.request()
            val t1 = System.nanoTime()
            println("Sending request: ${request.url} \n${request.headers}")

            val response = chain.proceed(request)
            val t2 = System.nanoTime()
            println("Received response for ${response.request.url} in ${(t2 - t1) / 1e6} ms")
            println("Response body: ${response.peekBody(Long.MAX_VALUE).string()}")

            response
        }

        // Build OkHttpClient
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)      // Logs request and response bodies
            .addInterceptor(customLoggingInterceptor) // Custom logging
            .connectTimeout(30, TimeUnit.SECONDS)    // Configure timeouts
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("https://fakestoreapi.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): FakeStoreApi {
        return retrofit.create(FakeStoreApi::class.java)
    }


    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}