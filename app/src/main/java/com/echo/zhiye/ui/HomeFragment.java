package com.echo.zhiye.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.echo.zhiye.R;
import com.echo.zhiye.data.NewsEntity;
import com.echo.zhiye.data.NewsRepository;
import com.echo.zhiye.data.NewsSource;
import com.echo.zhiye.viewmodel.NewsViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private NewsViewModel vm;
    private NewsAdapter adapter;
    private SwipeRefreshLayout refresh;
    private String topic="全部", source="全部";
    private View root;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s) {
        return i.inflate(R.layout.fragment_home,c,false);
    }
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s) {
        root=v; vm=new ViewModelProvider(this).get(NewsViewModel.class);
        ((TextView)v.findViewById(R.id.edition_date)).setText("今日版  ·  "+new SimpleDateFormat("yyyy.MM.dd",Locale.CHINA).format(new Date()));
        RecyclerView list=v.findViewById(R.id.news_recycler); list.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter=new NewsAdapter(new NewsAdapter.Listener() {
            @Override public void open(NewsEntity n) {
                vm.markRead(n.id); Bundle b=new Bundle(); b.putString("articleId",n.id);
                Navigation.findNavController(requireView()).navigate(R.id.articleDetailFragment,b);
            }
            @Override public void bookmark(NewsEntity n) { vm.bookmark(n.id); Toast.makeText(requireContext(),n.isBookmarked?"已取消收藏":"已加入收藏",Toast.LENGTH_SHORT).show(); }
        }); list.setAdapter(adapter);
        ChipGroup chips=v.findViewById(R.id.topic_chips);
        String[] topics={"全部","科技","政治","经济","文化","社会观察","娱乐"};
        for(String t:topics) { Chip chip=new Chip(requireContext()); chip.setText(t); chip.setCheckable(true);
            chip.setOnClickListener(x->{topic=t; observe();}); chips.addView(chip); if(t.equals("全部")) chip.setChecked(true); }
        Spinner spinner=v.findViewById(R.id.source_filter);
        List<String> names=new ArrayList<>(); names.add("全部");
        for(NewsSource ns:NewsSource.all()) if(!names.contains(ns.name)) names.add(ns.name);
        spinner.setAdapter(new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_dropdown_item,names));
        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> p,View x,int pos,long id) { source=names.get(pos); observe(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });
        v.findViewById(R.id.search_button).setOnClickListener(x->Navigation.findNavController(v).navigate(R.id.searchFragment));
        v.findViewById(R.id.refresh_button).setOnClickListener(x->refreshNews());
        refresh=v.findViewById(R.id.swipe_refresh); refresh.setOnRefreshListener(this::refreshNews);
        refresh.setColorSchemeResources(R.color.ink);
        observe(); vm.seedSamples();
    }
    private void observe() {
        vm.filtered(topic,source).observe(getViewLifecycleOwner(),items->{
            adapter.submit(items);
            root.findViewById(R.id.empty_state).setVisibility(items==null||items.isEmpty()?View.VISIBLE:View.GONE);
        });
    }
    private void refreshNews() {
        refresh.setRefreshing(true);
        vm.refresh((count,failed)->{
            if(!isAdded()) return; refresh.setRefreshing(false);
            String msg=count>0?"已同步 "+count+" 条新闻":"暂未获取到新内容";
            if(failed>0) msg+="；"+failed+" 个订阅源暂不可用";
            Toast.makeText(requireContext(),msg,Toast.LENGTH_LONG).show();
        });
    }
}
