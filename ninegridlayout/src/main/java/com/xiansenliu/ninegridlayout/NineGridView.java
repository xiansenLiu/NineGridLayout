package com.xiansenliu.ninegridlayout;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Author       xinliu
 * Date         5/17/17
 * Time         8:07 PM
 */

public class NineGridView extends ViewGroup implements Observer {
    private static final String TAG = "NineGridView";
    private NineGridAdapter mAdapter;
    private ArrayList<BaseVH> mHolderPool = new ArrayList<>();
    private int mChildCount;
    private int mMaxChildCount = 9;
    private int mChildWidth;
    private int mChildHeight;
    private int mRows;
    private int mColumns;
    private boolean isChildChanged = true;
    private int mSpace = 0;

    public NineGridView(@NonNull Context context) {
        super(context);
    }

    public NineGridView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NineGridView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NineGridView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        if (mAdapter == null) {
            setMeasuredDimension(0, 0);
            return;
        }
        if (mChildCount <= 0) {
            setMeasuredDimension(0, 0);
            return;
        }

        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingLeft() - getPaddingRight();

        generateGrids(mChildCount);
        generateGridSize(mChildCount, width, height);
        addNineGridChildren();
        onNineGridMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void addNineGridChildren() {
        if (isChildChanged) {
            removeAllViews();
            int min = Math.min(mChildCount, mMaxChildCount);
            for (int i = 0; i < min; i++) {
//            Log.i(TAG, "add Children: " + i + " " + mChildWidth);
                BaseVH vh = getVH(i);
                mAdapter.bindViewHolder(vh, i);
                addView(vh.mItemView, generateDefaultLayoutParams());
            }
            isChildChanged = false;
        }
    }


    private void onNineGridMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingStart() - getPaddingEnd();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        measureNineGridChildren(mChildCount, widthMeasureSpec, heightMeasureSpec);

        int totalHeight = getTotalHeight();
        int finalWidth = getAppropriateSize(widthMeasureSpec, width + getPaddingLeft() + getPaddingRight());
//        Log.i(TAG, "onNineGridMeasure: " + totalHeight);
        int finalHeight = getAppropriateSize(heightMeasureSpec, totalHeight + getPaddingTop() + getPaddingBottom());
//        Log.i(TAG, "onNineGridMeasure: " + finalHeight);
        setMeasuredDimension(finalWidth, finalHeight);
    }

    private int getTotalHeight() {
        int height;
        if (mChildCount == 1) {
            height = getChildAt(0).getMeasuredHeight();
            mChildHeight = height;
        } else {
            height = mRows * mChildHeight + (mRows - 1) * mSpace;
        }
        return height;
    }


    private void measureNineGridChildren(int childCount, int widthMeasureSpec, int heightMeasureSpec) {
        if (childCount == 1) {
            View childAt = getChildAt(0);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int spec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
            childAt.measure(widthMeasureSpec, spec);
            return;
        }
        int min = Math.min(mChildCount, mMaxChildCount);
        for (int i = 0; i < min; i++) {
            View childAt = getChildAt(i);
            childAt.measure(MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mChildHeight, MeasureSpec.EXACTLY));
        }
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
                Log.i(TAG, "getAppropriateSize: AT_MOST");
                result = Math.min(size, MeasureSpec.getSize(spec));
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        onNineGridLayout(changed, left, top, right, bottom);
    }

    private void onNineGridLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mAdapter == null || mChildCount == 0) {
//            Log.e(TAG, "onNineGridLayout: no child");
            return;
        }

        for (int i = 0; i < mChildCount; i++) {
            View child = getChildAt(i);
            if (child == null) {
                return;
            }
            if (mHolderPool.get(i) != null && !mHolderPool.get(i).isFlag()) {
//                mAdapter.bindViewHolder(mHolderPool.get(i), i);
                mHolderPool.get(i).setFlag(true);
            } else {
                return;
            }
            int[] position = generateChildPosition(i);
//            Log.e(TAG, "onNineGridLayout: " + i + "--" + position[0] + "-" + position[1] + "-" + position[2] + "-" + position[3]);
            child.layout(position[0], position[1], position[2], position[3]);
        }

    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof NineGridAdapter) {
            isChildChanged = true;
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
        this.isChildChanged = true;
        this.mAdapter = adapter;
        this.mAdapter.addObserver(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mHolderPool.forEach(holder -> holder.setFlag(false));
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


    //    calculate the row and column numbers
    private void generateGrids(int childCount) {
        if (childCount == 4) {
            mRows = 2;
            mColumns = 2;
        } else if (mChildCount == 2) {
            mRows = 1;
            mColumns = 2;
        } else if (childCount == 1) {
            mRows = 1;
            mColumns = 1;
        } else if (childCount == 3) {
            mRows = 1;
            mColumns = 3;
        }
    }

    private void generateGridSize(int childCount, int parentWidth, int parentHeight) {
        if (mChildCount == 1) {
            mChildWidth = parentWidth;
        } else {
            mChildWidth = (parentWidth - mSpace * 2) / 3;
            mChildHeight = mChildWidth;
        }
    }


    private int[] generateChildPosition(int childIndex) {
        int[] position = new int[4];
        int columnIndex = childIndex % mColumns;
        int rowIndex = (childIndex - columnIndex) / mRows;
        int left = columnIndex * (mSpace + mChildWidth);
        int top = rowIndex * (mSpace + mChildHeight);
        int right = left + mChildWidth;
        int bottom = top + mChildHeight;
        position[0] = left;
        position[1] = top;
        position[2] = right;
        position[3] = bottom;
        Log.e(TAG, "generateChildPosition: (" + columnIndex + "," + rowIndex + ")");
        return position;

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
