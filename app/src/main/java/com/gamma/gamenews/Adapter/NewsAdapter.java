package com.gamma.gamenews.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gamma.gamenews.Beans.News;
import com.gamma.gamenews.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by emers on 5/6/2018.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private Context context;
    private ArrayList<News> newsArray;

    public NewsAdapter(Context context, ArrayList<News> newsArray) {
        this.context = context;
        this.newsArray = newsArray;
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        LinearLayout card;
        TextView txtTitle, txtSubtitle;
        ImageView imgPicture, btnFav;


        public NewsViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.maincard);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtSubtitle = itemView.findViewById(R.id.txt_subtitle);
            imgPicture = itemView.findViewById(R.id.img_cover);
            btnFav = itemView.findViewById(R.id.btn_favorite);
        }
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_news,parent,false);
        return (new NewsViewHolder(v));
    }


    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        final News _new = newsArray.get(position);
        if(_new.getTitle()!=null)
            holder.txtTitle.setText(_new.getTitle().trim());
        else
            holder.txtTitle.setText(context.getResources().getString(R.string.no_title_available));


        if(_new.getDescription()!=null)
        holder.txtSubtitle.setText(_new.getDescription().trim());
        else
            holder.txtSubtitle.setText(context.getResources().getString(R.string.no_description_available));

        if(_new.getCoverImage() != null){
            Picasso.get().load(_new.getCoverImage())
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.no_image)
                    .into(holder.imgPicture);
        } else
            holder.imgPicture.setImageResource(R.drawable.no_image);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return newsArray != null ? newsArray.size(): 0;
    }
}
