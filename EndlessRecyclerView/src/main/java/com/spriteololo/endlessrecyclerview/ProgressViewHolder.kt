package com.spriteololo.endlessrecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder

open class ProgressViewHolder(itemView: View) : ViewHolder(itemView) {
    companion object {
        fun inflate(@LayoutRes layoutResId: Int, parent: ViewGroup): ProgressViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(layoutResId, parent, false)
            return ProgressViewHolder(view)
        }
    }
}