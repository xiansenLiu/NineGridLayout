package com.xiansenliu.ninegridlayout;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by xinliu
 * Date       4/2/17
 * Time       21:13.
 */

public abstract class ViewDelegate<O extends Object> {
    private static final String TAG = "ViewDelegate";
    protected Class mPojoType = null;
    protected Class mViewHolderClass = null;
    private Constructor mViewHolderConstructor = null;
    protected Context mContext;
    protected LayoutInflater mInflater;

    public ViewDelegate() {
        initPojoAndHolderType();
        if (mPojoType == null) {
//            Log.i(TAG, "pojo type not initialized");
            mPojoType = Object.class;
        }
        if (mViewHolderClass == null) {
//            Log.i(TAG, "viewholder type not initialized");
            mViewHolderClass = BaseVH.class;
        }
        try {
            mViewHolderConstructor = mViewHolderClass.getConstructor(View.class);
            mViewHolderConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Init pojo and viewholder type with specific Class type. eg. mPojoType = Animal.class;
     */
    protected abstract void initPojoAndHolderType();

    final boolean isViewType(Object o) {
        if (o.getClass() == mPojoType) {
            return true;
        }
        return false;
    }

    BaseVH onCreateViewHolder(ViewGroup parent) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        if (mInflater == null) {
            mInflater = LayoutInflater.from(mContext);
        }
        View itemView = mInflater.inflate(getItemLayout(), applyParentLayoutParamsOrNot() ? parent : null, false);
        try {
            return (BaseVH) mViewHolderConstructor.newInstance(itemView);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return the item layout res id.
     */
    protected abstract int getItemLayout();

    public abstract void onBindViewHolder(BaseVH holder, int position, O o, List items);

    public Class getPojoType() {
        return mPojoType;
    }

    protected boolean applyParentLayoutParamsOrNot() {
        return false;
    }
}
