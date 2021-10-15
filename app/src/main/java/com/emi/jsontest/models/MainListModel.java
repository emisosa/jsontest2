package com.emi.jsontest.models;

import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;

public class MainListModel <T>{

    @SerializedName("count")
    private int count;

    @SerializedName("next")
    private int next;

    @SerializedName("previous")
    private int previous;

    @SerializedName("results")
    private ArrayList<T> list;

    public MainListModel() {
    }

    public int getCount() {
        return count;
    }


    public ArrayList<T> getList() {
        return list;
    }

    public void setList(ArrayList<T> list) {
        this.list = list;
    }
}
