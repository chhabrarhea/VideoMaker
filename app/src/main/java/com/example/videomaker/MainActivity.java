package com.example.videomaker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
private final int FROM_GALLERY=100;
private final int GALLERY=1;
private final int FROM_CAMERA=200;
private final int CAMERA=2;

private LinearLayout root1;
private RelativeLayout root2;
private ImageView image;
private String mCurrentPhotoPath;
private final String TAG="myMainActivity";
private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root1=findViewById(R.id.linear1);
        root2=findViewById(R.id.relative1);
        image=findViewById(R.id.display);
    }
   public void openCamera() throws IOException
   {
       Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // Ensure that there's a camera activity to handle the intent
       if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
           // Create the File where the photo should go
           File photoFile = null;
           try {
               photoFile = createImageFile();
           } catch (IOException ex) {
               // Error occurred while creating the File
               return;
           }
           // Continue only if the File was successfully created
           if (photoFile != null) {
               Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                       BuildConfig.APPLICATION_ID + ".provider",
                       createImageFile());
               takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
               startActivityForResult(takePictureIntent, FROM_CAMERA);
           }
       }
   }
   public void openGallery()
   {
       Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       startActivityForResult(intent,FROM_GALLERY);
   }
   public void startCamera(View view)
   {
       if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
       {
           ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA);
       }
       else if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
           ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},CAMERA);
       else
           try {
               openCamera();
           }catch (Exception e)
           {
               Log.i(TAG,e.getMessage());
           }

   }
   public void startGallery(View view)
   {
       if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||  ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
       {
           ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY);
       }
       else
       openGallery();
   }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("CAMERA",requestCode+" "+resultCode);
        if (data==null)
        {
            Log.i("camera","null");
        }
        if (resultCode == RESULT_OK) {
            try {
                Log.i(TAG,"final try block");
                if (requestCode == FROM_CAMERA) {
                        uri = Uri.parse(mCurrentPhotoPath);
                        Log.i(TAG,uri.toString());
                        Glide.with(this).load(uri.toString()).into(image);
                        root2.setVisibility(View.VISIBLE);
                        root1.setVisibility(View.GONE);
                    }
                if (requestCode == FROM_GALLERY && data!=null) {

                    uri = data.getData();
                    Glide.with(MainActivity.this).load(uri.toString()).into(image);
                    root2.setVisibility(View.VISIBLE);
                    root1.setVisibility(View.GONE);

                }
            }catch (Exception e)
            {
                Log.i("MyActivity",e.getMessage());
            }
        }

        }
        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == CAMERA) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        openCamera();
                    }catch (Exception e)
                    {
                        Log.i(TAG,e.getMessage());
                    }
                } else {
                    Snackbar.make(root1, "Permission required to continue.", Snackbar.LENGTH_LONG)
                            .setAction("Settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.parse("package:" + getPackageName()));
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }).show();
                }
            } else if (requestCode == GALLERY) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Snackbar.make(root1, "Permission required to continue.", Snackbar.LENGTH_LONG)
                            .setAction("Settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.parse("package:" + getPackageName()));
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }).show();
                }

            }
        }

        public void splitImage(View view)
        {
            Intent intent=new Intent(MainActivity.this,SplitImageActivity.class);
            intent.putExtra("uri",uri.toString());
            startActivity(intent);
        }

        public void goBack(View v)
        {
            root2.setVisibility(View.GONE);
            root1.setVisibility(View.VISIBLE);
            uri=null;
        }
    }