package com.sprite.endlessrecycler

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.spriteololo.endlessrecyclerview.BaseEndlessViewHolder
import com.spriteololo.endlessrecyclerview.EndlessRecyclerView
import com.spriteololo.endlessrecyclerview.LightEndlessAdapter

class MainActivity : AppCompatActivity(), EndlessRecyclerView.EndlessScrollListener {
    lateinit var adapter: LolAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<EndlessRecyclerView>(R.id.recycler)
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

class LolAdapter : LightEndlessAdapter<LolAdapter.CustomViewHolder, String>() {

    override val itemLayoutRes = R.layout.item_text

    override fun createViewHolder(view: View): CustomViewHolder {
        return CustomViewHolder(view)
    }

    override fun bindElementAt(holder: CustomViewHolder, position: Int, item: String) {
        holder.tv.text = item
    }

    class CustomViewHolder(view: View) : BaseEndlessViewHolder(view) {
        val tv: TextView = view.findViewById(R.id.tv_title)
    }
}