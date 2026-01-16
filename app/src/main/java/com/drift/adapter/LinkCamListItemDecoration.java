package com.drift.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by admin on 2017-10-26.
 */

public class LinkCamListItemDecoration extends RecyclerView.ItemDecoration{
    private int topSpace;
    private int bottomSpace;

    public LinkCamListItemDecoration(int top, int bottom) {
        this.topSpace = top;
        this.bottomSpace = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = topSpace;
        outRect.bottom = bottomSpace;
    }
}