package com.xeross.anniveraire.listener

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewClickListener(private val context: Context,
                                private val recyclerView: RecyclerView, private val clickListener: ClickListener) : RecyclerView.OnItemTouchListener {

    private var gestureDetector: GestureDetector? = null

    init {
        gestureDetector = GestureDetector(context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapUp(e: MotionEvent?) = true

                    override fun onLongPress(motionEvent: MotionEvent?) {
                        motionEvent?.let { e ->
                            val child = recyclerView.findChildViewUnder(e.x, e.y)
                            child?.let {
                                clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child))
                            }
                        }
                    }
                })
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = rv.findChildViewUnder(e.x, e.y)
        child?.let {
            gestureDetector?.takeIf { it.onTouchEvent(e) }?.let {
                clickListener.onClick(child, rv.getChildLayoutPosition(child))
            }
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}