package com.echo.zhiye.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class NewsRepository {
    public interface RefreshCallback { void onComplete(int imported,int failed); }
    private static volatile NewsRepository INSTANCE;
    private final NewsDao dao;
    private final ExecutorService io=Executors.newFixedThreadPool(4);
    private final Handler main=new Handler(Looper.getMainLooper());
    private final RssParser parser=new RssParser();
    private NewsRepository(Context c) { dao=AppDatabase.get(c).newsDao(); }
    public static NewsRepository get(Context c) {
        if(INSTANCE==null) synchronized(NewsRepository.class) {
            if(INSTANCE==null) INSTANCE=new NewsRepository(c.getApplicationContext());
        }
        return INSTANCE;
    }
    public LiveData<List<NewsEntity>> all() { return dao.observeAll(); }
    public LiveData<List<NewsEntity>> filtered(String topic,String source) { return dao.observeFiltered(topic,source); }
    public LiveData<List<NewsEntity>> bookmarks() { return dao.observeBookmarks(); }
    public LiveData<List<NewsEntity>> history() { return dao.observeHistory(); }
    public LiveData<List<NewsEntity>> local() { return dao.observeLocal(); }
    public LiveData<List<NewsEntity>> search(String q,String topic,String source) { return dao.search(q,topic,source); }
    public void byId(String id,java.util.function.Consumer<NewsEntity> cb) {
        io.execute(()->{ NewsEntity n=dao.findById(id); main.post(()->cb.accept(n)); });
    }
    public void bookmark(String id) {
        io.execute(()->{ NewsEntity n=dao.findById(id); if(n!=null) {
            boolean saved=!n.isBookmarked; dao.setBookmark(id,saved,saved?System.currentTimeMillis():0);
        }});
    }
    public void markRead(String id) { io.execute(()->dao.markRead(id,System.currentTimeMillis())); }
    public void save(NewsEntity n) { io.execute(()->dao.upsert(n)); }
    public void deleteLocal(String id) { io.execute(()->dao.deleteLocal(id)); }
    public void clearHistory(Runnable done) {
        io.execute(()->{ dao.clearHistory(); if(done!=null) main.post(done); });
    }
    public void refresh(RefreshCallback cb) {
        List<NewsSource> feeds=new ArrayList<>();
        for(NewsSource s:NewsSource.all()) if(s.hasFeed()) feeds.add(s);
        if(feeds.isEmpty()) { if(cb!=null) main.post(()->cb.onComplete(0,0)); return; }
        AtomicInteger remaining=new AtomicInteger(feeds.size());
        AtomicInteger imported=new AtomicInteger(0), failed=new AtomicInteger(0);
        for(NewsSource source:feeds) io.execute(()->{
            try {
                List<NewsEntity> articles=parser.fetch(source);
                for(NewsEntity article:articles) {
                    // Insert new records, then refresh only publisher metadata for existing records.
                    // User-owned bookmark/read state is intentionally preserved across syncs.
                    dao.insertIfAbsent(article);
                    dao.updateRemoteMetadata(article.id,article.title,article.summary,article.body,
                            article.imageUrl,article.sourceName,article.sourceId,article.topic,
                            article.author,article.publishedAt);
                }
                imported.addAndGet(articles.size());
            } catch(Exception e) { failed.incrementAndGet(); }
            if(remaining.decrementAndGet()==0 && cb!=null)
                main.post(()->cb.onComplete(imported.get(),failed.get()));
        });
    }
    public void seedSamples() {
        io.execute(()->{
            if(dao.countAll()>0) return;
            List<NewsEntity> list=new ArrayList<>(); long now=System.currentTimeMillis();
            list.add(sample("sample-01","当人工智能进入日常工具，下一步是重新设计人与技术的关系","从生成式模型到个人智能助理，技术变化正在改变普通人的工作与学习方式。","科技",now));
            list.add(sample("sample-02","全球公共议题的讨论，正在回到制度与治理本身","政策如何形成、公共资源如何分配，是观察政治的重要入口。","政治",now-3600000));
            list.add(sample("sample-03","观察经济数据，也要看见数据背后的普通生活","就业、消费、产业投资与家庭预期共同构成经济运行的现实图景。","经济",now-7200000));
            list.add(sample("sample-04","城市文化空间如何在更新中保留公共性","书店、剧场、博物馆与街区文化，成为理解城市生活的窗口。","文化",now-10800000));
            list.add(sample("sample-05","从一项社区服务，看公共生活的细微变化","社会观察不只关注宏大议题，也关心制度如何落到具体的人。","社会观察",now-14400000));
            list.add(sample("sample-06","影视与音乐产业：内容之外，还有新的观看方式","流媒体、院线、现场演出与粉丝文化共同影响文娱产业结构。","娱乐",now-18000000));
            dao.upsertAll(list);
        });
    }
    private NewsEntity sample(String id,String title,String summary,String topic,long time) {
        return new NewsEntity(id,title,summary,summary,null,"知页样例","sample",topic,null,
                "知页编辑部（样例）",time,false,0,0,false,time);
    }
}
