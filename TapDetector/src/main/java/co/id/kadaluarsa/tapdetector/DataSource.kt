package co.id.kadaluarsa.tapdetector

import okhttp3.Callback

interface DataSource {
    fun recordBehavior(data: Map<String, Any?>, responseCallback: Callback)
    fun getBehavior(data: Map<String, Any>, responseCallback: Callback)
}