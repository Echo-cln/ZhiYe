package com.echo.zhiye.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.echo.zhiye.R;
import com.echo.zhiye.data.AuthRepository;
import com.echo.zhiye.data.SessionManager;
import com.echo.zhiye.data.UserEntity;

public class AuthFragment extends Fragment {
    private boolean registerMode=false;
    private EditText email,password,name;
    private TextView toggle,title;
    private AuthRepository repository;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s) { return i.inflate(R.layout.fragment_auth,c,false); }
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s) {
        repository=new AuthRepository(requireContext());
        email=v.findViewById(R.id.auth_email); password=v.findViewById(R.id.auth_password); name=v.findViewById(R.id.auth_name);
        toggle=v.findViewById(R.id.auth_toggle); title=v.findViewById(R.id.auth_title);
        updateMode();
        toggle.setOnClickListener(x->{ registerMode=!registerMode; updateMode(); });
        v.findViewById(R.id.auth_submit).setOnClickListener(x->submit(v));
    }
    private void updateMode() {
        title.setText(registerMode?"创建知页账户":"登录知页");
        name.setVisibility(registerMode?View.VISIBLE:View.GONE);
        toggle.setText(registerMode?"已有账户？直接登录":"还没有账户？立即注册");
    }
    private void submit(View v) {
        String e=email.getText().toString().trim(), p=password.getText().toString();
        if(!Patterns.EMAIL_ADDRESS.matcher(e).matches()) { email.setError("请输入有效邮箱"); return; }
        if(p.length()<6) { password.setError("密码至少 6 位"); return; }
        AuthRepository.Callback cb=new AuthRepository.Callback() {
            @Override public void success(UserEntity user) {
                if(!isAdded())return;
                new SessionManager(requireContext()).signIn(user);
                Toast.makeText(requireContext(),registerMode?"注册成功":"登录成功",Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).popBackStack();
            }
            @Override public void error(String message) { if(isAdded()) Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show(); }
        };
        if(registerMode) repository.register(e,name.getText().toString(),p,cb);
        else repository.login(e,p,cb);
    }
}
