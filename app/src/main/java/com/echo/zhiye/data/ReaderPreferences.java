package com.echo.zhiye.data;
import android.content.Context;
import android.content.SharedPreferences;
public class ReaderPreferences {
 private final SharedPreferences prefs;
 public ReaderPreferences(Context context) { prefs=context.getApplicationContext().getSharedPreferences("zhiye_reader_preferences",Context.MODE_PRIVATE); }
 public int fontSizeSp() { return prefs.getInt("font_size_sp",16); }
 public int lineSpacingExtraDp() { return prefs.getInt("line_spacing_extra_dp",6); }
 public void setFontSizeSp(int size) { prefs.edit().putInt("font_size_sp",Math.max(14,Math.min(22,size))).apply(); }
 public void setLineSpacingExtraDp(int dp) { prefs.edit().putInt("line_spacing_extra_dp",Math.max(2,Math.min(14,dp))).apply(); }
}
