package com.vsm.mymapstest

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.vsm.mymapstest.utils.Permissions


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), OnMapReadyCallback {

    private val TAG = this::class.java.simpleName

    //Huawei map
    private var hMap: HuaweiMap? = null
    private var mMapView: MapView? = null
    private lateinit var rootView: View
    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    private var common = Permissions()

    private val RUNTIME_PERMISSIONS = arrayOf<String>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.INTERNET
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_first, container, false)
        Log.i(TAG, "onCreate:hzj");

        /*if (!common.hasPermissions(context!!, RUNTIME_PERMISSIONS)) {
            ActivityCompat.requestPermissions(MainActivity::class.java, RUNTIME_PERMISSIONS, REQUEST_CODE);
        }*/
        //get mapview instance
        //get mapview instance
        mMapView = rootView.findViewById(R.id.mapView)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mMapView!!.onCreate(mapViewBundle)
        //get map instance
        //get map instance
        mMapView!!.getMapAsync(this)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* view.findViewById<Button>(R.id.button_first).setOnClickListener {
             findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
         }*/
    }

    override fun onStart() {
        super.onStart()
        mMapView?.onStart();
    }

    override fun onStop() {
        super.onStop()
        mMapView?.onStop();
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView?.onDestroy();
    }

    override fun onPause() {
        mMapView?.onPause();
        super.onPause()

    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume();
    }

    override fun onMapReady(p0: HuaweiMap?) {
        //get map instance in a callback method
        Log.i(TAG, "onMapReady: ");
        hMap = p0;
    }
}
