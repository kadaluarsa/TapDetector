package co.id.kadaluarsa.tapdetector.utils

import android.util.Log

internal fun Any.debugLog(message: String) {
    Log.i(
        this.javaClass.simpleName, "[debug log message fraudSDK] :=> $message"
    )
}

internal fun Any.errorLog(message: String){
    Log.e(
        this.javaClass.simpleName, "[debug error message fraudSDK] :=> $message"
    )
}
