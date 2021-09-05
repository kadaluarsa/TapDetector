package co.id.kadaluarsa.tapdetector.sample

import android.app.Application
import co.id.kadaluarsa.tapdetector.FraudDetectorClient

class SampleApp : Application() {

    private lateinit var tap: FraudDetectorClient

    companion object {
        private lateinit var sInstance: SampleApp
        fun getAppContext(): SampleApp {
            return sInstance
        }

        @Synchronized
        private fun setInstance(app: SampleApp) {
            sInstance = app
        }
    }

    fun getTap(): FraudDetectorClient {
        return if (::tap.isInitialized) {
            tap
        } else {
            FraudDetectorClient
                .application(this@SampleApp)
                .enableDebug(BuildConfig.DEBUG)
                .setUserId("aGI3Q8HGqS")
                .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        setInstance(this)
        tap = getTap()
    }

}