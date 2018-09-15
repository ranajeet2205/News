package com.example.android.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private Context mContext;

    private List<News> newsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,sectionName,authorName,date;
        //reference to the view for each data item

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //title of the news
            title = (TextView)itemView.findViewById(R.id.news_title);
            //section name of news
            sectionName =(TextView)itemView.findViewById(R.id.section_name);
            //author name of news
            authorName = (TextView)itemView.findViewById(R.id.author_name);
            //date and time of news publish
            date = (TextView)itemView.findViewById(R.id.date_time);
        }
    }

    //Constructor of newsadapter
    public NewsAdapter(Context mContext, List<News> newsList) {
        this.mContext = mContext;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create a new views
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_list,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final News news = newsList.get(position);
        //repale the content of the view
        holder.title.setText(news.getmTitle());
        holder.sectionName.setText(news.getmSectionName());
        holder.authorName.setText(news.getmAuthorName());

        holder.date.setText(news.getmPublicationDate());

        //recyclerview onclick listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(news.getmURL());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                mContext.startActivity(websiteIntent);
            }
        });

    }

    // Return the size of your dataset
    @Override
    public int getItemCount() {
        return newsList.size();
    }



}
