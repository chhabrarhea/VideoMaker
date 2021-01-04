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
    private int[] audioDuration;
    private ArrayList<String> imageName;
    String [] tempFiles;
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
        this.audioURI=new String[this.bitmapArrayList.size()];
        this.imageName=new ArrayList<>();
        this.audioName=new String[this.bitmapArrayList.size()];
        this.audioDuration=new int[this.bitmapArrayList.size()];
        this.tempFiles=new String[3];
    }

    public void setTempFiles(String[] tempFiles) {
        this.tempFiles = tempFiles;
    }

    public void addAudio(String uri, int pos, String name, int duration)
    {
        if (pos<this.audioURI.length)
        this.audioURI[pos]=uri;
        this.audioName[pos]=name;
        this.audioDuration[pos]=duration;
    }

    public int[] getAudioDuration() {
        return audioDuration;
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
            for (int i=0;i<bitmapArrayList.size();i++)
            saveImage(bitmapArrayList.get(i),i);
        }
    }
    private void saveImage(Bitmap finalBitmap,int i) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root ,"VideoMaker11");
        if (!myDir.exists())
        myDir.mkdirs();


        String fname = "VideoMaker_"+i +".jpg";

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

    public void deleteTempFiles()
    {
        for (String path:this.tempFiles)
        {
            if (new File(path).exists())
                new File(path).deleteOnExit();
        }
        for (String path:this.audioURI)
        {
            if (new File(path).exists())
                new File(path).deleteOnExit();
        }

        for (String name:this.imageName)
        {
            String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/VideoMaker11/"+name;
            if (new File(path).exists())
                new File(path).deleteOnExit();
        }
    }
}

