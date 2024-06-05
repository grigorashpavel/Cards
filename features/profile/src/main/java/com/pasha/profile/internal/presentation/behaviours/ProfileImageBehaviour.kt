package com.pasha.profile.internal.presentation.behaviours

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView


class ProfileImageBehaviour(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<ShapeableImageView>(context, attrs) {

    private var startX: Float? = null
    private var startY: Float? = null
    private var startSize: Float? = null

    private val endSize = 0f

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: ShapeableImageView,
        dependency: View
    ): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: ShapeableImageView,
        dependency: View
    ): Boolean {
        if (dependency is AppBarLayout) {
            if (startX == null) {
                startX = child.x
                startY = child.y
                startSize = child.scaleX
            }

            val totalScrollRange = dependency.totalScrollRange.toFloat()
            val currentScroll = -dependency.y
            val progress = currentScroll / totalScrollRange

            val endX = parent.width - child.width.toFloat()
            val currentX = startX!! - (endX - startX!!) * progress * 1.6f
            child.x = currentX

            val currentSize = startSize!! + (endSize - startSize!!) * progress
            child.scaleX = currentSize
            child.scaleY = currentSize

            return true
        }
        return false
    }
}