package com.echo.zhiye.data;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
@Entity(tableName="news_articles",indices={@Index(value={"url"},unique=true),@Index(value={"topic","publishedAt"}),@Index(value={"sourceId","publishedAt"}),@Index(value={"isBookmarked","savedAt"})})
public class NewsEntity {
 @PrimaryKey @NonNull public String id;
 public String title,summary,body,imageUrl,sourceName,sourceId,topic,url,author;
 public long publishedAt,savedAt,readAt,createdAt;
 public boolean isBookmarked,isLocal;
 @ColumnInfo(defaultValue="0") public int readProgress;
 public NewsEntity(@NonNull String id,String title,String summary,String body,String imageUrl,String sourceName,String sourceId,String topic,String url,String author,long publishedAt,boolean isBookmarked,long savedAt,long readAt,boolean isLocal,long createdAt) {
  this.id=id;this.title=title;this.summary=summary;this.body=body;this.imageUrl=imageUrl;this.sourceName=sourceName;this.sourceId=sourceId;this.topic=topic;this.url=url;this.author=author;this.publishedAt=publishedAt;this.isBookmarked=isBookmarked;this.savedAt=savedAt;this.readAt=readAt;this.isLocal=isLocal;this.createdAt=createdAt;this.readProgress=0;
 }
}
