package xute.markdeditor.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xute.markdeditor.MarkDEditor

object RetrofitApiClient {
    private var retrofit: Retrofit? = null

    @JvmStatic
    fun getClient(token: String): Retrofit? {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request()
                            .newBuilder()
                            .addHeader("Authorization", "Token $token")
                            .build()
                    chain.proceed(request)
                }
                .addInterceptor(logging)
                .build()
        retrofit = Retrofit.Builder()
                .baseUrl(MarkDEditor.serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        return retrofit
    }
}