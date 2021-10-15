package com.emi.jsontest.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.emi.jsontest.R;
import com.emi.jsontest.data.CacheManager;
import com.emi.jsontest.data.JsonDownloadUtil;
import com.emi.jsontest.models.FilmModel;
import com.emi.jsontest.models.MainListModel;
import com.emi.jsontest.ui.adapters.BaseRecyclerAdapter;
import com.emi.jsontest.ui.adapters.FilmRecyclerAdapter;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.Response;

public class FilmsListFragment extends BaseListFragment<FilmModel> {

    private static final String FILMS_URL = "https://swapi.dev/api/films";


    @Override
    protected boolean isItemEqual(FilmModel aItem1, FilmModel aItem2) {
        return aItem1.episode_id==aItem2.episode_id;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_list_films;
    }



    @Override
    protected boolean startDownload() {
        try {
            String lurl=FILMS_URL;
            MainListModel<FilmModel> lresult =null;
            Type lTypeToken = new TypeToken<MainListModel<FilmModel>>() {}.getType();
            String lCached=CacheManager.getFromCache(lurl);
            if (lCached==null) {
                Response lresponse = JsonDownloadUtil.requestJson(true, lurl);
                Reader lInputStream = (lresponse != null && lresponse.body() != null) ? lresponse.body().charStream() : null;
                lresult=JsonDownloadUtil.getResultModel(lInputStream, lTypeToken);
                CacheManager.addToCache(lurl,JsonDownloadUtil.mGson.toJson(lresult));
            }else{
                lresult=JsonDownloadUtil.getResultModel(lCached, lTypeToken);
            }

            if (lresult != null) {
                ArrayList<FilmModel> llist = lresult.getList();
                if (llist == null) return false;

                for (FilmModel litem : llist)
                    this.addOrUpdateItem(litem);

                //start adding/updating items
                return true;
            }
            return false;
        }catch (Exception E){
            E.printStackTrace();
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FilmsListFragment.this.getContext(), getString(R.string.connectivity_error_msg), Toast.LENGTH_LONG).show();
                }
            });
            return false;
        }
    }

    @Override
    protected BaseRecyclerAdapter<FilmModel> createAdapter(ArrayList<FilmModel> aList) {
        return new FilmRecyclerAdapter(this.getContext(),aList);
    }

    @Override
    protected String getTitle() {
        return "films navigator";
    }


    @Override
    public void onResume() {
        super.onResume();
        forceDownload();
    }

    @Override
    public void onStop() {
        try {
            CacheManager.saveCacheToStorage(this.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            CacheManager.loadCacheFromStorage(this.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
