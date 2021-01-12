package com.spriteololo.endlessrecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.spriteololo.observablecollections.ArrayListListener
import com.spriteololo.observablecollections.ObservableArrayList

abstract class BaseEndlessAdapter<T : BaseEndlessViewHolder> : RecyclerView.Adapter<T>()
abstract class BaseEndlessViewHolder(view: View) : RecyclerView.ViewHolder(view)

abstract class LightEndlessAdapter<ViewHolder : BaseEndlessViewHolder, T : Any> :
    BaseEndlessAdapter<ViewHolder>(), ArrayListListener<T> {

    abstract val itemLayoutRes: Int
    abstract fun createViewHolder(view: View): ViewHolder

    abstract fun bindElementAt(holder: RecyclerView.ViewHolder, position: Int, item: T)

    var items: ObservableArrayList<T> = ObservableArrayList()
        set(value) {
            if (field != value) {
                value.setListener(null)
                field = value
                value.setListener(this)
            }
        }

    open fun getDiffUtil(oldList: List<T>, newList: List<T>) = BaseDiffUtil(oldList, newList)

    private fun recalculate(oldList: List<T>, newList: List<T>) {
        getDiffUtil(oldList, newList).calculateDiffAndSendToAdapter(this)
    }

    override fun onChanged(item: T, index: Int) {
        notifyItemChanged(index)
    }

    override fun onCleared() {
        notifyDataSetChanged()
    }

    override fun onInsertCollection(index: Int, oldSnapshot: Collection<T>) {
        recalculate(oldSnapshot as ArrayList<T>, items)
    }

    override fun onInsertItem(item: T, index: Int) {
        notifyItemInserted(index)
    }

    override fun onRemovedItem(oldSnapshot: Collection<T>) {
        recalculate(oldSnapshot as ArrayList<T>, items)
    }

    override fun onRemovedItem(item: T, index: Int) {
        notifyItemRemoved(index)
    }

    override fun onRemovedItems(fromIndex: Int, toIndex: Int) {
        notifyItemRangeRemoved(fromIndex, toIndex - fromIndex)
    }

    override fun onRemovedItems(oldSnapshot: Collection<T>) {
        recalculate(oldSnapshot as ArrayList<T>, items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(itemLayoutRes, parent, false)
        return createViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        bindElementAt(holder, position, item)
    }
}