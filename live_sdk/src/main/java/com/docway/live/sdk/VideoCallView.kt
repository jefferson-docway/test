package com.docway.live.sdk

import android.content.Context
import android.view.View

interface VideoCallView {
    fun getContext(): Context
    fun onAddVideoViews(localVideoView: View, remoteVideoView: View)
    fun onLoadRemoteName(remoteName: String)
    fun onVideoRemotePublished()
    fun onVideoRemoteUnpublished()
    fun onRemoteDisconnect()
    fun onError()
}