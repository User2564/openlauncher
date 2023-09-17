package com.benny.openlauncher.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CenteredLayoutManager extends LinearLayoutManager {

    public CenteredLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        int parentWidth = getWidth();
        int totalWidth = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            totalWidth += getDecoratedMeasuredWidth(child);
        }

        int leftOffset = (parentWidth - totalWidth) / 2;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            layoutDecoratedWithMargins(
                    child,
                    leftOffset,
                    child.getTop(),
                    leftOffset + getDecoratedMeasuredWidth(child),
                    child.getBottom()
            );

            leftOffset += getDecoratedMeasuredWidth(child);
        }
    }
}
