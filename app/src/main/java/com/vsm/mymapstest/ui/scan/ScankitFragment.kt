package com.vsm.mymapstest.ui.scan

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.vsm.mymapstest.MainMainActivity
import com.vsm.mymapstest.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScankitFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScankitFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG = this::class.java.simpleName
    private lateinit var rootView: View
    private lateinit var mainActivity: MainMainActivity
    val CAMERA_REQ_CODE = 111
    val DEFINED_CODE = 222
    val BITMAP_CODE = 333
    val MULTIPROCESSOR_SYN_CODE = 444
    val MULTIPROCESSOR_ASYN_CODE = 555
    val GENERATE_CODE = 666
    val DECODE = 1
    val GENERATE = 2
    private val REQUEST_CODE_SCAN_ONE = 0X01
    private val REQUEST_CODE_DEFINE = 0X0111
    private val REQUEST_CODE_SCAN_MULTI = 0X011
    val DECODE_MODE = "decode_mode"
    val RESULT = "SCAN_RESULT"

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
        rootView = inflater.inflate(R.layout.fragment_scankit, container, false)
        mainActivity = activity as MainMainActivity
        checkPermissions()


        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScankitFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScankitFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun checkPermissions() {
        ActivityCompat.requestPermissions(
            mainActivity,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            CAMERA_REQ_CODE
        )
    }
}
