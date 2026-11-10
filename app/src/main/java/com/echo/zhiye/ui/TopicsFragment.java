package com.echo.zhiye.ui;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.echo.zhiye.R;
public class TopicsFragment extends Fragment {
 private static final String[][] TOPICS={
  {"科技","人工智能、科学研究、数字产业与技术发展"},
  {"政治","公共政策、国际关系、治理与制度"},
  {"经济","宏观经济、产业、商业与金融市场"},
  {"文化","艺术、阅读、思想与文化产业"},
  {"社会观察","教育、城市、劳动、社区与日常生活"},
  {"娱乐","影视、音乐、演出与大众文化"}
 };
 @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s){return i.inflate(R.layout.fragment_topics,c,false);}
 @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){
  v.findViewById(R.id.topics_back).setOnClickListener(x->Navigation.findNavController(v).popBackStack());
  LinearLayout list=v.findViewById(R.id.topic_list);LayoutInflater inflater=LayoutInflater.from(requireContext());
  for(String[] topic:TOPICS){
   View row=inflater.inflate(R.layout.item_topic,list,false);
   ((TextView)row.findViewById(R.id.topic_name)).setText(topic[0]);
   ((TextView)row.findViewById(R.id.topic_description)).setText(topic[1]);
   row.setOnClickListener(x->{Bundle args=new Bundle();args.putString("topic",topic[0]);Navigation.findNavController(v).navigate(R.id.searchFragment,args);});
   list.addView(row);
  }
 }
}
