package com.echo.zhiye.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.echo.zhiye.R;
import com.echo.zhiye.data.NewsEntity;
import com.echo.zhiye.viewmodel.NewsViewModel;
import java.util.List;

public class SearchFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s) { return i.inflate(R.layout.fragment_search,c,false); }
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s) {
        NewsViewModel vm=new ViewModelProvider(this).get(NewsViewModel.class);
        String topic=getArguments()==null?"全部":getArguments().getString("topic","全部");
        String source=getArguments()==null?"全部":getArguments().getString("source","全部");
        EditText input=v.findViewById(R.id.search_input);
        RecyclerView list=v.findViewById(R.id.search_results);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        NewsAdapter adapter=new NewsAdapter(new NewsAdapter.Listener() {
            @Override public void open(NewsEntity n) { vm.markRead(n.id); Bundle b=new Bundle(); b.putString("articleId",n.id); Navigation.findNavController(v).navigate(R.id.articleDetailFragment,b); }
            @Override public void bookmark(NewsEntity n) { vm.bookmark(n.id); }
        });
        list.setAdapter(adapter);
        MutableLiveData<String> query=new MutableLiveData<>("");
        LiveData<List<NewsEntity>> results=Transformations.switchMap(query,q->vm.search(q,topic,source));
        results.observe(getViewLifecycleOwner(),adapter::submit);
        if(!"全部".equals(topic)) input.setHint("在「"+topic+"」中搜索");
        if(!"全部".equals(source)) input.setHint("在 "+source+" 中搜索");
        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int st,int c,int a) {}
            @Override public void onTextChanged(CharSequence s,int st,int before,int count) { query.setValue(s.toString()); }
            @Override public void afterTextChanged(Editable e) {}
        });
    }
}
