package com.sprite.endlessrecycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class EndlessRecyclerView extends RecyclerView {

    public EndlessRecyclerView(Context context) {
        this(context, null);
    }

    public EndlessRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EndlessRecyclerView(Context context, @Nullable AttributeSet attrSet, int defStyle) {
        super(context, attrSet, defStyle);

        /*TypedArray attrs = context.obtainStyledAttributes(
                attrSet, R.styleable.EndlessRecyclerView, defStyle, 0);
        endlessScrollEnable =
                attrs.getBoolean(R.styleable.EndlessRecyclerView_erv_endlessScrollEnabled, true);
        mVisibleThreshold = attrs.getInt(R.styleable.EndlessRecyclerView_erv_visibleThreshold,
                DEFAULT_VISIBLE_THRESHOLD);
        progressLayoutId = attrs.getResourceId(R.styleable.EndlessRecyclerView_erv_progressLayout,
                R.layout.erv_progress);
        attrs.recycle();

        setEndlessScrollEnableInner(endlessScrollEnable);*/
    }

    @Nullable
    @Override
    public Adapter getAdapter() {
        return super.getAdapter();
    }
}
