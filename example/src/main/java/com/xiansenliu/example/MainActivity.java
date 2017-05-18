package com.xiansenliu.example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.xiansenliu.ninegridlayout.DefaultNineGridAdapter;
import com.xiansenliu.ninegridlayout.NineGrid;
import com.xiansenliu.ninegridlayout.NineGridView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private NineGridView mNineGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNineGrid = (NineGridView) findViewById(R.id.nine_grid);

        ArrayList<String> list = new ArrayList<>();


        int size = 2;
        for (int i = 0; i < size; i++) {
            list.add("");
        }

        DefaultNineGridAdapter<String> adapter = new DefaultNineGridAdapter<>(list);
        adapter.addViewDelegates(new PicDelegate());
        mNineGrid.setAdapter(adapter);
        mNineGrid.setSpace(10);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_image) {
            startActivity(new Intent(this, ImageActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
