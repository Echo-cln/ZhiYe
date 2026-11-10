package com.echo.zhiye.ui;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.echo.zhiye.R;
import com.echo.zhiye.data.NewsEntity;
import com.echo.zhiye.data.ReaderPreferences;
import com.echo.zhiye.viewmodel.NewsViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class ArticleDetailFragment extends Fragment {
 private final Handler handler=new Handler(Looper.getMainLooper());
 private Runnable pendingProgressSave;
 private int lastSavedProgress=-1;
 @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s){return i.inflate(R.layout.fragment_detail,c,false);}
 @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){
  String id=getArguments()==null?null:getArguments().getString("articleId");NewsViewModel vm=new ViewModelProvider(this).get(NewsViewModel.class);
  if(id==null){Toast.makeText(requireContext(),"未找到文章",Toast.LENGTH_SHORT).show();return;}
  v.findViewById(R.id.detail_back).setOnClickListener(x->Navigation.findNavController(v).popBackStack());
  vm.byId(id,n->{
   if(!isAdded()||n==null)return;vm.markRead(id);
   ((TextView)v.findViewById(R.id.detail_title)).setText(n.title);((TextView)v.findViewById(R.id.detail_summary)).setText(n.summary);
   ((TextView)v.findViewById(R.id.detail_source)).setText(n.sourceName+"  ·  "+n.topic);
   String date=n.publishedAt>0?new SimpleDateFormat("yyyy年MM月dd日 HH:mm",Locale.CHINA).format(new Date(n.publishedAt)):"发布时间未提供";
   ((TextView)v.findViewById(R.id.detail_date)).setText(date);
   TextView body=v.findViewById(R.id.detail_body),summary=v.findViewById(R.id.detail_summary),notice=v.findViewById(R.id.detail_notice);
   boolean external=!n.isLocal&&!"知页样例".equals(n.sourceName);body.setVisibility(external?View.GONE:View.VISIBLE);body.setText(n.body==null?n.summary:n.body);
   notice.setVisibility(external?View.VISIBLE:View.GONE);notice.setText("以上为媒体订阅源提供的摘要。知页不复制整篇新闻，阅读全文请前往原媒体页面。");
   ReaderPreferences prefs=new ReaderPreferences(requireContext());float size=prefs.fontSizeSp(),spacing=prefs.lineSpacingExtraDp()*getResources().getDisplayMetrics().density;
   summary.setTextSize(size);body.setTextSize(size);summary.setLineSpacing(spacing,1f);body.setLineSpacing(spacing,1f);
   ImageView image=v.findViewById(R.id.detail_image);
   if(n.imageUrl!=null&&!n.imageUrl.isEmpty()){image.setVisibility(View.VISIBLE);Glide.with(this).load(n.imageUrl).centerCrop().placeholder(R.drawable.bg_image_placeholder).error(R.drawable.bg_image_placeholder).into(image);}else image.setVisibility(View.GONE);
   TextView bookmark=v.findViewById(R.id.detail_bookmark);bookmark.setText(n.isBookmarked?"已收藏":"收藏");
   bookmark.setOnClickListener(x->{n.isBookmarked=!n.isBookmarked;vm.setBookmark(n.id,n.isBookmarked);bookmark.setText(n.isBookmarked?"已收藏":"收藏");Toast.makeText(requireContext(),n.isBookmarked?"已加入收藏":"已取消收藏",Toast.LENGTH_SHORT).show();});
   v.findViewById(R.id.detail_original).setOnClickListener(x->{if(n.url==null||n.url.isEmpty())Toast.makeText(requireContext(),"该样例没有原文链接",Toast.LENGTH_SHORT).show();else startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(n.url)));});
   v.findViewById(R.id.detail_share).setOnClickListener(x->shareArticle(n));
   ScrollView scroll=v.findViewById(R.id.detail_scroll);TextView progress=v.findViewById(R.id.detail_progress);
   progress.setText("阅读进度  ·  "+Math.max(0,n.readProgress)+"%");lastSavedProgress=n.readProgress;
   scroll.setOnScrollChangeListener((View view,int sx,int sy,int ox,int oy)->{
    int value=calculateProgress(scroll);progress.setText("阅读进度  ·  "+value+"%");
    if(value!=lastSavedProgress){if(pendingProgressSave!=null)handler.removeCallbacks(pendingProgressSave);pendingProgressSave=()->{if(isAdded())vm.updateReadProgress(id,value);lastSavedProgress=value;pendingProgressSave=null;};handler.postDelayed(pendingProgressSave,350);}
   });
   scroll.post(()->{
    if(n.readProgress>=100){progress.setText("阅读进度  ·  100%");lastSavedProgress=100;return;}
    int range=scroll.getChildCount()==0?0:scroll.getChildAt(0).getHeight()-scroll.getHeight();
    if(n.readProgress>0&&range>0)scroll.scrollTo(0,Math.round(range*n.readProgress/100f));
    int value=calculateProgress(scroll);progress.setText("阅读进度  ·  "+value+"%");lastSavedProgress=value;
    if(value!=n.readProgress)vm.updateReadProgress(id,value);
   });
  });
 }
 private int calculateProgress(ScrollView scroll){if(scroll.getChildCount()==0)return 0;int range=scroll.getChildAt(0).getHeight()-scroll.getHeight();if(range<=0)return 100;return Math.max(0,Math.min(100,Math.round(scroll.getScrollY()*100f/range)));}
 private void shareArticle(NewsEntity n){String text=n.title+(n.url==null||n.url.isEmpty()?"":"\n"+n.url);Intent send=new Intent(Intent.ACTION_SEND);send.setType("text/plain");send.putExtra(Intent.EXTRA_TEXT,text);startActivity(Intent.createChooser(send,"分享新闻"));}
 @Override public void onDestroyView(){if(pendingProgressSave!=null){handler.removeCallbacks(pendingProgressSave);pendingProgressSave.run();pendingProgressSave=null;}super.onDestroyView();}
}
