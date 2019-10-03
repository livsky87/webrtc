package com.livsky.webrtc

import android.util.Log
import org.webrtc.*

open class CustomPeerConnectionObserver(private var TAG: String) : PeerConnection.Observer {

    override fun onIceCandidate(p0: IceCandidate?) {
        Log.d(TAG, p0.toString())
    }

    override fun onDataChannel(p0: DataChannel?) {
        Log.d(TAG, p0.toString())
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        Log.d(TAG, p0.toString())
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        Log.d(TAG, p0.toString())
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        Log.d(TAG, p0.toString())
    }

    override fun onAddStream(p0: MediaStream?) {
        Log.d(TAG, p0.toString())
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        Log.d(TAG, p0.toString())
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
        Log.d(TAG, p0.toString())
    }

    override fun onRemoveStream(p0: MediaStream?) {
        Log.d(TAG, p0.toString())
    }

    override fun onRenegotiationNeeded() {
        Log.d(TAG, "onRenegotiationNeeded")
    }

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
        Log.d(TAG, p0.toString())
    }
}