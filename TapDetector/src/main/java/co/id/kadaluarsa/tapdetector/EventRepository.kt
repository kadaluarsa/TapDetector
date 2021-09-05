package co.id.kadaluarsa.tapdetector

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import co.id.kadaluarsa.tapdetector.model.EventResponse
import co.id.kadaluarsa.tapdetector.utils.GPSTrackerService
import co.id.kadaluarsa.tapdetector.utils.debugLog
import co.id.kadaluarsa.tapdetector.utils.errorLog
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


internal class EventRepository(
    private val application: Application, private val userId: String
) :
    Application.ActivityLifecycleCallbacks {
    private val doubleTapCallback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            errorLog("double tap not recorded")
        }

        override fun onResponse(call: Call, response: Response) {
            debugLog("double tap recorded")
        }
    }
    private val singleCallback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            errorLog("single tap not recorded")
        }

        override fun onResponse(call: Call, response: Response) {
            debugLog("single tap recorded")
        }
    }
    private val data by lazy {
        FraudDataSource()
    }
    private val gpsService by lazy {
        GPSTrackerService(application)
    }

    private var location: Location? = null
    private val tapListener: GestureDetector.OnGestureListener =
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                debugLog("double tap > ${e.x} - ${e.y}")
                data.recordBehavior(
                    mapOf(
                        "TapPositionX" to e.x,
                        "TapPositionY" to e.y,
                        "Latitude" to getLatitude(),
                        "Longitude" to getLongitude(),
                        "UserId" to userId
                    ), doubleTapCallback
                )
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                debugLog("single tap > ${e.x} - ${e.y}")
                data.recordBehavior(
                    mapOf(
                        "TapPositionX" to e.x,
                        "TapPositionY" to e.y,
                        "Latitude" to getLatitude(),
                        "Longitude" to getLongitude(),
                        "UserId" to userId
                    ), singleCallback
                )
                return true
            }
        }

    private fun getLatitude(): Double = location?.latitude ?: gpsService.getLatitude()
    private fun getLongitude(): Double = location?.longitude ?: gpsService.getLongitude()
    private var detector: GestureDetector? = null

    @SuppressLint("ClickableViewAccessibility")
    var touchListener: View.OnTouchListener? = View.OnTouchListener { _, p1 ->
        detector?.onTouchEvent(p1)
        true
    }

    init {
        application.registerActivityLifecycleCallbacks(this)
        detector = GestureDetector(application, tapListener)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        debugLog("created ${p0.localClassName}")
    }

    override fun onActivityStarted(p0: Activity) {
        debugLog("started ${p0.localClassName}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResumed(p0: Activity) {
        debugLog("resumed ${p0.localClassName}")
        val activity = p0 as AppCompatActivity
        val child = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
            .getChildAt(0) as View
        child.setOnTouchListener(touchListener)
        if (gpsService.canGetLocation()) {
            location = gpsService.getLocation()
        }
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
        val view = p0.window.decorView.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        view.setOnTouchListener(null)
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
        val view = p0.window.decorView.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        view.setOnTouchListener(null)
        gpsService.stopUsingGPS()
    }

    /**
     * if user client want manually track some action
     */
    fun track(property: FraudProperty) {
        debugLog("track $property")
    }

    fun dump(data: (respose: EventResponse) -> Unit) {
        this.data.getBehavior(mapOf(
            "UserId" to userId
        ), object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                errorLog("failed to dump behavior history")
            }

            override fun onResponse(call: Call, response: Response) {
                debugLog(
                    "dumped behaviour : \n" +
                            "${response.body}"
                )
                response.body?.string()?.let {
                    val data = Gson().fromJson(it, EventResponse::class.java)
                    data(data)
                }
            }
        })
    }

}


