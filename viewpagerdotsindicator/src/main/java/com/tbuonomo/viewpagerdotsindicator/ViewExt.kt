package com.tbuonomo.viewpagerdotsindicator

import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

fun View.setPaddingHorizontal(padding: Int) {
  setPadding(padding, paddingTop, padding, paddingBottom)
}

fun View.setPaddingVertical(padding: Int) {
  setPadding(paddingLeft, padding, paddingRight, padding)
}

private fun DotsIndicator.attachToRecyclerView(recyclerView: RecyclerView, snapHelper: PagerSnapHelper) {
  if (recyclerView.adapter == null) {
    throw IllegalStateException("You have to set an adapter to the recyccler view before initializing the dots indicator !")
  }

  if (recyclerView.layoutManager == null) {
    throw IllegalStateException("You have to set a layout manager to the recyccler view before initializing the dots indicator !")
  }

  val adapter = recyclerView.adapter!!
  val layoutManager = recyclerView.layoutManager!!

  recyclerView.adapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
    override fun onChanged() {
      super.onChanged()
      refreshDots()
    }
  })

  pager = object : BaseDotsIndicator.Pager {
    var onScrollListener: RecyclerView.OnScrollListener? = null

    override val isNotEmpty: Boolean
      get() = adapter.itemCount > 0

    override val currentItem: Int
      get() = layoutManager.getPosition(snapHelper.findSnapView(layoutManager)!!)

    override val isEmpty: Boolean
      get() = adapter.itemCount == 0

    override val count: Int
      get() = adapter.itemCount

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
      if (smoothScroll) {
        recyclerView.smoothScrollToPosition(item)
      } else {
        recyclerView.scrollToPosition(item)
      }
    }

    override fun removeOnPageChangeListener() {
      onScrollListener?.let(recyclerView::removeOnScrollListener)
    }

    override fun addOnPageChangeListener(
      onPageChangeListenerHelper: OnPageChangeListenerHelper
    ) {
      onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          val snapPosition = layoutManager.getPosition(snapHelper.findSnapView(layoutManager)!!)
          onPageChangeListenerHelper.onPageScrolled(snapPosition, dx.toFloat())
        }
      }
      recyclerView.addOnScrollListener(onScrollListener!!)
    }
  }

  refreshDots()
}