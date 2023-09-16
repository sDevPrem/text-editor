package com.sdevprem.basictexteditor.common

import android.animation.ObjectAnimator
import android.view.View

fun View.animateDown() {
    ObjectAnimator.ofFloat(this, "translationY", height.toFloat()).apply {
        duration = 200
        start()
    }
}

fun View.animateUp() {
    ObjectAnimator.ofFloat(this, "translationY", -height.toFloat()).apply {
        duration = 200
        start()
    }
}