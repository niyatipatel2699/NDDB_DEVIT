package com.devit.nddb.backgroundservice

interface StepListener {
    fun step(timeNs: Long)
}