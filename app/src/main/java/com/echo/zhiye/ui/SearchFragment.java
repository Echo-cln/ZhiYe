package com.echo.zhiye.ui;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.echo.zhiye.data.NewsSource;
import com.echo.zhiye.viewmodel.NewsViewModel;
import java.util.ArrayList;
import java.util.List;
public class SearchFragment extends Fragment {
 private static class SearchState{
  final String query,topic,source;
  SearchState(String q,String t,String s){query=q;topic=t;source=s;}
 }
 @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s){return i.inflate(R.layout.fragment_search,c,false);}
 @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){
  NewsViewModel vm=new ViewModelProvider(this).get(NewsViewModel.class);
  String initialTopic=getArguments()==null?"全部":getArguments().getString("topic","全部");
  String initialSource=getArguments()==null?"全部":getArguments().getString("source","全部");
  v.findViewById(R.id.search_back).setOnClickListener(x->Navigation.findNavController(v).popBackStack());
  EditText input=v.findViewById(R.id.search_input);Spinner topicFilter=v.findViewById(R.id.search_topic_filter);
  Spinner sourceFilter=v.findViewById(R.id.search_source_filter);TextView count=v.findViewById(R.id.search_count);
  RecyclerView list=v.findViewById(R.id.search_results);list.setLayoutManager(new LinearLayoutManager(requireContext()));
  NewsAdapter adapter=new NewsAdapter(new NewsAdapter.Listener(){
   @Override public void open(NewsEntity n){vm.markRead(n.id);Bundle b=new Bundle();b.putString("articleId",n.id);Navigation.findNavController(v).navigate(R.id.articleDetailFragment,b);}
   @Override public void bookmark(NewsEntity n){vm.setBookmark(n.id,n.isBookmarked);}
  });list.setAdapter(adapter);
  List<String> topics=new ArrayList<>();topics.add("全部");topics.add("科技");topics.add("政治");topics.add("经济");topics.add("文化");topics.add("社会观察");topics.add("娱乐");
  topicFilter.setAdapter(new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_dropdown_item,topics));
  List<String> sources=new ArrayList<>();sources.add("全部");for(NewsSource source:NewsSource.all())if(!sources.contains(source.name))sources.add(source.name);
  sourceFilter.setAdapter(new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_dropdown_item,sources));
  topicFilter.setSelection(Math.max(0,topics.indexOf(initialTopic)));sourceFilter.setSelection(Math.max(0,sources.indexOf(initialSource)));
  MutableLiveData<SearchState> state=new MutableLiveData<>(new SearchState("",initialTopic,initialSource));
  LiveData<List<NewsEntity>> results=Transformations.switchMap(state,f->vm.search(f.query,f.topic,f.source));
  results.observe(getViewLifecycleOwner(),items->{adapter.submit(items);int size=items==null?0:items.size();count.setText(size+" 条结果");v.findViewById(R.id.search_empty).setVisibility(size==0?View.VISIBLE:View.GONE);});
  input.addTextChangedListener(new TextWatcher(){
   @Override public void beforeTextChanged(CharSequence s,int st,int c,int a){}
   @Override public void onTextChanged(CharSequence s,int st,int before,int c){SearchState old=state.getValue();state.setValue(new SearchState(s.toString().trim(),old==null?"全部":old.topic,old==null?"全部":old.source));}
   @Override public void afterTextChanged(Editable e){}
  });
  topicFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
   @Override public void onItemSelected(AdapterView<?> p,View x,int pos,long id){SearchState old=state.getValue();state.setValue(new SearchState(old==null?"":old.query,topics.get(pos),old==null?"全部":old.source));}
   @Override public void onNothingSelected(AdapterView<?> p){}
  });
  sourceFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
   @Override public void onItemSelected(AdapterView<?> p,View x,int pos,long id){SearchState old=state.getValue();state.setValue(new SearchState(old==null?"":old.query,old==null?"全部":old.topic,sources.get(pos)));}
   @Override public void onNothingSelected(AdapterView<?> p){}
  });
 }
}
