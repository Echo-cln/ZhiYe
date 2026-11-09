package com.echo.zhiye.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.echo.zhiye.R;
import com.echo.zhiye.data.NewsEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.Holder> {
    public interface Listener { void open(NewsEntity item); void bookmark(NewsEntity item); }
    private final Listener listener;
    private final List<NewsEntity> items=new ArrayList<>();
    public NewsAdapter(Listener listener) { this.listener=listener; }
    public void submit(List<NewsEntity> list) { items.clear(); if(list!=null) items.addAll(list); notifyDataSetChanged(); }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup p,int t) {
        return new Holder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_news,p,false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h,int pos) {
        NewsEntity n=items.get(pos);
        h.title.setText(n.title); h.summary.setText(n.summary==null?"":n.summary);
        h.meta.setText((n.sourceName==null?"":n.sourceName)+"  ·  "+new SimpleDateFormat("MM-dd HH:mm",Locale.CHINA).format(new Date(n.publishedAt)));
        h.topic.setText(n.topic==null?"":n.topic);
        h.sample.setVisibility("知页样例".equals(n.sourceName)?View.VISIBLE:View.GONE);
        h.bookmark.setImageResource(n.isBookmarked?android.R.drawable.btn_star_big_on:android.R.drawable.btn_star_big_off);
        if(n.imageUrl!=null && !n.imageUrl.isEmpty()) Glide.with(h.image).load(n.imageUrl).centerCrop()
                .placeholder(R.drawable.bg_image_placeholder).error(R.drawable.bg_image_placeholder).into(h.image);
        else h.image.setImageResource(R.drawable.bg_image_placeholder);
        h.itemView.setOnClickListener(v->listener.open(n));
        h.bookmark.setOnClickListener(v->listener.bookmark(n));
    }
    @Override public int getItemCount() { return items.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        TextView title,summary,meta,topic,sample; ImageView image; ImageButton bookmark;
        Holder(@NonNull View v) { super(v); title=v.findViewById(R.id.article_title); summary=v.findViewById(R.id.article_summary);
            meta=v.findViewById(R.id.article_meta); topic=v.findViewById(R.id.article_topic);
            sample=v.findViewById(R.id.article_sample); image=v.findViewById(R.id.article_image); bookmark=v.findViewById(R.id.article_bookmark); }
    }
}
