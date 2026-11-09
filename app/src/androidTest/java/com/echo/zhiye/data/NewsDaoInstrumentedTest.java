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

    @Test public void roomSupportsNewsCreateReadBookmarkHistoryAndDelete() {
        long now=System.currentTimeMillis();
        NewsEntity article=new NewsEntity("test-article","测试标题","测试摘要","测试正文",
                null,"测试媒体","test-source","科技",null,"测试作者",now,
                false,0,0,true,now);

        dao.upsert(article);
        NewsEntity inserted=dao.findById("test-article");
        assertNotNull(inserted);
        assertEquals("测试标题",inserted.title);

        dao.setBookmark("test-article",true,now+1);
        dao.markRead("test-article",now+2);
        NewsEntity updated=dao.findById("test-article");
        assertTrue(updated.isBookmarked);
        assertTrue(updated.readAt>0);

        dao.deleteLocal("test-article");
        assertNull(dao.findById("test-article"));
    }
}
