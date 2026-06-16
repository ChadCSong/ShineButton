package com.sackcentury.shinebuttonlib.listener

import android.animation.Animator

/**
 * Created by SongChao on 2019-06-14
 */
open class SimpleAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationStart(animator: Animator) {}

    override fun onAnimationEnd(animator: Animator) {}

    override fun onAnimationCancel(animator: Animator) {}

    override fun onAnimationRepeat(animator: Animator) {}
}
