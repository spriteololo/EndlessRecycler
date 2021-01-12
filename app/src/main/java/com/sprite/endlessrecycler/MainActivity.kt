package com.sprite.endlessrecycler

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spriteololo.endlessrecyclerview.EndlessRecyclerView

class MainActivity : AppCompatActivity(), EndlessRecyclerView.EndlessScrollListener {
    lateinit var adapter: LolAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<EndlessRecyclerView>(R.id.recycler)
        recycler.layoutManager = GridLayoutManager(this, 5)
        with(LolAdapter()) {
            adapter = this
            recycler.setAdapter(this)
        }
        recycler.endlessScrollListener = this
    }

    override fun onLoadMore() {
        Handler(Looper.getMainLooper()).postDelayed({ adapter.addItems() }, 1000)
    }
}

class LolAdapter : RecyclerView.Adapter<LolAdapter.CustomViewHolder>() {
    private val items: ArrayList<String> = arrayListOf("0", "1", "2")

    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv: TextView = view.findViewById(R.id.tv_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_text, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val item = items[position]
        holder.tv.text = item
    }

    fun addItems() {
        val size = items.size
        items.add(size.toString())
        notifyItemInserted(size)
    }
}