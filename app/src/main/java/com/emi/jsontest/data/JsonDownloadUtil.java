package com.emi.jsontest.data;

import android.annotation.SuppressLint;

import com.emi.jsontest.models.MainListModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonDownloadUtil {

    private static final String SWAPI_HOST = "swapi.dev";

    private static class SwapiDevHostVerifier implements HostnameVerifier {
        @SuppressLint("BadHostnameVerifier")
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return SWAPI_HOST.contains(hostname);
        }
    }

    private static OkHttpClient createHttp(){
        HostnameVerifier lverifier = new SwapiDevHostVerifier();
        OkHttpClient.Builder lbuilder=new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
        //      .protocols(Arrays.asList(Protocol.HTTP_1_1));

        lbuilder.hostnameVerifier(lverifier);
        return lbuilder.build();
    }

    private static OkHttpClient mHttpPoolClient=null;

    public static Response requestJson(boolean aUseSingletonClient, String url) {
        try {
            OkHttpClient lOkHttpClient = null;
            if (!aUseSingletonClient || mHttpPoolClient == null) {
                lOkHttpClient = createHttp();
                if (aUseSingletonClient) mHttpPoolClient = lOkHttpClient;
            } else
                lOkHttpClient = mHttpPoolClient;
            Request request;

            request = new Request.Builder().url(url).build();
            return lOkHttpClient.newCall(request).execute();
        } catch (Exception E) {
            E.printStackTrace();
        }
        return null;
    }

    public static Gson mGson = new GsonBuilder().create();

  //  public static void registerTypeAdapter(Type aType, Object aTypeAdapter){
   //     mGson=mGson.newBuilder().registerTypeAdapter(aType,aTypeAdapter).create();
  //  }

 //   static {
  //      registerTypeAdapter(Date.class, new CompatibleDateAdapter());
 //   }
     public static <T> MainListModel<T> getResultModel(String in, Type aType){
         if (in == null) {
             return null;
         }
         try {
             MainListModel<T> resultModel = mGson.fromJson(in,aType);
             return resultModel;
         }
         catch (Exception e) {
             e.printStackTrace();
         }
         return null;
     }

    public static <T> MainListModel<T> getResultModel(Reader in, Type aType){
        if (in == null) {
            return null;
        }
        try {
            MainListModel<T> resultModel = mGson.fromJson(in,aType);
            return resultModel;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



}
