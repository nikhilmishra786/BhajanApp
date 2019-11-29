package com.infomantri.bhakti.bhajan.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.infomantri.bhakti.bhajan.AppConstant
import com.infomantri.bhakti.bhajan.BuildConfig
import com.syngenta.pack.network.NetworkServices
import okhttp3.*
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException


object RetrofitClient {

    private var networkServices: NetworkServices? = null
    val YOUTUBE_API_BASE_URL =
        "https://www.googleapis.com/youtube/v3/"

    fun getService(): NetworkServices? {
        if (networkServices == null) {

            val gsonBuilder = GsonBuilder()
            gsonBuilder.serializeNulls()
            val gson = gsonBuilder.create()

            val retrofit = Retrofit.Builder()
                .baseUrl(YOUTUBE_API_BASE_URL)
                .addConverterFactory(nullOnEmptyConverterFactory)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getClient())
                .build()

            networkServices = retrofit.create(NetworkServices::class.java)
        }

        return networkServices
    }

    val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ) = object :
            Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter =
                retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)

            override fun convert(value: ResponseBody) =
                if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
        }
    }

    private fun getClient(): OkHttpClient {

        val okHttpBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            // used to print logs
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.addInterceptor(logging)
        }

        return okHttpBuilder
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(SecurityInterceptor())
//                .authenticator(TokenAuthenticator())
            .build()
    }

    class SecurityInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val mainRequest = chain.request()
            val builder = mainRequest.newBuilder()
//            addCustomHeaders(mainRequest, builder)
            var mainResponse = chain.proceed(builder.build())
            return mainResponse

        }
    }
}
