package com.livsky.webrtc

import android.util.Log
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

open class CustomSdpObserver(private val TAG: String): SdpObserver {
    override fun onSetFailure(p0: String?) {
        Log.d(TAG, p0.toString())
    }

    override fun onSetSuccess() {
        Log.d(TAG, "")
    }

    override fun onCreateSuccess(p0: SessionDescription?) {
        Log.d(TAG, p0.toString())
    }

    override fun onCreateFailure(p0: String?) {
        Log.d(TAG, p0.toString())
    }
}