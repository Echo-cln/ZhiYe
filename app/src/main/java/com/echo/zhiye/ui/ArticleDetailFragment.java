package com.echo.zhiye.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.echo.zhiye.R;
import com.echo.zhiye.data.NewsEntity;
import com.echo.zhiye.viewmodel.NewsViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ArticleDetailFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s) {
        return i.inflate(R.layout.fragment_detail,c,false);
    }
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s) {
        String id=getArguments()==null?null:getArguments().getString("articleId");
        NewsViewModel vm=new ViewModelProvider(this).get(NewsViewModel.class);
        if(id==null) { Toast.makeText(requireContext(),"未找到文章",Toast.LENGTH_SHORT).show(); return; }
        vm.byId(id,n->{
            if(!isAdded()||n==null)return;
            ((TextView)v.findViewById(R.id.detail_title)).setText(n.title);
            ((TextView)v.findViewById(R.id.detail_summary)).setText(n.summary);
            ((TextView)v.findViewById(R.id.detail_source)).setText(n.sourceName+"  ·  "+n.topic);
            ((TextView)v.findViewById(R.id.detail_date)).setText(new SimpleDateFormat("yyyy年MM月dd日 HH:mm",Locale.CHINA).format(new Date(n.publishedAt)));
            TextView body=v.findViewById(R.id.detail_body), notice=v.findViewById(R.id.detail_notice);
            boolean external=!n.isLocal && !"知页样例".equals(n.sourceName);
            body.setVisibility(external?View.GONE:View.VISIBLE);
            body.setText(n.body==null?n.summary:n.body);
            notice.setVisibility(external?View.VISIBLE:View.GONE);
            notice.setText("以上为媒体订阅源提供的摘要。知页不复制整篇新闻，阅读全文请前往原媒体页面。");
            ImageView image=v.findViewById(R.id.detail_image);
            if(n.imageUrl!=null&&!n.imageUrl.isEmpty()) Glide.with(this).load(n.imageUrl).centerCrop().into(image);
            else image.setVisibility(View.GONE);
            TextView bookmark=v.findViewById(R.id.detail_bookmark);
            bookmark.setText(n.isBookmarked?"已收藏":"收藏");
            bookmark.setOnClickListener(x->{
                n.isBookmarked=!n.isBookmarked;
                vm.bookmark(n.id);
                bookmark.setText(n.isBookmarked?"已收藏":"收藏");
                Toast.makeText(requireContext(),n.isBookmarked?"已加入收藏":"已取消收藏",Toast.LENGTH_SHORT).show();
            });
            v.findViewById(R.id.detail_original).setOnClickListener(x->{
                if(n.url==null||n.url.isEmpty()) Toast.makeText(requireContext(),"该样例没有原文链接",Toast.LENGTH_SHORT).show();
                else startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(n.url)));
            });
        });
    }
}
