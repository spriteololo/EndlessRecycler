package com.spriteololo.endlessrecyclerview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.annotation.IntRange
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EndlessRecyclerView : RecyclerView {
    private val HANDLER = Handler(Looper.getMainLooper())
    private var progressLayoutId = 0
    private var mVisibleThreshold: Int = DEFAULT_VISIBLE_THRESHOLD
    private var endlessScrollEnabled = true
    private var loading = true

    private val loadingAdapterDataObserver: LoadingAdapterDataObserver =
        LoadingAdapterDataObserver()
    var endlessScrollListener: EndlessScrollListener? = null
        set(value) {
            if (field == null && value != null) {
                field = value
                attachEndlessScrollListener()
            } else if (value == null) {
                detachEndlessScrollListener()
            }
        }

    private var endlessRecyclerOnScrollListener: EndlessRecyclerOnScrollListener? = null

    private var onLoadMoreRunnable: Runnable? = null

    private var mAdapter: ProgressAdapterWrapper? = null

    private var previousTotalCount = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrSet: AttributeSet?, defStyle: Int) : super(
        context,
        attrSet,
        defStyle
    ) {
        isSaveEnabled = true
        val attrs = context.obtainStyledAttributes(
            attrSet, R.styleable.EndlessRecyclerView, defStyle, 0
        )
        endlessScrollEnabled =
            attrs.getBoolean(R.styleable.EndlessRecyclerView_erv_endlessScrollEnabled, true)
        mVisibleThreshold = attrs.getInt(
            R.styleable.EndlessRecyclerView_erv_visibleThreshold,
            DEFAULT_VISIBLE_THRESHOLD
        )
        progressLayoutId = attrs.getResourceId(
            R.styleable.EndlessRecyclerView_erv_progressLayout,
            R.layout.erv_progress
        )
        attrs.recycle()

        setEndlessScrollEnableInner(endlessScrollEnabled)
    }

    interface EndlessScrollListener {
        fun onLoadMore()
    }

    fun setEndlessScrollEnable(enable: Boolean) {
        if (endlessScrollEnabled != enable) {
            setEndlessScrollEnableInner(enable)
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        mAdapter?.unregisterAdapterDataObserver(loadingAdapterDataObserver)

        mAdapter = if (adapter != null) {
            val progressEnabled = endlessScrollEnabled && progressLayoutId != 0
            ProgressAdapterWrapper(progressLayoutId, adapter as BaseEndlessAdapter<BaseEndlessViewHolder>, progressEnabled)
        } else {
            null
        }

        super.setAdapter(mAdapter)

        loading = false
        mAdapter?.let {
            it.registerAdapterDataObserver(loadingAdapterDataObserver)
            attachEndlessScrollListener()
        } ?: detachEndlessScrollListener()

        viewTreeObserver.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    loadMore()
                }
            })
    }

    override fun setLayoutManager(layoutManager: LayoutManager?) {
        super.setLayoutManager(layoutManager)
        if (layoutManager == null) {
            detachEndlessScrollListener()
        } else {
            attachEndlessScrollListener()
        }
    }

    private fun detachEndlessScrollListener() {
        mAdapter?.progressEnabled = false

        onLoadMoreRunnable?.let {
            HANDLER.removeCallbacks(it)
            onLoadMoreRunnable = null
        }

        loading = false
        endlessRecyclerOnScrollListener?.let {
            removeOnScrollListener(it)
            endlessRecyclerOnScrollListener = null
        }
    }

    private fun attachEndlessScrollListener() {
        if (!endlessScrollEnabled) {
            return
        }
        if (mAdapter != null && layoutManager != null && endlessScrollListener != null) {

            endlessRecyclerOnScrollListener?.let { addOnScrollListener(it) } ?: with(
                EndlessRecyclerOnScrollListener()
            ) {
                endlessRecyclerOnScrollListener = this
                addOnScrollListener(this)
            }

            mAdapter?.progressEnabled = true
        }

        loadMore()
    }

    @IntRange(from = 1)
    fun getVisibleThreshold(): Int {
        return mVisibleThreshold
    }

    fun setVisibleThreshold(@IntRange(from = 1) threshold: Int) {
        require(mVisibleThreshold > 0) { "Visible threshold must be positive value." }
        if (endlessRecyclerOnScrollListener == null) {
            if (mVisibleThreshold != threshold) {
                mVisibleThreshold = threshold
                detachEndlessScrollListener()
                attachEndlessScrollListener()
            }
        } else {
            throw UnsupportedOperationException(
                "Changing visible threshold is only possible when RecyclerView doesn't have adapter and layout manager."
            )
        }
    }

    private fun setEndlessScrollEnableInner(enable: Boolean) {
        endlessScrollEnabled = enable
        if (endlessScrollEnabled) {
            attachEndlessScrollListener()
        } else {
            detachEndlessScrollListener()
        }
    }

    override fun getAdapter(): BaseEndlessAdapter<BaseEndlessViewHolder>? {
        return mAdapter?.innerAdapter
    }

    private fun loadMore() {
        if (!endlessScrollEnabled || endlessScrollListener == null) {
            return
        }
        val visibleItemCount = childCount
        val totalItemCount = layoutManager?.itemCount ?: -1

        val firstVisibleItem = layoutManager?.let {
            findFirstVisibleItemPosition(
                it
            )
        } ?: -1
        if (totalItemCount < 0 || firstVisibleItem < 0) {
            return
        }

        if (loading && totalItemCount > previousTotalCount) {
            loading = false
            previousTotalCount = totalItemCount
        }

        if (!loading && totalItemCount <= firstVisibleItem + visibleItemCount + mVisibleThreshold) {
            onLoadMoreRunnable?.let { removeCallbacks(it) }
            with(OnLoadMoreRunnable()) {
                onLoadMoreRunnable = this
                HANDLER.post(this)
            }
            loading = true
        }
    }

    private inner class EndlessRecyclerOnScrollListener : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dx != 0 || layoutManager?.run {
                    isVertical(
                        this
                    )
                } == true) {
                loadMore()
            }
        }
    }

    private inner class OnLoadMoreRunnable : Runnable {
        override fun run() {
            if (endlessScrollEnabled && context != null) {
                endlessScrollListener?.onLoadMore()
            }
            onLoadMoreRunnable = null
        }
    }

    private inner class LoadingAdapterDataObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            resetLoadingAndLoadMore()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            resetLoadingAndLoadMore()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            resetLoadingAndLoadMore()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            resetLoadingAndLoadMore()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            resetLoadingAndLoadMore()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            resetLoadingAndLoadMore()
        }

        fun resetLoadingAndLoadMore() {
            loading = false
            loadMore()
        }
    }

    private companion object {
        private const val DEFAULT_VISIBLE_THRESHOLD = 10

        private fun findFirstVisibleItemPosition(layoutManager: LayoutManager): Int {
            return if (layoutManager is LinearLayoutManager) {
                layoutManager.findFirstVisibleItemPosition()
            } else {
                throw UnsupportedOperationException()
            }
        }

        private fun isVertical(layoutManager: LayoutManager): Boolean {
            return if (layoutManager is LinearLayoutManager) {
                layoutManager.orientation == LinearLayoutManager.VERTICAL
            } else {
                throw UnsupportedOperationException()
            }
        }
    }
}