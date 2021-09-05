package co.id.kadaluarsa.tapdetector

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import co.id.kadaluarsa.tapdetector.model.EventResponse
import co.id.kadaluarsa.tapdetector.utils.debugLog
import co.id.kadaluarsa.tapdetector.utils.errorLog
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


internal class EventRepository(
    private val config: FraudDetectConfig
) :
    Application.ActivityLifecycleCallbacks {
    private val data by lazy {
        FraudDataSource()
    }
    private var location: Location? = null
    private val tapListener: GestureDetector.OnGestureListener =
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                debugLog("double tap > ${e.x} - ${e.y}")
                data.recordBehavior(mapOf(
                    "TapPositionX" to e.x,
                    "TapPositionY" to e.y,
                    "Latitude" to location?.latitude,
                    "Longitude" to location?.longitude,
                    "UserId" to config.userId
                ), object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        errorLog("double tap not recorded")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        debugLog("double tap recorded")
                    }
                })
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                debugLog("single tap > ${e.x} - ${e.y}")
                data.recordBehavior(mapOf(
                    "TapPositionX" to e.x,
                    "TapPositionY" to e.y,
                    "Latitude" to location?.latitude,
                    "Longitude" to location?.longitude,
                    "UserId" to config.userId
                ), object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        errorLog("single tap not recorded")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        debugLog("single tap recorded")
                    }
                })
                return true
            }
        }

    private var locationListenerGPS: LocationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location) {
            debugLog("location changed GPS")
            location = p0
            locationManager.removeUpdates(this)
            locationManager.removeUpdates(locationListenerNetwork)
        }
    }
    private val locationListenerNetwork: LocationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location) {
            debugLog("location changed Network")
            location = p0
            locationManager.removeUpdates(this)
            locationManager.removeUpdates(locationListenerGPS)
        }
    }
    private lateinit var locationManager: LocationManager
    private var detector: GestureDetector? = null

    internal var touchListener: View.OnTouchListener? = View.OnTouchListener { _, p1 ->
        detector?.onTouchEvent(p1)
        true
    }

    init {
        config.application.registerActivityLifecycleCallbacks(this)
        detector = GestureDetector(config.application, tapListener)
        initLocation()
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
        val child = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as View
        child.setOnTouchListener(touchListener)
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
    }


    @SuppressLint("MissingPermission")
    private fun initLocation() {
        debugLog("initialization Location")
        if (ActivityCompat.checkSelfPermission(
                config.application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                config.application,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            errorLog("permission location and coearse_location should be granted by user")
            return
        }
        locationManager = config.application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        var netLocation: Location? = null
        var gpsLocation: Location? = null

        if (isGpsEnabled) gpsLocation =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (isNetworkEnabled) netLocation =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (isGpsEnabled.not() && isNetworkEnabled.not()) {
            errorLog("No location provider available")
        }

        debugLog(
            "location info : \n" +
                    "network_enabled : $isNetworkEnabled\n" +
                    "gps_enabled : $isGpsEnabled"
        )

        if (gpsLocation == null && netLocation == null) {
            startLocationUpdates(isGpsEnabled, isNetworkEnabled)
        } else {
            var finalLoc: Location? = if (gpsLocation != null && netLocation != null) {
                if (gpsLocation.accuracy > netLocation.accuracy) netLocation else gpsLocation
            } else {
                gpsLocation ?: netLocation
            }
            debugLog("location ${finalLoc?.latitude} - ${finalLoc?.longitude}")
            location = finalLoc
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(isGpsEnabled: Boolean, isNetworkEnabled: Boolean) {
        debugLog("starting location updates")
        if (isGpsEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0F,
                locationListenerGPS, Looper.getMainLooper()
            )
        }
        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0F,
                locationListenerNetwork, Looper.getMainLooper()
            )
        }
    }

    /**
     * if user client want manually track some action
     */
    fun track(property: FraudProperty) {
        debugLog("track $property")
    }

    fun dump(data: (respose: EventResponse) -> Unit) {
        this.data.getBehavior(mapOf(
            "UserId" to config.userId
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


