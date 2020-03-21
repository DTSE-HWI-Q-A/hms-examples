package com.vsm.mymapstest.ui.location

import android.annotation.SuppressLint
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.huawei.hms.common.ApiException
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.*
import com.vsm.mymapstest.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LocationFragment : Fragment(), View.OnClickListener {
    private val TAG = this::class.java.simpleName

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var rootView: View
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var textViewLatitude: TextView
    private lateinit var textViewLongitude: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_location, container, false)
        textViewLatitude = rootView.findViewById(R.id.textViewLatitude)
        textViewLongitude = rootView.findViewById(R.id.textViewLongitude)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        settingsClient = LocationServices.getSettingsClient(activity)
        mLocationRequest = LocationRequest()
        // set the interval for location updates, in milliseconds.
        mLocationRequest.setInterval(10000)
        // set the priority of the request
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n")
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult != null) {
                    val locations: List<Location> =
                        locationResult.locations
                    if (!locations.isEmpty()) {
                        for (location in locations) {
                            textViewLatitude.text = "Latitude: ${location.latitude.toString()}"
                            textViewLongitude.text = "Longitude: ${location.longitude.toString()}"
                            Log.i(
                                TAG,
                                "onLocationResult location[Longitude,Latitude,Accuracy]:" + location.getLongitude()
                                    .toString() + "," + location.getLatitude()
                                    .toString() + "," + location.getAccuracy()
                            )
                        }
                    }
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                if (locationAvailability != null) {
                    val flag = locationAvailability.isLocationAvailable
                    Log.i(TAG, "onLocationAvailability isLocationAvailable:$flag")
                }
            }
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestLocationUpdatesWithCallback()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LocationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LocationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdatesWithCallback()
    }

    private fun requestLocationUpdatesWithCallback() {
        try {
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest)
            val locationSettingsRequest = builder.build()
            // check devices settings before request location updates.
            settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    Log.i(TAG, "check location settings success")
                    //request location updates
                    fusedLocationProviderClient.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.getMainLooper()
                        ).addOnSuccessListener {
                            Log.i(
                                TAG,
                                "requestLocationUpdatesWithCallback onSuccess"
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                TAG,
                                "requestLocationUpdatesWithCallback onFailure:" + e.message
                            )
                        }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "checkLocationSetting onFailure:" + e.message)
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(activity, 0)
                        } catch (sie: IntentSender.SendIntentException) {
                            Log.e(TAG, "PendingIntent unable to execute request.")
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "requestLocationUpdatesWithCallback exception:" + e.message)
        }
    }

    private fun removeLocationUpdatesWithCallback() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                .addOnSuccessListener { Log.i(TAG, "removeLocationUpdatesWithCallback onSuccess") }
                .addOnFailureListener { e ->
                    Log.e(
                        TAG,
                        "removeLocationUpdatesWithCallback onFailure:" + e.message
                    )
                }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "removeLocationUpdatesWithCallback exception:" + e.message)
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v!!.id) {
                R.id.location_requestLocationUpdatesWithCallback -> requestLocationUpdatesWithCallback()
                R.id.location_removeLocationUpdatesWithCallback -> removeLocationUpdatesWithCallback()
                else -> {
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "RequestLocationUpdatesWithCallbackActivity Exception:$e")
        }
    }
}
