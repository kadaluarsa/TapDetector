package co.id.kadaluarsa.tapdetector

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class FraudDataSource : DataSource {
    override fun recordBehavior(_data: Map<String, Any?>, responseCallback: Callback) {
        val data = JSONObject(_data).toString()
        val body: RequestBody =
            data.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val httpBuilder = Dependency.okhttpBuilder
        httpBuilder?.build()?.let { url ->
            val request: Request = Request.Builder()
                .url(url)
                .post(body)
                .build()
            Dependency.okHttpClient
                .newCall(request)
                .enqueue(responseCallback)
        }
    }

    override fun getBehavior(_data: Map<String, Any>, responseCallback: Callback) {
        val data = JSONObject(_data).toString()
        val httpBuilder = Dependency.okhttpBuilder
        httpBuilder?.removeAllQueryParameters("where")
        httpBuilder?.addQueryParameter("where", data)
        httpBuilder?.build()?.let { url ->
            val request = Request.Builder()
                .url(url)
                .build()
            Dependency.okHttpClient
                .newCall(request)
                .enqueue(responseCallback)
        }
    }
}