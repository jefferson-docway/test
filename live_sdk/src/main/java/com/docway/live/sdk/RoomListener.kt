package com.docway.live.sdk

import com.twilio.video.RemoteVideoTrack

internal interface RoomListener {
    fun addRemoteParticipantVideo(remoteVideoTrack: RemoteVideoTrack)
    fun removeRemoteParticipantVideo(remoteVideoTrack: RemoteVideoTrack)

    fun onVideoRemotePublished()
    fun onVideoRemoteUnpublished()
}