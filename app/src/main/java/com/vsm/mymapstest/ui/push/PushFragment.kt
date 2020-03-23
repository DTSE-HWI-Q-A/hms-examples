package com.vsm.mymapstest.ui.push

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.vsm.mymapstest.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var rootView: View
private lateinit var buttonTokenPush: Button

/**
 * A simple [Fragment] subclass.
 * Use the [PushFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PushFragment : Fragment() {
    private val TAG = this::class.java.simpleName
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        rootView = inflater.inflate(R.layout.fragment_push, container, false)
        //textViewTokenPush = rootView.findViewById(R.id.textViewTokenPush)
        buttonTokenPush = rootView.findViewById(R.id.buttonTokenPush)
        buttonTokenPush.setOnClickListener {
            object : Thread() {
                override fun run() {
                    try {
                        val appId =
                            AGConnectServicesConfig.fromContext(context).getString("client/app_id")
                        Log.i(TAG, "appId: $appId")
                        var pushtoken = HmsInstanceId.getInstance(context).getToken(appId, "HCM")
                        showLog(pushtoken, appId)
                        Log.i(TAG, "token: $pushtoken")
                    } catch (e: Exception) {
                        Log.i(TAG, "getToken failed, $e")
                    }
                }
            }.start()
        }

        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PushFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PushFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun showLog(log: String, appId: String) {
        activity?.runOnUiThread(Runnable {
            val tvView: TextView = rootView.findViewById(R.id.textViewTokenPush)
            val textViewAppIdPush: TextView = rootView.findViewById(R.id.textViewAppIdPush)
            if (tvView is TextView) {
                if (!log.isEmpty()) {
                    tvView.text = "My Token: $log"
                } else {
                    val sharedPreference = activity?.getSharedPreferences(
                        activity?.getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE
                    )
                    tvView.text =
                        sharedPreference?.getString(getString(R.string.preference_token_key), "")
                }
                textViewAppIdPush.text = "My AppId: $appId"
                Toast.makeText(rootView.context, log, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
