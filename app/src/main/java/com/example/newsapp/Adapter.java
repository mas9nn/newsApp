package com.example.newsapp;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.models.Articles;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder>{
    private ItemClickListener mClickListener;
    private List<Articles> news;
    private Context context;

    public Adapter(List<Articles> news, Context context) {
        this.news = news;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.title.setText(news.get(position).getTitle());
        holder.body.setText(news.get(position).getSource().getName());
        holder.date.setText(dateTime(news.get(position).getPublishedAt()));
        holder.title.setText(news.get(position).getTitle());

        Picasso.get().load(news.get(position).getUrlToImage()).fetch(new Callback() {
            @Override
            public void onSuccess() {
                Picasso.get().load(news.get(position).getUrlToImage()).into(holder.image);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    public void addItems(List<Articles> postItems) {
        news.addAll(postItems);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return news.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title,body,date;
        ImageView image;
        CardView cardView;

        MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            date = itemView.findViewById(R.id.date);
            image = itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.card_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public String dateTime(String date){
        Date date1;
        String t = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
             date1 = simpleDateFormat.parse(date);
             t = formatter.format(date1).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
