package com.vsm.mymapstest.ui.account

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import com.vsm.mymapstest.R
import com.vsm.mymapstest.utils.ICallBack


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountKitFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountKitFragment : Fragment(), View.OnClickListener {
    private val TAG = this::class.java.simpleName
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var rootView: View
    private lateinit var mHuaweiIdAuthParams: HuaweiIdAuthParams
    private lateinit var mAuthManager: HuaweiIdAuthService
    private lateinit var hwid_signin: Button
    private lateinit var hwid_signInCode: Button
    private lateinit var hwid_signout: Button

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
        rootView = inflater.inflate(R.layout.fragment_account_kit, container, false)

        hwid_signin = rootView.findViewById(R.id.hwid_signin)
        hwid_signin.setOnClickListener(this)
        hwid_signInCode = rootView.findViewById(R.id.hwid_signInCode)
        hwid_signInCode.setOnClickListener(this)
        hwid_signout = rootView.findViewById(R.id.hwid_signout)
        hwid_signout.setOnClickListener(this)
        mHuaweiIdAuthParams =
            HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken()
                .createParams()
        mAuthManager = HuaweiIdAuthManager.getService(activity, mHuaweiIdAuthParams)


        return rootView
    }


    private fun signIn() {
        startActivityForResult(
            mAuthManager.signInIntent,
            Constant().REQUEST_SIGN_IN_LOGIN
        )
    }

    private fun signInCode() {
        mHuaweiIdAuthParams =
            HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setProfile()
                .setAuthorizationCode()
                .createParams()
        mAuthManager = HuaweiIdAuthManager.getService(activity, mHuaweiIdAuthParams)
        startActivityForResult(
            mAuthManager.signInIntent,
            Constant().REQUEST_SIGN_IN_LOGIN_CODE
        )
    }

    private fun signOut() {
        val signOutTask = mAuthManager.signOut()
        signOutTask.addOnSuccessListener { Log.i(TAG, "signOut Success") }
            .addOnFailureListener { Log.i(TAG, "signOut fail") }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountKitFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountKitFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun validateIdToken(idToken: String) {
        if (TextUtils.isEmpty(idToken)) {
            Log.i(TAG, "ID Token is empty")
        } else {
            val idTokenParser = IDTokenParser()
            try {
                idTokenParser.verify(idToken, object : ICallBack {
                    override fun onSuccess() {}
                    override fun onSuccess(idTokenJsonStr: String?) {
                        if (!TextUtils.isEmpty(idTokenJsonStr)) {
                            Log.i(
                                TAG,
                                "id Token Validate Success, verify signature: $idTokenJsonStr"
                            )
                        } else {
                            Log.i(TAG, "Id token validate failed.")
                        }
                    }

                    override fun onFailed() {
                        Log.i(TAG, "Id token validate failed.")
                    }
                })
            } catch (e: Exception) {
                Log.i(TAG, "id Token validate failed." + e.javaClass.simpleName)
            } catch (e: Error) {
                Log.i(TAG, "id Token validate failed." + e.javaClass.simpleName)
                if (Build.VERSION.SDK_INT < 23) {
                    Log.i(
                        TAG,
                        "android SDK Version is not support. Current version is: " + Build.VERSION.SDK_INT
                    )
                }
            }
        }
    }

    private fun silentSignIn() {
        val task = mAuthManager.silentSignIn()
        task.addOnSuccessListener { Log.i(TAG, "silentSignIn success") }
        task.addOnFailureListener { e ->
            //if Failed use getSignInIntent
            if (e is ApiException) {
                val apiException = e
                signIn()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === Constant().REQUEST_SIGN_IN_LOGIN) { //login success
//get user message by parseAuthResultFromIntent
            val authHuaweiIdTask =
                HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {
                val huaweiAccount = authHuaweiIdTask.result
                Log.i(TAG, huaweiAccount.displayName + " signIn success ")
                Log.i(TAG, "AccessToken: " + huaweiAccount.accessToken)
                validateIdToken(huaweiAccount.idToken)
            } else {
                Log.i(
                    TAG,
                    "signIn failed: " + (authHuaweiIdTask.exception as ApiException).statusCode
                )
            }
        }
        if (requestCode === Constant().REQUEST_SIGN_IN_LOGIN_CODE) { //login success
            val authHuaweiIdTask =
                HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {
                val huaweiAccount = authHuaweiIdTask.result
                Log.i(TAG, "signIn get code success.")
                Log.i(TAG, "ServerAuthCode: " + huaweiAccount.authorizationCode)
            } else {
                Log.i(
                    TAG,
                    "signIn get code failed: " + (authHuaweiIdTask.exception as ApiException).statusCode
                )
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.hwid_signin -> signIn()
            R.id.hwid_signout -> signOut()
            R.id.hwid_signInCode -> signInCode()
            else -> {
            }
        }

    }
}
