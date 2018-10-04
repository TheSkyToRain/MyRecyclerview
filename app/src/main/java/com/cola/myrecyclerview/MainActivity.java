package com.cola.myrecyclerview;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BaseAdapter mBaseAdapter;
    private List<String> mStrings = new ArrayList<>();
    private int lastVisibleItemPosition = 0;
    //可见的最后一个item
    private LinearLayoutManager mLinearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        addData(mStrings);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mBaseAdapter = new BaseAdapter(this,mStrings);
        mBaseAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getApplicationContext(),"点击了第"+(position+1)+"条",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("确认删除吗?")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mBaseAdapter.removeData(position);
                            }
                        }).show();
            }
        });
        mBaseAdapter.setOpenItemAnimationEnable(true);
        recyclerView.setAdapter(mBaseAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1
                        == mBaseAdapter.getItemCount()){
                    mBaseAdapter.changeLoadMoreStatus(BaseAdapter.LOADING_MORE);
                    loadMoreData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> loadMoreDatas = new ArrayList<>();
                for (int i = 1; i <= 100; i++){
                    loadMoreDatas.add(i+"");
                }
                mBaseAdapter.addMoreItems(loadMoreDatas);
                mBaseAdapter.changeLoadMoreStatus(BaseAdapter.SHOW_FOOTER);
            }
        },2500);
    }

    private void addData(List<String> mStrings) {
        for (int i = 1; i <= 100; i++){
            mStrings.add(i+"");
        }
    }
}
