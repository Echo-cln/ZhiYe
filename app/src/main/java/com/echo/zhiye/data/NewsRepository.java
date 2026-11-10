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
    public interface RefreshCallback {
        void onComplete(int newArticles, int failedFeeds);
    }

    private static volatile NewsRepository INSTANCE;
    private final NewsDao dao;
    private final ExecutorService io = Executors.newFixedThreadPool(4);
    private final ExecutorService progressIo = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private final RssParser parser = new RssParser();

    private NewsRepository(Context context) {
        dao = AppDatabase.get(context).newsDao();
    }

    public static NewsRepository get(Context context) {
        if (INSTANCE == null) {
            synchronized (NewsRepository.class) {
                if (INSTANCE == null) INSTANCE = new NewsRepository(context.getApplicationContext());
            }
        }
        return INSTANCE;
    }

    public LiveData<List<NewsEntity>> all() { return dao.observeAll(); }
    public LiveData<List<NewsEntity>> filtered(String topic, String source) { return dao.observeFiltered(topic, source); }
    public LiveData<List<NewsEntity>> bookmarks() { return dao.observeBookmarks(); }
    public LiveData<List<NewsEntity>> history() { return dao.observeHistory(); }
    public LiveData<List<NewsEntity>> local() { return dao.observeLocal(); }
    public LiveData<List<NewsEntity>> search(String query, String topic, String source) {
        return dao.search(query, topic, source);
    }

    public void byId(String id, java.util.function.Consumer<NewsEntity> callback) {
        io.execute(() -> {
            NewsEntity article = dao.findById(id);
            main.post(() -> callback.accept(article));
        });
    }

    public void setBookmark(String id, boolean saved) {
        io.execute(() -> dao.setBookmark(id, saved, saved ? System.currentTimeMillis() : 0));
    }

    public void markRead(String id) {
        progressIo.execute(() -> dao.markRead(id, System.currentTimeMillis()));
    }

    public void updateReadProgress(String id, int progress) {
        int safe = Math.max(0, Math.min(100, progress));
        progressIo.execute(() -> dao.updateReadProgress(id, safe, System.currentTimeMillis()));
    }

    public void save(NewsEntity article) { io.execute(() -> dao.upsert(article)); }
    public void deleteLocal(String id) { io.execute(() -> dao.deleteLocal(id)); }

    public void clearHistory(Runnable done) {
        io.execute(() -> {
            dao.clearHistory();
            if (done != null) main.post(done);
        });
    }

    public void refresh(RefreshCallback callback) {
        List<NewsSource> feeds = new ArrayList<>();
        for (NewsSource source : NewsSource.all()) {
            if (source.hasFeed()) feeds.add(source);
        }
        if (feeds.isEmpty()) {
            if (callback != null) main.post(() -> callback.onComplete(0, 0));
            return;
        }

        AtomicInteger remaining = new AtomicInteger(feeds.size());
        AtomicInteger newArticles = new AtomicInteger(0);
        AtomicInteger failedFeeds = new AtomicInteger(0);

        for (NewsSource source : feeds) {
            io.execute(() -> {
                try {
                    List<NewsEntity> articles = parser.fetch(source);
                    for (NewsEntity article : articles) {
                        long insertedRowId = dao.insertIfAbsent(article);
                        if (insertedRowId != -1L) newArticles.incrementAndGet();
                        dao.updateRemoteMetadata(article.id, article.title, article.summary, article.body,
                                article.imageUrl, article.sourceName, article.sourceId, article.topic,
                                article.author, article.publishedAt);
                    }
                } catch (Exception ignored) {
                    failedFeeds.incrementAndGet();
                }

                if (remaining.decrementAndGet() == 0 && callback != null) {
                    int inserted = newArticles.get();
                    int failed = failedFeeds.get();
                    main.post(() -> callback.onComplete(inserted, failed));
                }
            });
        }
    }

    public void seedSamples() {
        io.execute(() -> {
            if (dao.countAll() > 0) return;
            List<NewsEntity> samples = new ArrayList<>();
            long now = System.currentTimeMillis();
            samples.add(sample("sample-01",
                    "当人工智能走进课堂，我们该如何学习",
                    "工具正在改变获取知识的方式，也让独立思考变得更加重要。",
                    "科技", now));
            samples.add(sample("sample-02",
                    "公共空间，正在重新连接日常生活",
                    "从街角公园到社区书店，重新发现日常生活的连接。",
                    "社会观察", now - 3600000));
            samples.add(sample("sample-03",
                    "从课堂到开源社区，年轻开发者迈出第一步",
                    "校园与技术创新的真实故事。",
                    "校园", now - 7200000));
            samples.add(sample("sample-04",
                    "理解数据隐私，从一次应用授权开始",
                    "在数字生活中，如何更清晰地管理个人信息。",
                    "科技", now - 10800000));
            samples.add(sample("sample-05",
                    "观察经济数据，也要看见数据背后的普通生活",
                    "就业、消费、产业投资与家庭预期共同构成经济运行的现实图景。",
                    "经济", now - 14400000));
            samples.add(sample("sample-06",
                    "影视与音乐产业：内容之外，还有新的观看方式",
                    "流媒体、院线、现场演出与粉丝文化共同影响文娱产业结构。",
                    "娱乐", now - 18000000));
            dao.upsertAll(samples);
        });
    }

    private NewsEntity sample(String id, String title, String summary, String topic, long time) {
        return new NewsEntity(id, title, summary, summary, null, "知页样例", "sample",
                topic, null, "知页编辑部（样例）", time, false, 0, 0, false, time);
    }
}
