package com.emi.jsontest.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emi.jsontest.R;
import com.emi.jsontest.models.FilmModel;

import java.util.ArrayList;

public class FilmRecyclerAdapter extends BaseRecyclerAdapter<FilmModel>{



    public class FilmHolder extends RecyclerView.ViewHolder{
        TextView mTitle;
        TextView mCrawl;
        TextView mDirector;
        TextView mDate;

        public FilmHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.textFilmName);
            mCrawl = itemView.findViewById(R.id.textCrawlText);
            mDirector = itemView.findViewById(R.id.textDirector);
            mDate = itemView.findViewById(R.id.releaseDate);

        }

        public void setUI(FilmModel aItem){
            //load layout with data
            mTitle.setText(aItem.title);
            mCrawl.setText(aItem.opening_crawl);
            mDirector.setText(aItem.director);
            this.itemView.getContext().getString(R.string.release_date_text);
            mDate.setText( String.format("release: %1$s", aItem.release_date.toString()));
            mCrawl.setSelected(true);
        }


    }

    public FilmRecyclerAdapter(Context aContext, ArrayList<FilmModel> aList) {
        super(aContext, aList);
    }



    @Override
    protected RecyclerView.ViewHolder createViewHolder(ViewGroup parent) {
        View lview=mInflater.inflate(R.layout.list_item_film, parent, false);
        return new FilmHolder(lview);
    }

    @Override
    protected void loadUIfromModel(RecyclerView.ViewHolder aHolder, FilmModel aItem) {
        ((FilmHolder)aHolder).setUI(aItem);
    }
}
