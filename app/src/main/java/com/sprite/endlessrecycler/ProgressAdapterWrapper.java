package com.sprite.endlessrecycler;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProgressAdapterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private boolean mProgressEnabled;
    private RecyclerView.Adapter innerAdapter;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType != R.id.erv_progress) {
            return innerAdapter.onCreateViewHolder(parent, viewType);
        } else {
            return ProgressViewHolder.inflate(mEndlessRecyclerView.progressLayoutId, parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (!mProgressEnabled || position != getProgressPosition()) {
            mAdapter.onBindViewHolder(holder, position, payloads);
        }
    }

    private int getProgressPosition(){
        return mProgressEnabled ? innerAdapter.getItemCount() + 1 : innerAdapter.getItemCount();
    }

    @Override
    public int getItemCount() {
        int innerItemCount = innerAdapter.getItemCount();
        return mProgressEnabled ? innerItemCount + 1 : innerItemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (mProgressEnabled) {
            return position == getProgressPosition() ?
                    R.id.erv_progress : mAdapter.getItemViewType(position);
        } else {
            return mAdapter.getItemViewType(position);
        }
    }

    static class Companion{
        final int PROGRESS_VIEW_TYPE = 999;
    }
}
