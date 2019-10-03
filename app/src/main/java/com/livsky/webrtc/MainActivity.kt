package com.livsky.webrtc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import org.json.JSONObject
import org.webrtc.*

class MainActivity : AppCompatActivity(), SignallingClient.SignallingInterface {

    lateinit var peerConnectionFactory: PeerConnectionFactory
    lateinit var localPeer: PeerConnection
    lateinit var localVideoTrack: VideoTrack
    private val iceServers = ArrayList<PeerConnection.IceServer>()

    override fun onOfferReceived(data: JSONObject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAnswerReceived(data: JSONObject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onIceCandidateReceived(data: JSONObject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PeerConnectionFactory.initializeAndroidGlobals(this, true)

        val options = PeerConnectionFactory.Options()
        peerConnectionFactory = PeerConnectionFactory(options)

        val videoCapturerAndroid = createVideoCapturer()
        //val constraints = MediaConstraints()

        val videoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid)
        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)

        videoCapturerAndroid?.startCapture(1024, 720, 30)
        val videoView = findViewById<SurfaceViewRenderer>(R.id.surface_renderer)
        videoView.visibility = View.VISIBLE
        videoView.setMirror(true)

        val rootEglBase = EglBase.create()
        videoView.init(rootEglBase.eglBaseContext, null)

        localVideoTrack.addRenderer(VideoRenderer(videoView))

        SignallingClient.init(this)

        call()
    }

    private fun createVideoCapturer() : VideoCapturer? {
        return createCameraCapturer(Camera1Enumerator(false))
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames

        for (deviceName in deviceNames) {
            if (enumerator.isBackFacing(deviceName)) {
                val videoCapturer = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) return videoCapturer
            }
        }

        for (deviceName in deviceNames) {
            if (!enumerator.isBackFacing(deviceName)) {
                val videoCapturer = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) return videoCapturer
            }
        }

        return null
    }

    private fun call() {

        val sdpConstraints = MediaConstraints()
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveVideo", "true"))

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA

        localPeer = peerConnectionFactory.createPeerConnection(rtcConfig, sdpConstraints, object: CustomPeerConnectionObserver("localPeer") {
            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                onIceCandidateReceived(localPeer, p0!!)
            }
        })

        val stream = peerConnectionFactory.createLocalMediaStream("102")
        stream.addTrack(localVideoTrack)
        localPeer.addStream(stream)

        localPeer.createOffer(object: CustomSdpObserver("localCreateOffer") {
            override fun onCreateSuccess(p0: SessionDescription?) {
                super.onCreateSuccess(p0)
                Log.e("Offer", p0?.description!!)
                localPeer.setLocalDescription(CustomSdpObserver("localSetLocalDesc"), p0)
                SignallingClient.sendMessage(p0)
            }
        }, sdpConstraints)
    }

    private fun onIceCandidateReceived(peer: PeerConnection, iceCandidate: IceCandidate) {
        Log.e(TAG, "onIceCandidateReceived")
        if (peer != localPeer) {
            localPeer.addIceCandidate(iceCandidate)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
