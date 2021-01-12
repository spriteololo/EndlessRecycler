package com.spriteololo.endlessrecyclerview

import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.spriteololo.endlessrecyclerview.ProgressAdapterWrapper.Companion.PROGRESS_VIEW_TYPE
import com.spriteololo.endlessrecyclerview.ProgressViewHolder.Companion.inflate

class ProgressAdapterWrapper(
    private val progressLayoutId: Int,
    val innerAdapter: BaseEndlessAdapter<BaseEndlessViewHolder>,
    var progressEnabled: Boolean
) : RecyclerView.Adapter<ViewHolder>() {

    private var mObserver: AdapterDataObserver? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == PROGRESS_VIEW_TYPE) {
            inflate(progressLayoutId, parent)
        } else {
            innerAdapter.onCreateViewHolder(parent, viewType)
        }
    }

    override fun getItemCount(): Int {
        val innerItemCount = innerAdapter.itemCount
        return if (progressEnabled) innerItemCount + 1 else innerItemCount
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!progressEnabled || position != getProgressPosition()) {
            innerAdapter.onBindViewHolder(holder as BaseEndlessViewHolder, position)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (!progressEnabled || position != getProgressPosition()) {
            innerAdapter.onBindViewHolder(holder as BaseEndlessViewHolder, position, payloads)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (progressEnabled) {
            if (position == getProgressPosition()) PROGRESS_VIEW_TYPE
            else innerAdapter.getItemViewType(position)
        } else {
            innerAdapter.getItemViewType(position)
        }
    }

    override fun getItemId(position: Int): Long {
        return if (progressEnabled) {
            if (position == getProgressPosition()) {
                PROGRESS_VIEW_TYPE.toLong()
            } else {
                val itemId = innerAdapter.getItemId(position)
                if (itemId == PROGRESS_VIEW_TYPE.toLong()) {
                    throw IllegalStateException("Item has same id that progress has")
                } else {
                    itemId
                }
            }
        } else {
            innerAdapter.getItemId(position)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        if (holder is ProgressViewHolder) {
            super.onViewRecycled(holder)
        } else {
            innerAdapter.onViewRecycled(holder as BaseEndlessViewHolder)
        }
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
        innerAdapter.setHasStableIds(hasStableIds)
    }

    override fun onFailedToRecycleView(holder: ViewHolder): Boolean {
        return if (holder.adapterPosition == getProgressPosition()) {
            super.onFailedToRecycleView(holder)
        } else {
            innerAdapter.onFailedToRecycleView(holder as BaseEndlessViewHolder)
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        if (holder.adapterPosition != getProgressPosition()) {
            innerAdapter.onViewAttachedToWindow(holder as BaseEndlessViewHolder)
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        if (holder.adapterPosition != getProgressPosition()) {
            innerAdapter.onViewAttachedToWindow(holder as BaseEndlessViewHolder)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        innerAdapter.onAttachedToRecyclerView(recyclerView)
        mObserver ?: with(InnerAdapterDataObserverWrapper()) {
            mObserver = this
            innerAdapter.registerAdapterDataObserver(this)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        innerAdapter.onDetachedFromRecyclerView(recyclerView)
        mObserver?.let {
            innerAdapter.unregisterAdapterDataObserver(it)
            mObserver = null
        }
    }

    private fun getProgressPosition(): Int {
        return if (progressEnabled) itemCount - 1 else -1
    }

    private inner class InnerAdapterDataObserverWrapper : AdapterDataObserver() {
        override fun onChanged() {
            notifyDataSetChanged()
        }

        override fun onItemRangeChanged(
            @IntRange(from = 0) positionStart: Int,
            @IntRange(from = 1) itemCount: Int
        ) {
            notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeChanged(
            @IntRange(from = 0) positionStart: Int,
            @IntRange(from = 1) itemCount: Int,
            payload: Any?
        ) {
            notifyItemRangeChanged(positionStart, itemCount, payload)
        }

        override fun onItemRangeInserted(
            @IntRange(from = 0) positionStart: Int,
            @IntRange(from = 1) itemCount: Int
        ) {
            notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeRemoved(
            @IntRange(from = 0) positionStart: Int,
            @IntRange(from = 1) itemCount: Int
        ) {
            notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeMoved(
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            if (itemCount == 1) {
                notifyItemMoved(fromPosition, toPosition)
            } else {
                var from = fromPosition
                var to = toPosition
                val end = toPosition + itemCount
                while (from < end) {
                    notifyItemMoved(from, to) //TODO ??
                    from++
                    to++
                }
            }
        }
    }

    private object Companion {
        const val PROGRESS_VIEW_TYPE = 999999
    }
}