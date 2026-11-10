package com.echo.zhiye.data;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class NewsDaoInstrumentedTest {
    private AppDatabase database;
    private NewsDao dao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = database.newsDao();
    }

    @After
    public void closeDb() {
        if (database != null) database.close();
    }

    @Test
    public void roomPreservesBookmarkAndProgressDuringRefresh() {
        long now = System.currentTimeMillis();
        NewsEntity article = new NewsEntity("test-article", "测试标题", "测试摘要", "测试摘要",
                null, "测试媒体", "test-source", "科技", "https://example.org/story",
                "测试作者", now, false, 0, 0, false, now);
        dao.upsert(article);
        assertEquals("测试标题", dao.findById("test-article").title);

        dao.setBookmark("test-article", true, now + 1);
        dao.updateReadProgress("test-article", 42, now + 2);

        NewsEntity refreshed = new NewsEntity("test-article", "更新后的标题", "更新摘要", "更新摘要",
                null, "测试媒体", "test-source", "科技", "https://example.org/story",
                "测试作者", now + 3, false, 0, 0, false, now + 3);
        assertEquals(-1L, dao.insertIfAbsent(refreshed));
        dao.updateRemoteMetadata(refreshed.id, refreshed.title, refreshed.summary, refreshed.body,
                refreshed.imageUrl, refreshed.sourceName, refreshed.sourceId, refreshed.topic,
                refreshed.author, refreshed.publishedAt);

        NewsEntity afterRefresh = dao.findById("test-article");
        assertEquals("更新后的标题", afterRefresh.title);
        assertTrue(afterRefresh.isBookmarked);
        assertEquals(42, afterRefresh.readProgress);

        dao.clearHistory();
        NewsEntity afterClear = dao.findById("test-article");
        assertNotNull(afterClear);
        assertEquals(0, afterClear.readAt);
        assertEquals(0, afterClear.readProgress);
        assertTrue(afterClear.isBookmarked);
    }

    @Test
    public void refreshDoesNotEraseKnownOptionalMetadataWhenFeedOmitsFields() {
        long publishedAt = 1794300000000L;
        NewsEntity original = new NewsEntity("metadata-article", "原始标题", "原始摘要",
                "原始正文摘要", "https://example.org/cover.jpg", "测试媒体", "test-source",
                "科技", "https://example.org/metadata", "原始作者", publishedAt,
                false, 0, 0, false, publishedAt);
        dao.upsert(original);

        NewsEntity sparseRefresh = new NewsEntity("metadata-article", "更新标题", "", "",
                null, "测试媒体", "test-source", "科技", original.url, null,
                0, false, 0, 0, false, System.currentTimeMillis());
        assertEquals(-1L, dao.insertIfAbsent(sparseRefresh));
        dao.updateRemoteMetadata(sparseRefresh.id, sparseRefresh.title, sparseRefresh.summary,
                sparseRefresh.body, sparseRefresh.imageUrl, sparseRefresh.sourceName,
                sparseRefresh.sourceId, sparseRefresh.topic, sparseRefresh.author,
                sparseRefresh.publishedAt);

        NewsEntity saved = dao.findById("metadata-article");
        assertEquals("更新标题", saved.title);
        assertEquals("原始摘要", saved.summary);
        assertEquals("原始正文摘要", saved.body);
        assertEquals("https://example.org/cover.jpg", saved.imageUrl);
        assertEquals("原始作者", saved.author);
        assertEquals(publishedAt, saved.publishedAt);
    }

    @Test
    public void localNewsCanBeCreatedUpdatedAndDeleted() {
        long now = System.currentTimeMillis();
        NewsEntity local = new NewsEntity("local-article", "本地标题", "本地摘要", "本地正文",
                null, "本地编辑", "local", "文化", null, "本地编辑",
                now, false, 0, 0, true, now);
        dao.upsert(local);
        assertNotNull(dao.findById("local-article"));

        NewsEntity edited = new NewsEntity("local-article", "修改后的标题", "修改后的摘要",
                "修改后的正文", null, "本地编辑", "local", "文化", null, "本地编辑",
                now + 1, false, 0, 0, true, now);
        dao.upsert(edited);
        assertEquals("修改后的标题", dao.findById("local-article").title);

        dao.deleteLocal("local-article");
        assertNull(dao.findById("local-article"));
    }
}
