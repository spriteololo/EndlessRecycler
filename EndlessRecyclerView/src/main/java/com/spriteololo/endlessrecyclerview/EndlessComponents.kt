package com.spriteololo.endlessrecyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessAdapter<T : EndlessViewHolder> : RecyclerView.Adapter<T>()
abstract class EndlessViewHolder(view: View) : RecyclerView.ViewHolder(view)