package com.example.videomaker.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.io.File.separator;

public class BitmapHelper {
    private static BitmapHelper bitmapHelper=null;
    private ArrayList<Bitmap> bitmapArrayList;
    private String[] audioURI;
    private String[] audioName;
    private ArrayList<String> imageName;
    private BitmapHelper()
    {
        bitmapArrayList=new ArrayList<>();

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

    public String[] getAudioName() {
        return audioName;
    }

    public ArrayList<String> getImageName() {
        return imageName;
    }

    public synchronized void  setBitmapArrayList(ArrayList<Bitmap> bitmapArrayList) {
        this.bitmapArrayList = bitmapArrayList;
        this.audioURI=new String[bitmapArrayList.size()];
        this.imageName=new ArrayList<>();
        this.audioName=new String[bitmapArrayList.size()];
    }



    public void addAudio(String uri, int pos,String name)
    {
        if (pos<this.audioURI.length)
        this.audioURI[pos]=uri;
        this.audioName[pos]=name;
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
        if(pos<this.audioURI.length)
        return this.audioURI[pos];
        else
            return null;
    }

    public boolean allAudioAdded()
    {
        for (int i=0;i<audioURI.length;i++)
        {
            if (audioURI[i]==null || audioURI[i].isEmpty())
            {
                return false;
            }

        }
        return true;
    }

   //Save Bitmap to Storage
    public void saveTempBitmap() {

        if (isExternalStorageWritable()) {
            for (Bitmap bitmap: bitmapArrayList)
            saveImage(bitmap);
        }
    }
    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root ,"VideoMaker");
        if (!myDir.exists())
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "VideoMaker_"+ timeStamp +".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            this.imageName.add(fname);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}

