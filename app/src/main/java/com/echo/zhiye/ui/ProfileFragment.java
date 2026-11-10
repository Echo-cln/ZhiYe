package com.echo.zhiye.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.echo.zhiye.R;
import com.echo.zhiye.data.AppDatabase;
import com.echo.zhiye.data.SessionManager;
import com.echo.zhiye.data.UserEntity;
import com.google.android.material.button.MaterialButton;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s) { return i.inflate(R.layout.fragment_profile,c,false); }
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s) {
        SessionManager session=new SessionManager(requireContext());
        TextView title=v.findViewById(R.id.profile_name), email=v.findViewById(R.id.profile_email);
        MaterialButton auth=v.findViewById(R.id.profile_auth);
        if(session.isSignedIn()) {
            title.setText("知页读者"); email.setText(session.email()); auth.setText("退出登录");
            String userId=session.userId(); android.content.Context app=requireContext().getApplicationContext();
            Executors.newSingleThreadExecutor().execute(()->{
                UserEntity user=AppDatabase.get(app).userDao().findById(userId);
                if(user!=null) new Handler(Looper.getMainLooper()).post(()->{ if(isAdded()) title.setText(user.displayName); });
            });
            auth.setOnClickListener(x->{ session.signOut(); requireActivity().recreate(); });
        } else {
            title.setText("欢迎来到知页"); email.setText("登录后管理你的阅读记录"); auth.setText("登录 / 注册");
            auth.setOnClickListener(x->Navigation.findNavController(v).navigate(R.id.authFragment));
        }
        v.findViewById(R.id.profile_favorites).setOnClickListener(x->{ Bundle b=new Bundle(); b.putString("mode","bookmarks"); Navigation.findNavController(v).navigate(R.id.collectionFragment,b); });
        v.findViewById(R.id.profile_history).setOnClickListener(x->{ Bundle b=new Bundle(); b.putString("mode","history"); Navigation.findNavController(v).navigate(R.id.collectionFragment,b); });
        v.findViewById(R.id.profile_local).setOnClickListener(x->Navigation.findNavController(v).navigate(R.id.localNewsFragment));
        v.findViewById(R.id.profile_settings).setOnClickListener(x->Navigation.findNavController(v).navigate(R.id.settingsFragment));
    }
}
