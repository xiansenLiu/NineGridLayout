package com.xiansenliu.ninegridlayout;

import android.util.Log;
import android.view.ViewGroup;

import java.util.Observable;

/**
 * Created by xinliu
 * Date       4/3/17
 * Time       13:06.
 */

public abstract class NineGridAdapter<VH extends BaseVH> extends Observable {
    private static final String TAG = "NineGridAdapter";
    public abstract int getItemCount();

    public int getItemViewType(int position) {
        return 0;
    }

    public final VH createViewHolder(ViewGroup parent, int viewType) {
        return onCreateViewHolder(parent, viewType);
    }

    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    public final void bindViewHolder(VH holder, int position) {
        onBindViewHolder(holder, position);
    }

    public abstract void onBindViewHolder(VH holder, int position);

    public final void notifyDataSetChanged() {
        Log.i(TAG, "notifyDataSetChanged: ");
        setChanged();
        notifyObservers();
    }

}
