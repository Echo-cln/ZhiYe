package com.echo.zhiye.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthRepository {
    public interface Callback { void success(UserEntity user); void error(String message); }
    private final UserDao dao;
    private final ExecutorService io=Executors.newSingleThreadExecutor();
    private final Handler main=new Handler(Looper.getMainLooper());
    public AuthRepository(Context c) { dao=AppDatabase.get(c).userDao(); }
    public void register(String email,String name,String password,Callback cb) {
        io.execute(()->{
            try {
                String e=email.trim().toLowerCase();
                if(dao.findByEmail(e)!=null) { fail(cb,"该邮箱已注册"); return; }
                String salt=PasswordHasher.newSalt();
                UserEntity u=new UserEntity(UUID.randomUUID().toString(),e,
                        name.trim().isEmpty()?"知页读者":name.trim(),salt,PasswordHasher.hash(password,salt),System.currentTimeMillis());
                dao.insert(u); main.post(()->cb.success(u));
            } catch(Exception ex) { fail(cb,"注册失败，请检查输入后重试"); }
        });
    }
    public void login(String email,String password,Callback cb) {
        io.execute(()->{
            try {
                UserEntity u=dao.findByEmail(email.trim().toLowerCase());
                if(u==null||!PasswordHasher.verify(password,u.salt,u.passwordHash)) { fail(cb,"邮箱或密码不正确"); return; }
                main.post(()->cb.success(u));
            } catch(Exception ex) { fail(cb,"登录失败，请稍后重试"); }
        });
    }
    private void fail(Callback cb,String message) { main.post(()->cb.error(message)); }
}
