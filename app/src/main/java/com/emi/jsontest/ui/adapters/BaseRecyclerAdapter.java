package com.emi.jsontest.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context mContext;
    public ArrayList<T> mList;
    public LayoutInflater mInflater;

    public BaseRecyclerAdapter(Context aContext, ArrayList<T> aList){
        mContext=aContext;
        mList=aList;
        mInflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected abstract RecyclerView.ViewHolder createViewHolder(ViewGroup parent);
    protected abstract void loadUIfromModel(RecyclerView.ViewHolder aHolder, T aItem);

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        T litem=mList.get(position);
        assert litem != null;
        loadUIfromModel(holder,litem);
    }

}
