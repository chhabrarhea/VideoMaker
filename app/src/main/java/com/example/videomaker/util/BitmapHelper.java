package com.example.videomaker.util;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class BitmapHelper {
    private static BitmapHelper bitmapHelper=null;
    private ArrayList<Bitmap> bitmapArrayList;
    private ArrayList<String> audioURI;
    private BitmapHelper()
    {
        bitmapArrayList=new ArrayList<>();
        audioURI=new ArrayList<>();
    }
    public static synchronized BitmapHelper getInstance()
    {
        if (bitmapHelper==null)
        {
            bitmapHelper=new BitmapHelper();
        }
        return bitmapHelper;
    }

    public ArrayList<Bitmap> getBitmapArrayList() {
        return bitmapArrayList;
    }

    public synchronized void  setBitmapArrayList(ArrayList<Bitmap> bitmapArrayList) {
        this.bitmapArrayList = bitmapArrayList;
    }
    public void addAudio(String uri,int pos)
    {
        this.audioURI.add(pos,uri);
    }

    public ArrayList<String> getAudioURI() {
        return audioURI;
    }

    public void setAudioURI(ArrayList<String> audioURI) {
        this.audioURI = audioURI;
    }
    public Bitmap getBitmap(int pos)
    {
        if (pos<this.bitmapArrayList.size())
        return this.bitmapArrayList.get(pos);
        else
            return null;
    }
    public String getAudio(int pos)
    {
        if(pos<this.audioURI.size())
        return this.audioURI.get(pos);
        else
            return null;
    }
}

