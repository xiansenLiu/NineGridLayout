package com.xiansenliu.ninegridlayout;

import android.util.ArrayMap;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by xinliu
 * Date       4/2/17
 * Time       21:13.
 */

public final class NineGridManager {
    private static final String TAG = "NineGridManager";
    private ArrayMap<Class, ViewDelegate> mClassViewDelegateArrayMap;


    public NineGridManager() {
        mClassViewDelegateArrayMap = new ArrayMap<>();
    }

    protected void addViewDelegates(ViewDelegate... viewDelegates) {
        for (ViewDelegate viewDelegate : viewDelegates) {
            mClassViewDelegateArrayMap.put(viewDelegate.getPojoType(), viewDelegate);
        }
    }

    public void removeViewDelegates(ViewDelegate... viewDelegates) {
        for (ViewDelegate viewDelegate : viewDelegates) {
            mClassViewDelegateArrayMap.remove(viewDelegate.getPojoType());
        }
    }

    public int getItemViewType(List items, int position) {
        Object o = items.get(position);
        Class<?> key = o.getClass();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return mClassViewDelegateArrayMap.indexOfKey(key);
        } else {
            int size = mClassViewDelegateArrayMap.size();
            for (int i = 0; i < size; i++) {
                ViewDelegate viewDelegate = mClassViewDelegateArrayMap.valueAt(i);
                if (viewDelegate.getPojoType() == key) {
                    return i;
                }
            }
            return -1;
        }
    }

    public BaseVH onCreateViewHolder(ViewGroup parent, int viewType, List items) {
        ViewDelegate viewDelegate = mClassViewDelegateArrayMap.valueAt(viewType);
        BaseVH vh = viewDelegate.onCreateViewHolder(parent);
        return vh;
    }

    public void onBindViewHolder(BaseVH holder, int position, List items) {
        Object o = items.get(position);
        Class<?> key = o.getClass();
        ViewDelegate viewDelegate = mClassViewDelegateArrayMap.get(key);
        viewDelegate.onBindViewHolder(holder, position,o, items);
    }
}
