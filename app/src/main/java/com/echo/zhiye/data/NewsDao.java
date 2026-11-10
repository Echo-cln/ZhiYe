package com.echo.zhiye.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NewsDao {
    @Query("SELECT * FROM news_articles ORDER BY publishedAt DESC")
    LiveData<List<NewsEntity>> observeAll();

    @Query("SELECT * FROM news_articles WHERE (:topic='全部' OR topic=:topic) AND (:source='全部' OR sourceName=:source) ORDER BY publishedAt DESC")
    LiveData<List<NewsEntity>> observeFiltered(String topic, String source);

    @Query("SELECT * FROM news_articles WHERE isBookmarked=1 ORDER BY savedAt DESC")
    LiveData<List<NewsEntity>> observeBookmarks();

    @Query("SELECT * FROM news_articles WHERE readAt>0 ORDER BY readAt DESC")
    LiveData<List<NewsEntity>> observeHistory();

    @Query("SELECT * FROM news_articles WHERE isLocal=1 ORDER BY createdAt DESC")
    LiveData<List<NewsEntity>> observeLocal();

    @Query("SELECT * FROM news_articles WHERE id=:id LIMIT 1")
    NewsEntity findById(String id);

    @Query("SELECT COUNT(*) FROM news_articles")
    int countAll();

    @Query("SELECT * FROM news_articles WHERE (:topic='全部' OR topic=:topic) AND (:source='全部' OR sourceName=:source) AND (title LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%' OR sourceName LIKE '%' || :query || '%') ORDER BY publishedAt DESC")
    LiveData<List<NewsEntity>> search(String query, String topic, String source);

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void upsertAll(List<NewsEntity> articles);

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void upsert(NewsEntity article);

    @Insert(onConflict=OnConflictStrategy.IGNORE)
    long insertIfAbsent(NewsEntity article);

    @Query("UPDATE news_articles SET " +
            "title=CASE WHEN :title IS NOT NULL AND TRIM(:title)<>'' THEN :title ELSE title END, " +
            "summary=CASE WHEN :summary IS NOT NULL AND TRIM(:summary)<>'' THEN :summary ELSE summary END, " +
            "body=CASE WHEN :body IS NOT NULL AND TRIM(:body)<>'' THEN :body ELSE body END, " +
            "imageUrl=CASE WHEN :imageUrl IS NOT NULL AND TRIM(:imageUrl)<>'' THEN :imageUrl ELSE imageUrl END, " +
            "sourceName=CASE WHEN :sourceName IS NOT NULL AND TRIM(:sourceName)<>'' THEN :sourceName ELSE sourceName END, " +
            "sourceId=CASE WHEN :sourceId IS NOT NULL AND TRIM(:sourceId)<>'' THEN :sourceId ELSE sourceId END, " +
            "topic=CASE WHEN :topic IS NOT NULL AND TRIM(:topic)<>'' THEN :topic ELSE topic END, " +
            "author=CASE WHEN :author IS NOT NULL AND TRIM(:author)<>'' THEN :author ELSE author END, " +
            "publishedAt=CASE WHEN :publishedAt>0 THEN :publishedAt ELSE publishedAt END " +
            "WHERE id=:id AND isLocal=0")
    void updateRemoteMetadata(String id, String title, String summary, String body, String imageUrl,
                              String sourceName, String sourceId, String topic, String author, long publishedAt);

    @Query("UPDATE news_articles SET isBookmarked=:saved,savedAt=:savedAt WHERE id=:id")
    void setBookmark(String id, boolean saved, long savedAt);

    @Query("UPDATE news_articles SET readAt=:readAt,readProgress=:progress WHERE id=:id AND readAt<=:readAt")
    void updateReadProgress(String id, int progress, long readAt);

    @Query("UPDATE news_articles SET readAt=:readAt WHERE id=:id")
    void markRead(String id, long readAt);

    @Query("DELETE FROM news_articles WHERE id=:id AND isLocal=1")
    void deleteLocal(String id);

    @Query("UPDATE news_articles SET readAt=0,readProgress=0 WHERE readAt>0")
    void clearHistory();
}
