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
    public interface Listener {
        void open(NewsEntity item);
        void bookmark(NewsEntity item);
    }

    private static final int TYPE_STANDARD = 0;
    private static final int TYPE_FEATURED = 1;

    private final Listener listener;
    private final boolean showBookmark;
    private final boolean featuredFirst;
    private final List<NewsEntity> items = new ArrayList<>();

    public NewsAdapter(Listener listener) {
        this(listener, true, false);
    }

    public NewsAdapter(Listener listener, boolean showBookmark) {
        this(listener, showBookmark, false);
    }

    public NewsAdapter(Listener listener, boolean showBookmark, boolean featuredFirst) {
        this.listener = listener;
        this.showBookmark = showBookmark;
        this.featuredFirst = featuredFirst;
    }

    public void submit(List<NewsEntity> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return featuredFirst && position == 0 ? TYPE_FEATURED : TYPE_STANDARD;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == TYPE_FEATURED ? R.layout.item_news_featured : R.layout.item_news;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new Holder(view, viewType == TYPE_FEATURED);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        NewsEntity article = items.get(position);
        holder.title.setText(article.title);
        holder.summary.setText(article.summary == null ? "" : article.summary);

        String date = article.publishedAt > 0
                ? new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date(article.publishedAt))
                : "时间未提供";
        String meta = (article.sourceName == null ? "" : article.sourceName) + "  ·  " + date;
        if (article.readAt > 0) {
            meta += "  ·  " + (article.readProgress > 0 ? "阅读至 " + article.readProgress + "%" : "已读");
        }
        holder.meta.setText(meta);
        holder.topic.setText(article.topic == null ? "" : article.topic);
        holder.sample.setVisibility("知页样例".equals(article.sourceName) ? View.VISIBLE : View.GONE);
        holder.bookmark.setVisibility(showBookmark ? View.VISIBLE : View.GONE);
        holder.bookmark.setImageResource(article.isBookmarked
                ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);

        if (article.imageUrl != null && !article.imageUrl.isEmpty()) {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(holder.image).load(article.imageUrl).centerCrop()
                    .placeholder(R.drawable.bg_image_placeholder)
                    .error(R.drawable.bg_image_placeholder).into(holder.image);
        } else if (holder.featured) {
            Glide.with(holder.image).clear(holder.image);
            holder.image.setVisibility(View.GONE);
        } else {
            Glide.with(holder.image).clear(holder.image);
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setImageResource(R.drawable.bg_image_placeholder);
        }

        holder.itemView.setOnClickListener(view -> listener.open(article));
        holder.bookmark.setOnClickListener(view -> {
            article.isBookmarked = !article.isBookmarked;
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) notifyItemChanged(currentPosition);
            listener.bookmark(article);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final boolean featured;
        final TextView title, summary, meta, topic, sample;
        final ImageView image;
        final ImageButton bookmark;

        Holder(@NonNull View view, boolean featured) {
            super(view);
            this.featured = featured;
            title = view.findViewById(R.id.article_title);
            summary = view.findViewById(R.id.article_summary);
            meta = view.findViewById(R.id.article_meta);
            topic = view.findViewById(R.id.article_topic);
            sample = view.findViewById(R.id.article_sample);
            image = view.findViewById(R.id.article_image);
            bookmark = view.findViewById(R.id.article_bookmark);
        }
    }
}
