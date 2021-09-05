package co.id.kadaluarsa.tapdetector
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object Dependency {

    const val CONNECT_TIMEOUT: Long = 100000
    const val READ_TIMEOUT: Long = 100000
    const val WRITE_TIMEOUT: Long = 100000

    private val logInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    private val headerInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header(name = "X-Parse-Application-Id", value = BuildConfig.APP_ID)
                .header(name = "X-Parse-REST-API-Key", value = BuildConfig.APP_KEY)
                .header(name = "X-Parse-Session-Token", value = BuildConfig.APP_TOKEN)
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
    }

    val okhttpBuilder by lazy {
        BuildConfig.APP_URL.toHttpUrlOrNull()?.newBuilder()
    }

    val okHttpClient: OkHttpClient by lazy {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
        okHttpClient.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
        okHttpClient.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
        okHttpClient.addInterceptor(logInterceptor)
        okHttpClient.addInterceptor(headerInterceptor)
        okHttpClient.build()
    }
}