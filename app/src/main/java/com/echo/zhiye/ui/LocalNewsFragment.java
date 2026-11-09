package com.echo.zhiye.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.echo.zhiye.R;
import com.echo.zhiye.data.NewsEntity;
import com.echo.zhiye.viewmodel.NewsViewModel;

public class LocalNewsFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s) { return i.inflate(R.layout.fragment_local_news,c,false); }
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s) {
        NewsViewModel vm=new ViewModelProvider(this).get(NewsViewModel.class);
        RecyclerView list=v.findViewById(R.id.local_news_list); list.setLayoutManager(new LinearLayoutManager(requireContext()));
        NewsAdapter adapter=new NewsAdapter(new NewsAdapter.Listener() {
            @Override public void open(NewsEntity n) { Bundle b=new Bundle(); b.putString("articleId",n.id); Navigation.findNavController(v).navigate(R.id.editorFragment,b); }
            @Override public void bookmark(NewsEntity n) {
                new AlertDialog.Builder(requireContext()).setTitle("删除本地新闻？")
                    .setMessage("删除后无法恢复。").setNegativeButton("取消",null)
                    .setPositiveButton("删除",(d,w)->vm.deleteLocal(n.id)).show();
            }
        }); list.setAdapter(adapter);
        vm.local().observe(getViewLifecycleOwner(),items->{
            adapter.submit(items);
            v.findViewById(R.id.local_empty).setVisibility(items==null||items.isEmpty()?View.VISIBLE:View.GONE);
        });
        v.findViewById(R.id.local_add).setOnClickListener(x->Navigation.findNavController(v).navigate(R.id.editorFragment));
    }
}
