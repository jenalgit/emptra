package com.example.multiframe;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
    private OnScrollViewListener mListener = null;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setOnScrollViewListener(OnScrollViewListener listener) {
       mListener = listener;
    }
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
       super.onScrollChanged(l, t, oldl, oldt);
       if (mListener != null) {
           mListener.onScrollChanged(this, l, t, oldl, oldt);
       }
    }

    public static interface OnScrollViewListener {
       //public void onScrollChanged(OnScrollViewListener listener);
       void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy);
    }
}