package com.emi.jsontest.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class CacheManager {

    private static boolean mCacheDirty=false;
    private static final long CACHE_EXPIRATION_IN_DAYS=1;

    static class TJsonCache{
        public String mData;
        public Date Stamp;
    }
    private static Map<String, TJsonCache> mCache=new LinkedHashMap<String, TJsonCache>();


    public static String getFromCache(String url){
        TJsonCache litem=mCache.get(url);
        Date lNow=Calendar.getInstance().getTime();
        boolean lDoDownload=( litem==null || ( (lNow.getTime()-litem.Stamp.getTime()) / 1000/60/60/24 )>CACHE_EXPIRATION_IN_DAYS );
        return lDoDownload?null:litem.mData;
    }

    public static void addToCache(String url, String aJson) throws Exception {
        TJsonCache lCache;
        if (mCache.size()>50){
            Map.Entry<String,TJsonCache> entry = mCache.entrySet().iterator().next();
            mCache.remove(entry.getKey());
        }
        lCache = mCache.get(url);
        if (lCache==null){
            lCache = new TJsonCache();
            mCache.put(url,lCache); //save on cache for later
        }
        lCache.Stamp= Calendar.getInstance().getTime();
        lCache.mData=aJson;
        mCacheDirty=true;
    }

    public static void saveCacheToStorage(Context aContext) throws Exception {
        if (mCacheDirty) {
            mCacheDirty=false;
            SharedPreferences lpref = aContext.getSharedPreferences("jsontest_storage", Context.MODE_PRIVATE);
            SharedPreferences.Editor ledit = lpref.edit();

            int i = 0;
            for (String lkey : mCache.keySet()) {
                TJsonCache lcache = mCache.get(lkey);
                if (lcache != null) {
                    i++;
                    ledit.putString("cache-1-" + Integer.toString(i), lkey);
                    ledit.putLong("cache-2-" + Integer.toString(i), lcache.Stamp.getTime());
                    ledit.putString("cache-3a-" + Integer.toString(i), Base64.encodeToString(lcache.mData.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
                }
            }
            ledit.apply();
        }
    }

    public static void loadCacheFromStorage(Context aContext) throws Exception {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mCacheDirty=false;
            SharedPreferences lpref = aContext.getSharedPreferences("jsontest_storage", Context.MODE_PRIVATE);
            mCache.clear();

            boolean lEOF = false;
            int i = 0;
            String lname = "";
            String lkey = "";
            long lStamp = 0;
            String lJson = "";
            while (lkey != null) {
                i++;
                lname = "cache-1-" + Integer.toString(i);
                lkey = lpref.getString(lname, null);
                if (lkey != null) {
                    TJsonCache lcache = new TJsonCache();

                    lname = "cache-2-" + Integer.toString(i);
                    lStamp = lpref.getLong(lname, 0);
                    lcache.Stamp = new Date();
                    lcache.Stamp.setTime(lStamp);


                    //lname="cache-3c-"+Integer.toString(i);
                    lname = "cache-3a-" + Integer.toString(i);
                    String stringData = lpref.getString(lname, null);
                    if (stringData != null) {
                        lcache.mData =  new String(  Base64.decode(stringData, Base64.DEFAULT), StandardCharsets.UTF_8);
                    } else {
                        lcache.mData = null;
                    }
                    mCache.put(lkey, lcache);
                }
            }
        }else{
            mCacheDirty=false;
        }
    }


}
