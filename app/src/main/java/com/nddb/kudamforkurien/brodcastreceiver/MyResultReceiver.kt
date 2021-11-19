package com.nddb.kudamforkurien.brodcastreceiver

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver

class MyResultReceiver(handler: Handler?) : ResultReceiver(handler) {
    private var mReceiver: Receiver? = null
    fun setReceiver(receiver: Receiver) {
        mReceiver = receiver
    }

    interface Receiver {
        fun onReceiveResult(command: Int, resultData: Bundle?)
    }

    override fun onReceiveResult(command: Int, resultData: Bundle?) {
        if (mReceiver != null) {
            mReceiver!!.onReceiveResult(command, resultData)
        }
    }
}