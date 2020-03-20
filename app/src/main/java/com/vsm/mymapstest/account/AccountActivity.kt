package com.vsm.mymapstest.account

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.vsm.mymapstest.R
import com.vsm.mymapstest.utils.Constant


class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        val mHuaweiIdAuthParams: HuaweiIdAuthParams
        mHuaweiIdAuthParams =
            HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken()
                .createParams()
        val mHuaweiIdAuthService =
            HuaweiIdAuthManager.getService(this@AccountActivity, mHuaweiIdAuthParams)
        startActivityForResult(
            mHuaweiIdAuthService.getSignInIntent(),
            Constant().REQUEST_SIGN_IN_LOGIN
        );

    }
}
