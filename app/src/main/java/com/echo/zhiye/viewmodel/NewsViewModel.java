package com.echo.zhiye.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.echo.zhiye.data.NewsEntity;
import com.echo.zhiye.data.NewsRepository;
import java.util.List;

public class NewsViewModel extends AndroidViewModel {
    private final NewsRepository repository;
    public NewsViewModel(@NonNull Application app) { super(app); repository=NewsRepository.get(app); }
    public LiveData<List<NewsEntity>> all() { return repository.all(); }
    public LiveData<List<NewsEntity>> filtered(String topic,String source) { return repository.filtered(topic,source); }
    public LiveData<List<NewsEntity>> bookmarks() { return repository.bookmarks(); }
    public LiveData<List<NewsEntity>> history() { return repository.history(); }
    public LiveData<List<NewsEntity>> local() { return repository.local(); }
    public LiveData<List<NewsEntity>> search(String q,String topic,String source) { return repository.search(q,topic,source); }
    public void refresh(NewsRepository.RefreshCallback cb) { repository.refresh(cb); }
    public void seedSamples() { repository.seedSamples(); }
    public void bookmark(String id) { repository.bookmark(id); }
    public void markRead(String id) { repository.markRead(id); }
    public void save(NewsEntity n) { repository.save(n); }
    public void deleteLocal(String id) { repository.deleteLocal(id); }
    public void byId(String id,java.util.function.Consumer<NewsEntity> cb) { repository.byId(id,cb); }
    public void clearHistory(Runnable done) { repository.clearHistory(done); }
}
