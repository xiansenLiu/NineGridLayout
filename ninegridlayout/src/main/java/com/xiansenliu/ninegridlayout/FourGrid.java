package com.xiansenliu.ninegridlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by xinliu
 * Date       4/2/17
 * Time       17:22.
 */

public class FourGrid extends ViewGroup implements Observer {
    private static final String TAG = "NineGrid";
    private NineGridAdapter mAdapter;
    private ArrayList<BaseVH> mHolderPool = new ArrayList<>();


    private static final int STATE_MEASURING = 0;
    private static final int STATE_LAYOUTTING = 1;
    private static final int STATE_RESUME = 2;
    private static int[] STATE = {STATE_MEASURING, STATE_LAYOUTTING, STATE_RESUME};


    private int mSpace = 0;
    private int mChildWidth = 0;
    private int mChildHeight = 0;
    private int mChildCount;
    private int mMaxChildCount = 4;
    private int mLayoutStyle = 0;

    public static final int LAYOUT_STYLE_GRID = 0;//layout child like wechat
    public static final int LAYOUT_STYLE_FILL = -1;// layout child orderly

    public FourGrid(@NonNull Context context) {
        this(context, null);
    }

    public FourGrid(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FourGrid(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FourGrid, defStyleAttr, 0);
        mSpace = typedArray.getDimensionPixelSize(R.styleable.FourGrid_four_grid_space, 0);
        typedArray.recycle();
    }

    public FourGrid(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.i(TAG, "onMeasure: ");
        if (mAdapter == null) {
            setMeasuredDimension(0, 0);
//            Log.e(TAG, "onNineGridMeasure: no adapter is attached , skipping layout");
            return;
        }
        onNineGridMeasure(widthMeasureSpec, heightMeasureSpec);
        addChildren();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        Log.i(TAG, "onLayout: ");
        onNineGridLayout(changed, left, top, right, bottom);
    }

    private void onNineGridMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingStart() - getPaddingEnd();
        int height = 0;
//        mChildCount = mAdapter.getItemCount();
//        Log.i(TAG, "onNineGridMeasure: " + mChildCount);
        if (mChildCount <= 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        if (mChildCount == 1) {
            mChildWidth = width;
//            height = (int) (mChildWidth * 0.5625f);
        } else {
            mChildWidth = (width - mSpace) / 2;
            if (mChildCount >= 2 && mChildCount <= 4) {
                width = mChildWidth * 2 + mSpace;
                int ceil = (int) Math.ceil(mChildCount / 2.0);
                height = mChildWidth * ceil + mSpace * (ceil - 1);
            }
//            else {
//                width = mChildWidth * 3 + mSpace * 2;
//                if (mChildCount <= mMaxChildCount) {
//                    int ceil = (int) Math.ceil(mChildCount / 3.0);
//                    height = mChildWidth * ceil + mSpace * (ceil - 1);
//                } else if (mChildCount > mMaxChildCount) {
//                    height = width;
//                }
//            }
        }

        int finalWidth = getAppropriateSize(widthMeasureSpec, width + getPaddingLeft() + getPaddingRight());
        int finalHeight = getAppropriateSize(heightMeasureSpec, height + getPaddingTop() + getPaddingBottom());
//        Log.i(TAG, "onNineGridMeasure: finalWidth = " + finalWidth + " finalHeight = " + finalHeight);
        setMeasuredDimension(finalWidth, finalHeight);
    }

    private int getAppropriateSize(int spec, int size) {
        int mode = MeasureSpec.getMode(spec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
//                Log.i(TAG, "getAppropriateSize: UNSPECIFIED");
                result = size;
                break;
            case MeasureSpec.EXACTLY:
//                Log.i(TAG, "getAppropriateSize: EXACTLY");
                result = MeasureSpec.getSize(spec);
                break;
            case MeasureSpec.AT_MOST:
//                Log.i(TAG, "getAppropriateSize: AT_MOST");
                result = Math.min(size, MeasureSpec.getSize(spec));
                break;
        }
        return result;
    }

    private void addChildren() {
        removeAllViews();
        int min = Math.min(mChildCount, mMaxChildCount);

        for (int i = 0; i < min; i++) {
//            Log.i(TAG, "add Children: " + i + " " + mChildWidth);
            addView(getVH(i).mItemView, generateDefaultLayoutParams());
            View childAt = getChildAt(i);
            childAt.measure(MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY));
        }

    }


    private void onNineGridLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mChildCount == 0) {
//            Log.e(TAG, "onNineGridLayout: no child");
            return;
        }
        int column = 3;
        if (mChildCount <= 4 && mChildCount >= 2) {
            column = 2;
        }
        for (int i = 0; i < mChildCount; i++) {
            View child = getChildAt(i);
            if (child == null) {
//                Log.e(TAG, "onNineGridLayout: child at " + i + " is null");
                return;
            }
            if (mAdapter != null && mHolderPool.get(i) != null && !mHolderPool.get(i).isFlag()) {
                mAdapter.bindViewHolder(mHolderPool.get(i), i);
                mHolderPool.get(i).setFlag(true);
            } else {
                return;
            }
            int rowIndex = i / column;
            int colIndex = i % column;
            int childLeft = getPaddingLeft() + (mChildWidth + mSpace) * (colIndex);
            int childTop = getPaddingTop() + (mChildWidth + mSpace) * (rowIndex);
            int childRight = childLeft + mChildWidth;
            int childBottom = childTop + mChildWidth;
//            Log.i(TAG, "onNineGridLayout: ");
//            Log.i(TAG, "onNineGridLayout: child " + i + " layout " + "childLeft = " + childLeft + " childTop = " + childTop + " childRight = " + childRight + " childBottom = " + childBottom);
            child.layout(childLeft, childTop, childRight, childBottom);
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof NineGridAdapter) {
//            Log.i(TAG, "update: correct adapter");
            this.mAdapter = ((NineGridAdapter) o);
            this.mAdapter.addObserver(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mHolderPool.forEach(holder -> holder.setFlag(false));
            } else {
                for (BaseVH baseVH : mHolderPool) {
                    baseVH.setFlag(false);
                }
            }
            mChildCount = mAdapter.getItemCount();
            requestLayout();
            invalidate();
        }
    }


    private BaseVH getVH(int position) {
        if (mHolderPool.size() > 0 && position < mHolderPool.size()) {
            return mHolderPool.get(position);
        } else {
            int itemViewType = mAdapter.getItemViewType(position);
            BaseVH vh = mAdapter.createViewHolder(this, itemViewType);
            mHolderPool.add(vh);
            return vh;
        }
    }

    public void setAdapter(NineGridAdapter adapter) {
        this.mAdapter = adapter;
        this.mAdapter.addObserver(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mHolderPool.stream().forEach(holder -> holder.setFlag(false));
//          Error:Uncaught translation error: com.android.dx.cf.code.SimException: default or static interface method used without --min-sdk-version >= 24
//          the code below will caused the exception above
//          IntStream.range(0, 10);
        } else {
            for (BaseVH baseVH : mHolderPool) {
                baseVH.setFlag(false);
            }
        }
        mChildCount = mAdapter.getItemCount();
        this.requestLayout();
        this.invalidate();
    }

    public NineGridAdapter getAdapter() {
        return mAdapter;
    }

    public void setSpace(int space) {
        mSpace = space;
        if (mChildCount >= 2) {
            invalidate();
        }
    }

    /**
     * Clear holder pool.
     */
    public void clearHolderPool() {
        mHolderPool.clear();
    }
}
