package com.livsky.webrtc

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import org.webrtc.SessionDescription
import java.lang.Exception
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal class SignallingClient {

    internal interface SignallingInterface {
        fun onOfferReceived(data: JSONObject)

        fun onAnswerReceived(data: JSONObject)

        fun onIceCandidateReceived(data: JSONObject)
    }

    companion object {
        private const val TAG = "SignallingClient"
        private var socket: Socket? = null

        private var callback: SignallingInterface? = null

        private val trustAllCertificates = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
            }

            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
            }
        })

        fun init(signallingInterface: SignallingInterface) {
            this.callback = signallingInterface
            try {
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustAllCertificates, null)

                IO.setDefaultHostnameVerifier { _, _ -> true}
                IO.setDefaultSSLContext(sslContext)

                socket = IO.socket("https://p2p.new-life.io/broadcast")
                socket?.connect()

                socket?.on("message") { args ->
                    Log.e(TAG, "message: [${Arrays.toString(args)}]")
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

        fun sendMessage(message: SessionDescription) {
            val obj = JSONObject()
            obj.put("id", "presenter")
            obj.put("title", "12345")
            obj.put("sdpOffer", message.description)
            socket?.send(obj)
        }
    }
}