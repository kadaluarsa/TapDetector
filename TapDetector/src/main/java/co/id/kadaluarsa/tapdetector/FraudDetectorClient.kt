package co.id.kadaluarsa.tapdetector

import android.app.Application
import co.id.kadaluarsa.tapdetector.model.EventResponse
import co.id.kadaluarsa.tapdetector.utils.errorLog


/**
 * All client should access this class for init or tracking page
 */
class FraudDetectorClient private constructor(
    application: Application?,
    userId: String?,
    isDebug: Boolean
) {

    private val eventRepository: EventRepository by lazy {
        EventRepository(application!!, userId!!)
    }


    companion object {
        private var sInstance: FraudDetectorClient? = null
        fun getInstance(
            application: Application?,
            userId: String?,
            isDebug: Boolean
        ): FraudDetectorClient {
            sInstance = FraudDetectorClient(application, userId, isDebug)
            return sInstance!!
        }
    }

    fun getInstance(): FraudDetectorClient {
        if (sInstance == null) errorLog("SDK instance not found")
        return sInstance!!
    }

    class Config {
        private var application: Application? = null
        private var userId: String? = null
        private var isDebug: Boolean = false
        fun application(_application: Application) = apply { application = _application }
        fun setUserId(_userId: String) = apply { userId = _userId }
        fun enableDebug(_isDebug: Boolean) = apply { isDebug = _isDebug }
        fun build(): FraudDetectorClient {
            if (application == null) errorLog("Application not initialized")
            if (userId == null) errorLog("UserId cannot be null")
            return getInstance(application, userId, isDebug)
        }
    }

    fun track(property: FraudProperty) {
        eventRepository.track(property)
    }

    fun dumpedData(data: (respose: EventResponse) -> Unit) {
        eventRepository.dump(data)
    }

    fun touchListener() = eventRepository.touchListener
}

