package com.example.videomaker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.content.Intent;
import com.example.videomaker.R;
import com.example.videomaker.SegmentDetailActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SegmentAdapter extends RecyclerView.Adapter<SegmentAdapter.VH>  {

    private Context context;
    private ArrayList<Bitmap> bits;

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.video_segment,null,false);
        return new VH(view);
    }

    public SegmentAdapter(Context context, ArrayList<Bitmap> bits) {
        this.context = context;
        this.bits = bits;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
      holder.image.setImageBitmap(bits.get(position));
    }

    @Override
    public int getItemCount() {
        return bits.size();
    }

    class VH extends RecyclerView.ViewHolder
    {
        ImageView image;
        public VH(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            itemView.setOnClickListener(view -> {
                if(getAdapterPosition()!=RecyclerView.NO_POSITION){
              Intent intent=new Intent(context, SegmentDetailActivity.class);
              intent.putExtra("position",getAdapterPosition());
              context.startActivity(intent);}
            });
        }
    }
}
