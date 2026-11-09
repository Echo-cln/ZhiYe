package com.echo.zhiye.ui;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.echo.zhiye.R;
import com.echo.zhiye.data.ReaderPreferences;
import com.echo.zhiye.data.SessionManager;
import com.echo.zhiye.viewmodel.NewsViewModel;
public class SettingsFragment extends Fragment {
 @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s){return i.inflate(R.layout.fragment_settings,c,false);}
 @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){
  ReaderPreferences prefs=new ReaderPreferences(requireContext());SeekBar font=v.findViewById(R.id.settings_font_size);
  TextView label=v.findViewById(R.id.settings_font_label),preview=v.findViewById(R.id.settings_reader_preview);
  Spinner spacing=v.findViewById(R.id.settings_line_spacing);String[] labels={"紧凑","舒适","宽松"};int[] values={3,6,10};
  spacing.setAdapter(new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_dropdown_item,labels));
  int size=prefs.fontSizeSp();font.setMax(8);font.setProgress(size-14);int oldSpacing=prefs.lineSpacingExtraDp();
  int selected=oldSpacing<=4?0:oldSpacing<=8?1:2;spacing.setSelection(selected);applyPreview(preview,size,values[selected]);label.setText("正文大小 · "+size+"sp");
  font.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override public void onProgressChanged(SeekBar b,int p,boolean fromUser){int current=14+p;label.setText("正文大小 · "+current+"sp");int selectedSpacing=Math.max(0,Math.min(2,spacing.getSelectedItemPosition()));applyPreview(preview,current,values[selectedSpacing]);if(fromUser)prefs.setFontSizeSp(current);}
   @Override public void onStartTrackingTouch(SeekBar b){}
   @Override public void onStopTrackingTouch(SeekBar b){}
  });
  spacing.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){
   @Override public void onItemSelected(AdapterView<?> p,View x,int pos,long id){prefs.setLineSpacingExtraDp(values[pos]);applyPreview(preview,14+font.getProgress(),values[pos]);}
   @Override public void onNothingSelected(AdapterView<?> p){}
  });
  NewsViewModel vm=new ViewModelProvider(this).get(NewsViewModel.class);
  v.findViewById(R.id.settings_clear_history).setOnClickListener(x->vm.clearHistory(()->Toast.makeText(requireContext(),"阅读历史已清空",Toast.LENGTH_SHORT).show()));
  v.findViewById(R.id.settings_signout).setOnClickListener(x->{new SessionManager(requireContext()).signOut();Toast.makeText(requireContext(),"已退出登录",Toast.LENGTH_SHORT).show();requireActivity().recreate();});
  v.findViewById(R.id.settings_about).setOnClickListener(x->Toast.makeText(requireContext(),"知页 ZhiYe · Java + XML · MVVM · Room",Toast.LENGTH_LONG).show());
 }
 private void applyPreview(TextView view,int size,int spacingDp){view.setTextSize(size);view.setLineSpacing(spacingDp*getResources().getDisplayMetrics().density,1f);}
}
