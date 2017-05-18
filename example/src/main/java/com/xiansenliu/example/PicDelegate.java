package com.xiansenliu.example;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiansenliu.ninegridlayout.BaseVH;
import com.xiansenliu.ninegridlayout.ViewDelegate;

import java.util.List;

/**
 * Author       xinliu
 * Date         5/17/17
 * Time         7:21 PM
 */

public class PicDelegate extends ViewDelegate<String> {
    @Override
    protected void initPojoAndHolderType() {
        mPojoType = String.class;
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_ng_pic;
    }

    @Override
    public void onBindViewHolder(BaseVH holder, int position, String o, List items) {
        ImageView img = holder.getView(R.id.img, ImageView.class);
        img.setAdjustViewBounds(items.size() == 1);
        holder.mItemView.setOnClickListener(v -> {
            Toast.makeText(mContext, position + "", Toast.LENGTH_SHORT).show();
        });

    }
}
