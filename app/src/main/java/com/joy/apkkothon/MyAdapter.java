package com.joy.apkkothon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    ArrayList<Post> postLists;
    Context context;
    public MyAdapter(Context context, List<Post> postLists){
        this.postLists= (ArrayList<Post>) postLists;
        this.context=context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_row_view_item,parent,false);
        MyViewHolder holder=new MyViewHolder(view,context,postLists);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Post current=postLists.get(position);

        holder.Title.setText(Html.fromHtml(current.getTitle()));
        holder.Date.setText(current.getDate());
        Picasso.with(context).load(current.getThumbnail()).error(R.drawable.apkkothonsqrlogo).placeholder(R.drawable.apkkothonsqrlogo).into(holder.Thumbnail);

    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Title,Date;
        ImageView Thumbnail;
        Context context;
        Typeface bdfont=Typeface.createFromAsset(itemView.getContext().getAssets(),"font/SolaimanLipi.ttf");
        ArrayList<Post> postLists=new ArrayList<>();
        public MyViewHolder(View itemView, Context context, ArrayList<Post> postLists) {
            super(itemView);
            this.context= context;
            this.postLists= postLists;
            Title= (TextView) itemView.findViewById(R.id.title_text);
            Title.setTypeface(bdfont);
            Date= (TextView) itemView.findViewById(R.id.date_text);
            Thumbnail= (ImageView) itemView.findViewById(R.id.icon_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            Post post= this.postLists.get(position);
            Intent intent=new Intent(this.context,NewsDetailsActivity.class);
            intent.putExtra("title",post.getTitle());
            intent.putExtra("URL",post.getUrl());
            intent.putExtra("content",post.getContent());
            intent.putExtra("date",post.getDate());
            intent.putExtra("author",post.getAuthor());
            context.startActivity(intent);

        }
    }
}
