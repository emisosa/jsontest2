package com.emi.jsontest.ui.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emi.jsontest.R;
import com.emi.jsontest.data.CacheManager;
import com.emi.jsontest.ui.adapters.BaseRecyclerAdapter;

import java.util.ArrayList;

public abstract class BaseListFragment<T> extends Fragment {

    private ProgressBar mProgressBar;

    private RecyclerView mRecyclerView=null;
    BaseRecyclerAdapter<T> mAdapter=null;
    private TextView mTitle;

    private final ArrayList<T> mBuffer=new ArrayList<>();

    protected abstract boolean isItemEqual(T aItem1,T aItem2);

    protected void addOrUpdateItem(T aItem){
        //add or replace
        int lsize=0;
        synchronized (mBuffer) {
            lsize=mBuffer.size();
            int i = 0;
            for (i = 0; i < lsize; i++) {
                T litem = mBuffer.get(i);
                if (isItemEqual(litem, aItem)) {
                    mBuffer.set(i, aItem);
                    if (lsize!=0) {
                        int finalI = i;
                        this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyItemChanged(finalI);
                            }
                        });
                    }
                    return;
                }
            }
            mBuffer.add(aItem);
            if (lsize!=0) {
                this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyItemInserted(mBuffer.size() - 1);
                    }
                });
            }
        }

        if (lsize==0){
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   mAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    protected abstract int getLayout();
    protected abstract boolean startDownload();
    protected abstract BaseRecyclerAdapter<T> createAdapter(ArrayList<T> aList);
    protected abstract String getTitle();

    HandlerThread mDownloadHandler =new HandlerThread(getTitle());

    private boolean mDownloading=false;

    Runnable mDownloadRunnable =new Runnable() {
        @Override
        public void run() {
            if (!mDownloading) {
                try {
                    BaseListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.VISIBLE);
                        }
                    });
                    mDownloading=true;
                    try {
                        startDownload();
                    }catch (Exception E) {

                    }
                    mDownloading = false;
                    BaseListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Log.d("Dataset recycler","already downloading");
            }
        }
    };

    //this is for the error:Inconsistency detected. Invalid view holder adapter.    Google hasnt fixed it yet
    public class WrapContentLinearLayoutManager extends LinearLayoutManager {

        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }


        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("TAG", "meet a IOOBE in RecyclerView");
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView=view.findViewById(R.id.list_recycler);
        mProgressBar= view.findViewById(R.id.downloadListProgressBar);
        mProgressBar.setVisibility(View.GONE);
        mTitle=view.findViewById(R.id.listCaption);
        mTitle.setText(getTitle());

        mAdapter=createAdapter(mBuffer);

        LinearLayoutManager llm = new WrapContentLinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);


    }

    protected void forceDownload(){
        new Thread(mDownloadRunnable).start();
    }



}
