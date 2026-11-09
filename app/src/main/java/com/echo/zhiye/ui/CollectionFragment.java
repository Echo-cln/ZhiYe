package com.echo.zhiye.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.echo.zhiye.R;
import com.echo.zhiye.data.NewsEntity;
import com.echo.zhiye.viewmodel.NewsViewModel;
import java.util.List;

public class CollectionFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s) { return i.inflate(R.layout.fragment_collection,c,false); }
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s) {
        String mode=getArguments()==null?"bookmarks":getArguments().getString("mode","bookmarks");
        ((TextView)v.findViewById(R.id.collection_title)).setText("history".equals(mode)?"阅读历史":"我的收藏");
        NewsViewModel vm=new ViewModelProvider(this).get(NewsViewModel.class);
        RecyclerView list=v.findViewById(R.id.collection_list); list.setLayoutManager(new LinearLayoutManager(requireContext()));
        NewsAdapter adapter=new NewsAdapter(new NewsAdapter.Listener() {
            @Override public void open(NewsEntity n) { Bundle b=new Bundle(); b.putString("articleId",n.id); Navigation.findNavController(v).navigate(R.id.articleDetailFragment,b); }
            @Override public void bookmark(NewsEntity n) { vm.bookmark(n.id); }
        }); list.setAdapter(adapter);
        LiveData<List<NewsEntity>> data="history".equals(mode)?vm.history():vm.bookmarks();
        data.observe(getViewLifecycleOwner(),items->{ adapter.submit(items); v.findViewById(R.id.collection_empty).setVisibility(items==null||items.isEmpty()?View.VISIBLE:View.GONE); });
    }
}
