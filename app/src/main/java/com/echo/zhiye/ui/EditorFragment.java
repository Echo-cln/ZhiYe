package com.echo.zhiye.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.echo.zhiye.R;
import com.echo.zhiye.data.NewsEntity;
import com.echo.zhiye.viewmodel.NewsViewModel;
import java.util.UUID;

public class EditorFragment extends Fragment {
    private NewsViewModel vm;
    private EditText title,summary,body;
    private Spinner topic;
    private String articleId;
    private NewsEntity editing;
    private View saveButton,deleteButton;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s) { return i.inflate(R.layout.fragment_editor,c,false); }
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s) {
        vm=new ViewModelProvider(this).get(NewsViewModel.class);
        title=v.findViewById(R.id.editor_title); summary=v.findViewById(R.id.editor_summary); body=v.findViewById(R.id.editor_body); topic=v.findViewById(R.id.editor_topic);
        String[] topics={"科技","政治","经济","文化","社会观察","娱乐"};
        topic.setAdapter(new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_dropdown_item,topics));
        saveButton=v.findViewById(R.id.editor_save); deleteButton=v.findViewById(R.id.editor_delete);
        articleId=getArguments()==null?null:getArguments().getString("articleId");
        if(articleId!=null) {
            saveButton.setEnabled(false); deleteButton.setVisibility(View.VISIBLE); deleteButton.setEnabled(false);
            vm.byId(articleId,n->{
                if(!isAdded()||n==null)return;
                editing=n; title.setText(n.title); summary.setText(n.summary); body.setText(n.body);
                for(int i=0;i<topics.length;i++) if(topics[i].equals(n.topic)) { topic.setSelection(i); break; }
                saveButton.setEnabled(true); deleteButton.setEnabled(true);
            });
            deleteButton.setOnClickListener(x->{
                new androidx.appcompat.app.AlertDialog.Builder(requireContext()).setTitle("删除这条新闻？")
                        .setMessage("删除后无法恢复。").setNegativeButton("取消",null)
                        .setPositiveButton("删除",(d,w)->{ vm.deleteLocal(articleId); Navigation.findNavController(v).popBackStack(); }).show();
            });
        }
        saveButton.setOnClickListener(x->save(v));
    }
    private void save(View v) {
        String t=title.getText().toString().trim();
        if(t.isEmpty()) { title.setError("请输入标题"); return; }
        long now=System.currentTimeMillis();
        String id=editing==null?UUID.randomUUID().toString():editing.id;
        NewsEntity n=new NewsEntity(id,t,summary.getText().toString().trim(),body.getText().toString().trim(),
                null,"本地编辑","local",String.valueOf(topic.getSelectedItem()),null,"本地编辑",now,
                editing!=null&&editing.isBookmarked,editing==null?0:editing.savedAt,editing==null?0:editing.readAt,
                true,editing==null?now:editing.createdAt);
        vm.save(n); Toast.makeText(requireContext(),"已保存到本地新闻",Toast.LENGTH_SHORT).show();
        Navigation.findNavController(v).popBackStack();
    }
}
