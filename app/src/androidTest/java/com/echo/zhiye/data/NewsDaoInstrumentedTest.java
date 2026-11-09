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

    @Before public void setUp() {
        Context context=ApplicationProvider.getApplicationContext();
        database=Room.inMemoryDatabaseBuilder(context,AppDatabase.class)
                .allowMainThreadQueries().build();
        dao=database.newsDao();
    }

    @After public void closeDb() {
        if(database!=null) database.close();
    }

    @Test public void roomPreservesBookmarkAndHistoryDuringRefresh() {
        long now=System.currentTimeMillis();
        NewsEntity article=new NewsEntity("test-article","测试标题","测试摘要","测试摘要",
                null,"测试媒体","test-source","科技","https://example.org/story","测试作者",now,
                false,0,0,false,now);
        dao.upsert(article);
        assertEquals("测试标题",dao.findById("test-article").title);

        dao.setBookmark("test-article",true,now+1);
        dao.markRead("test-article",now+2);
        NewsEntity refreshed=new NewsEntity("test-article","更新后的标题","更新摘要","更新摘要",
                null,"测试媒体","test-source","科技","https://example.org/story","测试作者",now+3,
                false,0,0,false,now+3);
        dao.insertIfAbsent(refreshed);
        dao.updateRemoteMetadata(refreshed.id,refreshed.title,refreshed.summary,refreshed.body,
                refreshed.imageUrl,refreshed.sourceName,refreshed.sourceId,refreshed.topic,
                refreshed.author,refreshed.publishedAt);
        NewsEntity afterRefresh=dao.findById("test-article");
        assertEquals("更新后的标题",afterRefresh.title);
        assertTrue("refresh must preserve bookmark",afterRefresh.isBookmarked);
        assertTrue("refresh must preserve reading history",afterRefresh.readAt>0);

        dao.clearHistory();
        NewsEntity afterClear=dao.findById("test-article");
        assertNotNull(afterClear);
        assertEquals(0,afterClear.readAt);
        assertTrue(afterClear.isBookmarked);
    }

    @Test public void localNewsCanBeCreatedAndDeleted() {
        long now=System.currentTimeMillis();
        NewsEntity local=new NewsEntity("local-article","本地标题","本地摘要","本地正文",
                null,"本地编辑","local","文化",null,"本地编辑",now,false,0,0,true,now);
        dao.upsert(local);
        assertNotNull(dao.findById("local-article"));
        dao.deleteLocal("local-article");
        assertNull(dao.findById("local-article"));
    }
}
