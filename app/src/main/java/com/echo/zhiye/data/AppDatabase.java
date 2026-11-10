package com.echo.zhiye.data;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
@Database(entities={NewsEntity.class,UserEntity.class},version=2,exportSchema=false)
public abstract class AppDatabase extends RoomDatabase {
 private static volatile AppDatabase INSTANCE;
 public abstract NewsDao newsDao();
 public abstract UserDao userDao();
 static final Migration MIGRATION_1_2=new Migration(1,2) {
  @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
   db.execSQL("ALTER TABLE news_articles ADD COLUMN readProgress INTEGER NOT NULL DEFAULT 0");
  }
 };
 public static AppDatabase get(Context context) {
  if(INSTANCE==null) synchronized(AppDatabase.class) {
   if(INSTANCE==null) INSTANCE=Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"zhiye.db").addMigrations(MIGRATION_1_2).build();
  }
  return INSTANCE;
 }
}
