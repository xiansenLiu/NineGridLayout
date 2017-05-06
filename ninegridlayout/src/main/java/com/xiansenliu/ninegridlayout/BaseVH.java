package com.xiansenliu.ninegridlayout;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by xinliu
 * Date       4/2/17
 * Time       17:55.
 */

public class BaseVH {
    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    private boolean flag;
    public View mItemView;
    private SparseArray<View> views;

    public BaseVH(View itemView) {
        if (itemView == null) {
            throw new IllegalArgumentException("the itemView should not be null");
        }
        mItemView = itemView;
    }

    public final <T extends View> T getView(int viewId, Class<T> viewType) {
        if (views == null) {
            views = new SparseArray<>();
        }
        View view = views.get(viewId);
        if (view == null) {
            view = mItemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return viewType.cast(view);
    }
}
