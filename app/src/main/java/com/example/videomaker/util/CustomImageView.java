package com.example.videomaker.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CustomImageView extends AppCompatImageView {
    int touchY;

    ArrayList<Integer> allY=new ArrayList<>();
    public CustomImageView(Context context) {
        super(context);
    }

    public ArrayList<Integer> getAllY() {
        return allY;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

       touchY = (int) event.getY();
       if (event.getAction()==MotionEvent.ACTION_DOWN){
       allY.add(touchY);}

/*
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(touchX,touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(touchX,touchY);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
*/
        invalidate();
        return  true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        // Line color
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        // Line width in pixels
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        for (int y:allY)
        canvas.drawLine(0,y,canvas.getWidth(),y,paint);
    }

    public CustomImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageWithGlide(Context context, String imagePath) {
        Glide.with(context).load(imagePath).into(this);
    }

}
