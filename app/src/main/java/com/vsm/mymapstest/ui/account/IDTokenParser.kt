package com.vsm.mymapstest.ui.account

import android.util.Base64
import android.util.Log
import com.auth0.jwk.InvalidPublicKeyException
import com.auth0.jwk.Jwk
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.vsm.mymapstest.utils.ICallBack
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.security.interfaces.RSAPublicKey


class IDTokenParser {
    private val MAX_PUBLIC_KEY_SIZE = 4


    private var mJsonArray: JSONArray? = null

    private var mRSAPublicKey: RSAPublicKey? = null

    private val TAG = IDTokenParser::class.java.simpleName

    /**
     * catch the public key in this map
     */
    private val keyId2PublicKey: MutableMap<String, RSAPublicKey> =
        HashMap<String, RSAPublicKey>()

    /**
     * Verify Id Token
     *
     * @param idToken Your IdToken
     * @param callBack Asyn CallBack
     * @throws InvalidPublicKeyException throw when InvalidPublicKeyException happened
     */
    @Throws(InvalidPublicKeyException::class, JWTDecodeException::class)
    fun verify(idToken: String?, callBack: ICallBack) {
        val decoder: DecodedJWT = JWT.decode(idToken)
        getRSAPublicKeyByKidAsyn(decoder.getKeyId(), object : ICallBack {
            override fun onSuccess() {
                try {
                    val algorithm: Algorithm = Algorithm.RSA256(mRSAPublicKey, null)
                    val verifier: JWTVerifier = JWT.require(algorithm).build()
                    val jsonObject =
                        JSONObject(String(Base64.decode(decoder.getPayload(), Base64.DEFAULT)))
                    // Verify the value of iss
                    if (!decoder.getIssuer().equals(Constant().ID_TOKEN_ISSUE)) {
                        callBack.onFailed()
                        return
                    }
                    // Verify your app’s client ID.
                    val clientId: String = decoder.getAudience().get(0)
                    if (decoder.getAudience().size > 0) {
                        if (!decoder.getAudience().get(0).equals(Constant().CLIENT_ID)) {
                            callBack.onFailed()
                            return
                        }
                    }
                    // verify signature
                    verifier.verify(decoder)
                    jsonObject.put("alg", decoder.getAlgorithm())
                    jsonObject.put("typ", decoder.getType())
                    jsonObject.put("kid", decoder.getKeyId())
                    callBack.onSuccess(jsonObject.toString())
                } catch (e: JWTDecodeException) {
                    callBack.onFailed()
                } catch (e: JSONException) {
                    callBack.onFailed()
                } catch (e: TokenExpiredException) {
                    callBack.onFailed()
                    // jwt token is expire
                } catch (e: JWTVerificationException) {
                    callBack.onFailed()
                    // VERIFY SIGNATURE failed
                } catch (e: Exception) {
                    callBack.onFailed()
                } catch (e: Error) {
                    callBack.onFailed()
                }
            }

            override fun onSuccess(result: String?) {}
            override fun onFailed() {
                callBack.onFailed()
            }
        })
    }

    /**
     * get the RSAPublicKey by kid
     * Please cache the RSAPublicKey
     * In the demo we cache it in a map
     *
     * @param keyId Input keyId
     * @param callBack asyn callback
     * @throws InvalidPublicKeyException throw when InvalidPublicKeyException happened
     */
    @Throws(InvalidPublicKeyException::class)
    private fun getRSAPublicKeyByKidAsyn(
        keyId: String,
        callBack: ICallBack
    ) {
        getJwks(object : ICallBack {
            override fun onSuccess() {
                if (keyId2PublicKey[keyId] != null) {
                    mRSAPublicKey = keyId2PublicKey[keyId]
                    callBack.onSuccess()
                } else {
                    if (mJsonArray == null) {
                        mRSAPublicKey = null
                        return
                    }
                    if (keyId2PublicKey.size > MAX_PUBLIC_KEY_SIZE) {
                        keyId2PublicKey.clear()
                    }
                    try {
                        for (i in 0 until mJsonArray!!.length()) {
                            val kid = mJsonArray!!.getJSONObject(i).getString("kid")
                            keyId2PublicKey[kid] =
                                getRsaPublicKeyByJwk(mJsonArray!!.getJSONObject(i))
                        }
                        mRSAPublicKey = keyId2PublicKey[keyId]
                        callBack.onSuccess()
                    } catch (e: Exception) {
                        mRSAPublicKey = null
                        Log.i(TAG, "getRSAPublicKeyByKid failed: " + e.message)
                    }
                }
            }

            override fun onSuccess(keys: String?) {}
            override fun onFailed() {
                mRSAPublicKey = null
            }
        })
    }

    /**
     * get jwks from the https://oauth-login.cloud.huawei.com/oauth2/v3/certs endpoint
     * because the jwk update each day, please cache the jwk,here is the example of jwks
     * See more about JWK in http://self-issued.info/docs/draft-ietf-jose-json-web-key.html
     * {
     * "keys":[
     * {
     * "kty":"RSA",
     * "e":"AQAB",
     * "use":"sig",
     * "kid":"670c64e7443941582167f405e0a62a08c6db5becb090f397a45aa572fa000553",
     * "alg":"RS256",
     * "n":"AK4C-h_gWpziPmzo6PEBuwxHHD2F9x_LgiE5zl73fVmzBTo3KzRu8nXURQA-uV857r_qEhfsJQyy0Nr_wIkfAU86JsFHcGwzLlJucN12EHXOFY6nLti9tSWUAWaa2HAZuJytyc-DyguLR_nH5IKmGhmcgI26zUG07UPUB2Xnsn-T-K1npnaNI7K7xlbGQy5UUPFjQPBRiV2R_-iGf5KIqCwebXe24wzhbWMnmfb0lilAZyYO7PiQ8UgJJTuZOMbCD7P0dUJwxitHo81OyoVJUGQZpLBZqHVSsRpC0UZuxMEMBPza4R55yZS3gAKkE1xILabCUV-CJ6Gp4c4J1tiFNcc"
     * },
     * {
     * "kty":"RSA",
     * "e":"AQAB",
     * "use":"sig",
     * "kid":"1226cda6b82e6aa140ffe2f32515f1929c3048b2cdba267935fb71963fd3e57a",
     * "alg":"RS256",
     * "n":"AMNDMIxlySrGqeV7V3s865ZwzBD0hXVq8ys0H_ZQGMbfWss0WuwHrmIRdq8OQrYoN_o2KZKtUPBsJpJMAZ26JeGqf3dsU_wFEEjNOfrDdyIs86K6gKfQFLewUNycmzMhlqDFlCujAAF33RYn-Xg7UXG3pO_2PdcD1zIrxtawQZ-TQTMgH5mgX_lWO1YfYSuB91xEvUZII1ZYDjLcbkzjZCbvfU4tT2_PV8-gU0UaHI-pcyreUwB1EhleDZUW8MiaIVSr6UIYfHflxBQRrwCcFy-q_u-OeYBS683djmbF-FKZVKlipKim6hGpqIb-PC8pHO_WmM01sNvvMAkF1D5bg0M"
     * }
     * ]
     * }
     */
    private fun getJwks(iCallBack: ICallBack) {

        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(Constant().CERT_URL)
            .build()
        val call: Call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.i(TAG, "Get ID Token failed.")
                iCallBack.onFailed()
            }

            override fun onResponse(call: Call?, response: Response) {
                if (response.isSuccessful()) {
                    try {
                        val res: String = response.body()!!.string()
                        val jsonObject = JSONObject(res)
                        mJsonArray = jsonObject.getJSONArray("keys")
                        iCallBack.onSuccess()
                    } catch (e: NullPointerException) {
                        Log.i(TAG, "parse JsonArray failed." + e.message)
                        iCallBack.onFailed()
                    } catch (e: JSONException) {
                        Log.i(TAG, "parse JsonArray failed." + e.message)
                        iCallBack.onFailed()
                    } catch (e: IOException) {
                        Log.i(TAG, "parse JsonArray failed." + e.message)
                        iCallBack.onFailed()
                    }
                }
            }
        })
    }

    /**
     * get RsaPublicKey from a JWK
     * @param jwkObject received JSONObject
     * @return RsaPublicKey from a JWK
     * @throws InvalidPublicKeyException
     */
    @Throws(InvalidPublicKeyException::class, JSONException::class)
    private fun getRsaPublicKeyByJwk(jwkObject: JSONObject): RSAPublicKey {
        val additionalAttributes: MutableMap<String, Any> =
            HashMap()
        additionalAttributes["n"] = jwkObject.getString("n")
        additionalAttributes["e"] = jwkObject.getString("e")
        val operations: List<String> = emptyList()
        val jwk: Jwk = Jwk(
            jwkObject.getString("kid"),
            jwkObject.getString("kty"),
            jwkObject.getString("alg"),
            jwkObject.getString("use"),
            operations,
            null,
            null,
            null,
            additionalAttributes
        )
        return jwk.getPublicKey() as RSAPublicKey
    }
}