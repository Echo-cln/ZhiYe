package com.echo.zhiye.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.echo.zhiye.R;
import com.echo.zhiye.data.SessionManager;
import com.echo.zhiye.viewmodel.NewsViewModel;

public class SettingsFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s) { return i.inflate(R.layout.fragment_settings,c,false); }
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s) {
        NewsViewModel vm=new ViewModelProvider(this).get(NewsViewModel.class);
        v.findViewById(R.id.settings_clear_history).setOnClickListener(x->vm.clearHistory(()->Toast.makeText(requireContext(),"阅读历史已清空",Toast.LENGTH_SHORT).show()));
        v.findViewById(R.id.settings_signout).setOnClickListener(x->{ new SessionManager(requireContext()).signOut(); Toast.makeText(requireContext(),"已退出登录",Toast.LENGTH_SHORT).show(); });
        v.findViewById(R.id.settings_about).setOnClickListener(x->Toast.makeText(requireContext(),"知页 ZhiYe · Java + XML · Room",Toast.LENGTH_LONG).show());
    }
}
