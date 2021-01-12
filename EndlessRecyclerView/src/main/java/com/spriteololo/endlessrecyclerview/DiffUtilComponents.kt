package com.spriteololo.endlessrecyclerview

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

open class BaseDiffUtil<T>(
    private val oldList: List<T>,
    private val newList: List<T>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return areItemsTheSame(oldItem, newItem)
    }

    open fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return areContentsTheSame(oldItem, newItem)
    }

    open fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
}

fun DiffUtil.Callback.calculateDiffAndSendToAdapter(
    adapter: RecyclerView.Adapter<*>,
    detectMoves: Boolean = true
) {
    DiffUtil.calculateDiff(this, detectMoves).dispatchUpdatesTo(adapter)
}