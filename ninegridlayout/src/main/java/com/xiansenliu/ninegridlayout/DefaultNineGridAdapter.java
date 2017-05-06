package com.xiansenliu.ninegridlayout;

import android.view.ViewGroup;

import java.util.List;

/**
 * Created by xinliu
 * Date       4/2/17
 * Time       17:57.
 */

public class DefaultNineGridAdapter<O extends Object> extends NineGridAdapter<BaseVH> {
    protected final int MAX_SHOW_ITEM_COUNT = 9;
    private NineGridManager mNineGridManager;
    private List<O> mItems;

    @Override
    public int getItemCount() {
        if (mItems.size() >= MAX_SHOW_ITEM_COUNT) {
            return MAX_SHOW_ITEM_COUNT;
        } else {
            return mItems.size();
        }
    }

    public DefaultNineGridAdapter(List<O> items) {
        this.mItems = items;
        this.mNineGridManager = new NineGridManager();
    }

    public void addViewDelegates(ViewDelegate... viewDelegates) {
        mNineGridManager.addViewDelegates(viewDelegates);
    }

    public void removeDelegates(ViewDelegate... viewDelegates) {
        mNineGridManager.removeViewDelegates(viewDelegates);
    }

    public int getItemViewType(int position) {
        return mNineGridManager.getItemViewType(mItems, position);
    }

    public BaseVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return mNineGridManager.onCreateViewHolder(parent, viewType, mItems);
    }

    public void onBindViewHolder(BaseVH holder, int position) {
        mNineGridManager.onBindViewHolder(holder, position, mItems);
    }



}
