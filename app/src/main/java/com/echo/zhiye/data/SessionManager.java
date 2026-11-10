package com.echo.zhiye.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final SharedPreferences prefs;
    public SessionManager(Context c) {
        prefs=c.getApplicationContext().getSharedPreferences("zhiye_session",Context.MODE_PRIVATE);
    }
    public void signIn(UserEntity user) { prefs.edit().putString("user_id",user.id).putString("email",user.email).apply(); }
    public void signOut() { prefs.edit().clear().apply(); }
    public String userId() { return prefs.getString("user_id",null); }
    public String email() { return prefs.getString("email",null); }
    public boolean isSignedIn() { return userId()!=null; }
}
