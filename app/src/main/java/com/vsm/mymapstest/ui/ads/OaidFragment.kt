package com.vsm.mymapstest.ui.ads

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.vsm.mymapstest.R
import com.vsm.mymapstest.ui.ads.common.OaidCallback
import com.vsm.mymapstest.ui.ads.common.getOaid

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OaidFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OaidFragment : Fragment(), OaidCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG = this::class.java.simpleName
    private lateinit var rootView: View
    private var ad_id_tv: TextView? = null
    private var disable_ad_id_tv: TextView? = null

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
        rootView = inflater.inflate(R.layout.fragment_oaid, container, false)
        ad_id_tv = rootView.findViewById(R.id.ad_id_tv)
        disable_ad_id_tv = rootView.findViewById(R.id.disable_ad_id_tv)
        getIdentifierThread?.start()
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OaidFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OaidFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    /**
     * Update the device ID information from a UI thread.
     */
    private fun updateAdIdInfo(oaid: String?, isLimitAdTrackingEnabled: Boolean) {
        activity?.runOnUiThread(Runnable {
            if (!TextUtils.isEmpty(oaid)) {
                ad_id_tv?.setText(oaid)
            }
            disable_ad_id_tv?.setText(isLimitAdTrackingEnabled.toString())
        })
    }

    /**
     * Obtains device ID information from a non-UI thread.
     */
    private var getIdentifierThread: Thread? = object : Thread() {
        override fun run() {
            getOaid(rootView.context, this@OaidFragment)
        }
    }


    override fun onSuccuss(oaid: String?, isOaidTrackLimited: Boolean) {
        Log.i(
            TAG, "oiad=$oaid, isLimitAdTrackingEnabled=$isOaidTrackLimited"
        )
        updateAdIdInfo(oaid, isOaidTrackLimited)
    }

    override fun onFail(errMsg: String?) {
        Log.e(TAG, "getOaid Fail: $errMsg")
    }
}
