package com.docway.live.sdk

import android.util.Log
import com.twilio.video.*

internal class RemoteParticipantListener(private val listener: RoomListener):
        RemoteParticipant.Listener {

    override fun onAudioTrackEnabled(remoteParticipant: RemoteParticipant?, remoteAudioTrackPublication: RemoteAudioTrackPublication?) {
        Log.i("Live", "onAudioTrackEnabled")
    }
    override fun onAudioTrackDisabled(remoteParticipant: RemoteParticipant?, remoteAudioTrackPublication: RemoteAudioTrackPublication?) {
        Log.i("Live", "onAudioTrackDisabled")
    }
    override fun onAudioTrackPublished(remoteParticipant: RemoteParticipant?, remoteAudioTrackPublication: RemoteAudioTrackPublication?) {
        Log.i("Live", "onAudioTrackPublished")
    }
    override fun onAudioTrackUnpublished(remoteParticipant: RemoteParticipant?, remoteAudioTrackPublication: RemoteAudioTrackPublication?) {
        Log.i("Live", "onAudioTrackUnpublished")
    }
    override fun onAudioTrackSubscribed(remoteParticipant: RemoteParticipant?, remoteAudioTrackPublication: RemoteAudioTrackPublication?, remoteAudioTrack: RemoteAudioTrack?) {
        Log.i("Live", "onAudioTrackSubscribed")
    }
    override fun onAudioTrackUnsubscribed(remoteParticipant: RemoteParticipant?, remoteAudioTrackPublication: RemoteAudioTrackPublication?, remoteAudioTrack: RemoteAudioTrack?) {
        Log.i("Live", "onAudioTrackUnsubscribed")
    }
    override fun onAudioTrackSubscriptionFailed(remoteParticipant: RemoteParticipant?, remoteAudioTrackPublication: RemoteAudioTrackPublication?, twilioException: TwilioException?) {
        Log.i("Live", "onAudioTrackSubscriptionFailed")
    }

    override fun onVideoTrackEnabled(remoteParticipant: RemoteParticipant?, remoteVideoTrackPublication: RemoteVideoTrackPublication?) {
        Log.i("Live", "onVideoTrackEnabled")
    }
    override fun onVideoTrackDisabled(remoteParticipant: RemoteParticipant?, remoteVideoTrackPublication: RemoteVideoTrackPublication?) {
        Log.i("Live", "onVideoTrackDisabled")
    }
    override fun onVideoTrackPublished(remoteParticipant: RemoteParticipant?, remoteVideoTrackPublication: RemoteVideoTrackPublication?) {
        Log.i("Live", "onVideoTrackPublished")
        listener.onVideoRemotePublished()
    }
    override fun onVideoTrackUnpublished(remoteParticipant: RemoteParticipant?, remoteVideoTrackPublication: RemoteVideoTrackPublication?) {
        Log.i("Live", "onVideoTrackUnpublished")
        listener.onVideoRemoteUnpublished()
    }
    override fun onVideoTrackSubscribed(remoteParticipant: RemoteParticipant?, remoteVideoTrackPublication: RemoteVideoTrackPublication?, remoteVideoTrack: RemoteVideoTrack) {
        Log.i("Live", "onVideoTrackSubscribed")
        listener.addRemoteParticipantVideo(remoteVideoTrack)
    }
    override fun onVideoTrackUnsubscribed(remoteParticipant: RemoteParticipant?, remoteVideoTrackPublication: RemoteVideoTrackPublication?, remoteVideoTrack: RemoteVideoTrack) {
        Log.i("Live", "onVideoTrackUnsubscribed")
        listener.removeRemoteParticipantVideo(remoteVideoTrack)
    }
    override fun onVideoTrackSubscriptionFailed(remoteParticipant: RemoteParticipant?, remoteVideoTrackPublication: RemoteVideoTrackPublication?, twilioException: TwilioException?) {
        Log.i("Live", "onVideoTrackSubscriptionFailed")
    }

    override fun onDataTrackPublished(remoteParticipant: RemoteParticipant?, remoteDataTrackPublication: RemoteDataTrackPublication?) {
        Log.i("Live", "onDataTrackPublished")
    }
    override fun onDataTrackUnpublished(remoteParticipant: RemoteParticipant?, remoteDataTrackPublication: RemoteDataTrackPublication?) {
        Log.i("Live", "onDataTrackUnpublished")
    }
    override fun onDataTrackSubscribed(remoteParticipant: RemoteParticipant?, remoteDataTrackPublication: RemoteDataTrackPublication?, remoteDataTrack: RemoteDataTrack?) {
        Log.i("Live", "onDataTrackSubscribed")
    }
    override fun onDataTrackUnsubscribed(remoteParticipant: RemoteParticipant?, remoteDataTrackPublication: RemoteDataTrackPublication?, remoteDataTrack: RemoteDataTrack?) {
        Log.i("Live", "onDataTrackUnsubscribed")
    }
    override fun onDataTrackSubscriptionFailed(remoteParticipant: RemoteParticipant?, remoteDataTrackPublication: RemoteDataTrackPublication?, twilioException: TwilioException?) {
        Log.i("Live", "onDataTrackSubscriptionFailed")
    }
}