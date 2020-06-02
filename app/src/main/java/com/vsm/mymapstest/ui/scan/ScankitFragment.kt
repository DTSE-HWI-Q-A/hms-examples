package com.vsm.mymapstest.ui.scan

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.huawei.hms.hmsscankit.OnResultCallback
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.ml.scan.HmsScan
import com.vsm.mymapstest.MyApp
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
    val Context.myApp: MyApp get() = applicationContext as MyApp

    //declare RemoteView instance
    private var remoteView: RemoteView? = null

    //declare the key ,used to get the value returned from scankit
    val SCAN_RESULT = "scanResult"

    var mScreenWidth = 0
    var mScreenHeight = 0

    //scan_view_finder width & height is  300dp
    val SCAN_FRAME_SIZE = 300


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
        //1.get screen density to caculate viewfinder's rect

        //1.get screen density to caculate viewfinder's rect
        val dm = resources.displayMetrics
        val density = dm.density
        //2.get screen size
        //2.get screen size
        mScreenWidth = resources.displayMetrics.widthPixels
        mScreenHeight = resources.displayMetrics.heightPixels

        val scanFrameSize = (SCAN_FRAME_SIZE * density)

        //3.caculate viewfinder's rect,it's in the middle of the layout
        //set scanning area(Optional, rect can be null,If not configure,default is in the center of layout)

        //3.caculate viewfinder's rect,it's in the middle of the layout
        //set scanning area(Optional, rect can be null,If not configure,default is in the center of layout)
        val rect = Rect()
        rect.left = (mScreenWidth / 2 - scanFrameSize / 2).toInt()
        rect.right = (mScreenWidth / 2 + scanFrameSize / 2).toInt()
        rect.top = (mScreenHeight / 2 - scanFrameSize / 2).toInt()
        rect.bottom = (mScreenHeight / 2 + scanFrameSize / 2).toInt()

        //initialize RemoteView instance, and set calling back for scanning result

        //initialize RemoteView instance, and set calling back for scanning result
        remoteView = RemoteView.Builder().setContext(activity).setBoundingBox(rect)
            .setFormat(HmsScan.ALL_SCAN_TYPE).build()
        // remoteView.onCreate(savedInstanceState)
        this.remoteView?.setOnResultCallback(OnResultCallback { result -> //judge the result is effective
            if (result != null && result.size > 0 && result[0] != null && !TextUtils.isEmpty(
                    result[0].getOriginalValue()
                )
            ) {
                Log.i(TAG, result[0].toString())
                /*val intent = Intent()
                intent.putExtra(SCAN_RESULT, result[0])
                setResult(RESULT_OK, intent)
                this@ScankitFragment.finish()*/
            }
        })
        //add remoteView to framelayout
        val params = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val frameLayout: FrameLayout = rootView.findViewById(R.id.rim)
        frameLayout.addView(remoteView, params)



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


}
