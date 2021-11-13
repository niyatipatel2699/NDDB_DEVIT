package com.nddb.kudamforkurien.backgroundservice

/**
 *
 *
 * Created by sinku on 10.11.2021.
 */
internal class MotionActivity(val id: Int, private var previousSteps: Int) {
    internal var steps: Int = 0
        private set
    internal var active: Boolean = false
        private set

    init {
        this.active = true
    }

    internal fun update(currentSteps: Int) {
        if (active) {
            steps += currentSteps - previousSteps
        }
        previousSteps = currentSteps
    }

    internal fun toggle() {
        active = !active
    }
}