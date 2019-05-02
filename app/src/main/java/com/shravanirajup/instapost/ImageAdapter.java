package com.shravanirajup.instapost;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Upload> mUploads;

    public ImageAdapter(Context context,List<Upload> uploads) {
        mContext=context;
        mUploads=uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item,viewGroup,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder imageViewHolder, int i) {

        Upload uploadCurrent = mUploads.get(i);
        imageViewHolder.textViewName.setText(uploadCurrent.mNickName);
        Picasso.get().load(uploadCurrent.mImageUrl).fit().centerCrop().into(imageViewHolder.imageView);
        String comments = uploadCurrent.mHashtag +" "+ uploadCurrent.mComments;
        imageViewHolder.textHashTags.setText(comments);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder
    {
        private TextView textViewName;
        private ImageView imageView;
        private TextView textHashTags;

        public ImageViewHolder(View itemView)
        {
             super(itemView);
             textViewName=itemView.findViewById(R.id.username_text);
             imageView=itemView.findViewById(R.id.post_image);
             textHashTags=itemView.findViewById(R.id.caption_text);
        }
    }
}