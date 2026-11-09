package com.echo.zhiye.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "news_articles", indices = {
        @Index(value = {"url"}, unique = true),
        @Index(value = {"topic", "publishedAt"}),
        @Index(value = {"sourceId", "publishedAt"}),
        @Index(value = {"isBookmarked", "savedAt"})
})
public class NewsEntity {
    @PrimaryKey @NonNull public String id;
    public String title;
    public String summary;
    public String body;
    public String imageUrl;
    public String sourceName;
    public String sourceId;
    public String topic;
    public String url;
    public String author;
    public long publishedAt;
    public boolean isBookmarked;
    public long savedAt;
    public long readAt;
    public boolean isLocal;
    public long createdAt;

    public NewsEntity(@NonNull String id, String title, String summary, String body,
                      String imageUrl, String sourceName, String sourceId, String topic,
                      String url, String author, long publishedAt, boolean isBookmarked,
                      long savedAt, long readAt, boolean isLocal, long createdAt) {
        this.id=id; this.title=title; this.summary=summary; this.body=body; this.imageUrl=imageUrl;
        this.sourceName=sourceName; this.sourceId=sourceId; this.topic=topic; this.url=url;
        this.author=author; this.publishedAt=publishedAt; this.isBookmarked=isBookmarked;
        this.savedAt=savedAt; this.readAt=readAt; this.isLocal=isLocal; this.createdAt=createdAt;
    }
}
