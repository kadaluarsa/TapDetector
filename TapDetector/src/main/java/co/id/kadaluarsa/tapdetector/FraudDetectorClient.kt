package co.id.kadaluarsa.tapdetector

import android.app.Application
import co.id.kadaluarsa.tapdetector.model.EventResponse
import kotlin.properties.Delegates


/**
 * All client should access this class for init or tracking page
 */
class FraudDetectorClient private constructor() {
    private lateinit var eventRepository: EventRepository

    companion object {
        private var fraudConfig = FraudDetectConfig()
        fun application(_application: Application) = apply { fraudConfig.application = _application }
        fun setUserId(userId: String) = apply { fraudConfig.userId = userId }
        fun enableDebug(_isDebug: Boolean) = apply { fraudConfig.isDebug = _isDebug }
        fun build() = getInstance(fraudConfig)

        private fun getInstance(
            config: FraudDetectConfig
        ): FraudDetectorClient {
                return FraudDetectorClient().also {
                    it.eventRepository = EventRepository(config)
                }
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

internal class FraudDetectConfig {
    lateinit var application: Application
    var userId by Delegates.notNull<String>()
    var isDebug by Delegates.notNull<Boolean>()
}