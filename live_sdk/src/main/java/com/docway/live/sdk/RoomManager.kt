package com.docway.live.sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log
import android.view.View
import com.twilio.video.*

class RoomManager(
        private val view: VideoCallView,
        private val apiKey: String,
        private val roomName: String
): RoomListener {

    private var room: Room? =  null
    private var localParticipant: LocalParticipant? = null

    private var localAudioTrack: LocalAudioTrack? = null
    private var localVideoTrack: LocalVideoTrack? = null
    private lateinit var localVideoView: VideoView
    private lateinit var remoteVideoView: VideoView

    private var participantIdentity: String = ""

    private val cameraCapturer: CameraCapturer
    private val participantListener by lazy { RemoteParticipantListener(this) }
    private val receiver: VideoCallReceiver

    init {
        cameraCapturer = CameraCapturer(view.getContext(), CameraCapturer.CameraSource.FRONT_CAMERA)
        receiver = VideoCallReceiver()
    }

    fun connect() {
        createVideoViews()
        createAudioAndVideoTracks()
        enableAudio(true)

        val connectOptions = ConnectOptions.Builder(apiKey)
                .roomName(roomName)
                .audioTracks(listOf(localAudioTrack))
                .videoTracks(listOf(localVideoTrack))
                .build()

        room = Video.connect(view.getContext(), connectOptions, object: Room.Listener {
            override fun onConnected(room: Room) {
                Log.d("Live","Connected in " + room.name)
                localParticipant = room.localParticipant
                localVideoTrack?.let { localParticipant?.publishTrack(it) }
                room.remoteParticipants.firstOrNull()?.let { addRemoteParticipant(it) }
            }

            override fun onDisconnected(room: Room?, twilioException: TwilioException?) {
                Log.d("Live","Disconnected from " + room?.name)
                localParticipant = null
                this@RoomManager.room = null
            }

            override fun onConnectFailure(room: Room?, twilioException: TwilioException?) {
                Log.e("Live", "TwilioException: " + twilioException?.message)
                view.onError()
            }

            override fun onParticipantConnected(room: Room?, remoteParticipant: RemoteParticipant) {
                Log.i("Live", remoteParticipant.identity + " has joined the room.")
                addRemoteParticipant(remoteParticipant)
            }

            override fun onParticipantDisconnected(room: Room?, remoteParticipant: RemoteParticipant) {
                Log.i("Live", remoteParticipant.identity + " has left the room.")
                removeRemoteParticipant(remoteParticipant)
            }

            override fun onRecordingStarted(room: Room?) {
                Log.d("Live", "onRecordingStarted")
            }

            override fun onRecordingStopped(room: Room?) {
                Log.d("Live", "onRecordingStopped")
            }
        })
    }

    private fun createVideoViews() {
        localVideoView = VideoView(view.getContext())
        localVideoView.applyZOrder(true)
        localVideoView.mirror = true

        remoteVideoView = VideoView(view.getContext())
        remoteVideoView.mirror = true
        view.onAddVideoViews(localVideoView, remoteVideoView)
    }

    private fun createAudioAndVideoTracks() {
        if (localAudioTrack == null) {
            localAudioTrack = LocalAudioTrack.create(view.getContext(), true)
        }
        if (localVideoTrack == null) {
            localVideoTrack = LocalVideoTrack.create(view.getContext(), true, cameraCapturer)
        }
    }

    private fun configureVideoTrack() {
        localVideoTrack?.addRenderer(localVideoView as VideoRenderer)
        localVideoTrack?.let { localParticipant?.publishTrack(it) }
    }

    fun resume() {
        view.getContext().registerReceiver(receiver, IntentFilter(Intent.ACTION_HEADSET_PLUG))
        if (view.getContext() is Activity) {
            (view.getContext() as Activity).volumeControlStream = AudioManager.STREAM_VOICE_CALL
        }

        createAudioAndVideoTracks()
        configureVideoTrack()
    }

    fun pause() {
        view.getContext().unregisterReceiver(receiver)
        localVideoTrack?.let { localParticipant?.unpublishTrack(it) }
        localVideoTrack?.release()
        localVideoTrack = null
    }

    fun disconnect() {
        room?.disconnect()
        localAudioTrack?.release()
        localVideoTrack?.release()
    }

    fun cycleCamera() {
        cameraCapturer.switchCamera()
    }

    fun enableAudio(enable: Boolean) {
        val audioManager = view.getContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        localAudioTrack?.enable(enable)
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION


        /*
        if (enable) {
            requestAudioFocus()
        } else {
            abandonAudioFocus()
        }
        */
    }

    private fun requestAudioFocus() {
        val audioManager = view.getContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val playbackAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener { }
                    .build()
            audioManager.requestAudioFocus(focusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        }
    }

    private fun abandonAudioFocus() {
        val audioManager = view.getContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        @Suppress("DEPRECATION")
        audioManager.abandonAudioFocus(null)
    }

    private fun addRemoteParticipant(remoteParticipant: RemoteParticipant) {
        participantIdentity = remoteParticipant.identity
        view.onLoadRemoteName(participantIdentity)
        view.onVideoRemotePublished()

        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {
                remoteVideoTrackPublication.remoteVideoTrack?.let {
                    addRemoteParticipantVideo(it)
                }
            }
        }

        remoteParticipant.setListener(participantListener)
    }

    override fun addRemoteParticipantVideo(remoteVideoTrack: RemoteVideoTrack) {
        remoteVideoTrack.addRenderer(remoteVideoView as VideoRenderer)
    }

    private fun removeRemoteParticipant(remoteParticipant: RemoteParticipant) {
        view.onRemoteDisconnect()

        if (remoteParticipant.identity.equals(participantIdentity)) {
            return
        }

        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {
                remoteVideoTrackPublication.remoteVideoTrack?.let {
                    removeRemoteParticipantVideo(it)
                }
            }
        }
    }

    override fun removeRemoteParticipantVideo(remoteVideoTrack: RemoteVideoTrack) {
        remoteVideoTrack.removeRenderer(remoteVideoView)
    }

    override fun onVideoRemotePublished() {
        remoteVideoView.visibility = View.VISIBLE
        view.onVideoRemotePublished()
    }

    override fun onVideoRemoteUnpublished() {
        remoteVideoView.visibility = View.GONE
        view.onVideoRemoteUnpublished()
    }
}