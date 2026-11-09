package com.echo.zhiye.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.echo.zhiye.R;
import com.google.android.material.card.MaterialCardView;
import java.util.Arrays;
import java.util.List;

public class TopicsFragment extends Fragment {
    private static final String[][] DATA={
        {"科技","人工智能、科学研究、数字生活与技术产业"},
        {"政治","公共政策、国际关系、治理与制度"},
        {"经济","宏观经济、金融市场、商业与产业"},
        {"文化","艺术、文学、历史、思想与城市文化"},
        {"社会观察","教育、民生、环境与社会议题"},
        {"娱乐","影视、音乐、演出与内容产业"}
    };
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s) { return i.inflate(R.layout.fragment_topics,c,false); }
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s) {
        android.widget.LinearLayout list=v.findViewById(R.id.topic_list);
        for(String[] d:DATA) {
            MaterialCardView card=(MaterialCardView)LayoutInflater.from(requireContext()).inflate(R.layout.item_topic,list,false);
            ((TextView)card.findViewById(R.id.topic_name)).setText(d[0]);
            ((TextView)card.findViewById(R.id.topic_description)).setText(d[1]);
            card.setOnClickListener(x->{ Bundle b=new Bundle(); b.putString("topic",d[0]); Navigation.findNavController(v).navigate(R.id.searchFragment,b); });
            list.addView(card);
        }
    }
}
