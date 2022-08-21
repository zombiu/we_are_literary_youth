package com.example.we_youth.widget

import android.animation.ObjectAnimator
import android.view.View

class TranslationAnimUtils(val view: View) {

    fun getTransAnimatoion(toValue: Float): ObjectAnimator {
        return ObjectAnimator.ofFloat(
            view,
            "translationY",
            view.translationY,
            toValue
        )
    }
}