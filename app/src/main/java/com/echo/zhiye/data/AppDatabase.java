package com.echo.zhiye.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities={NewsEntity.class, UserEntity.class}, version=1, exportSchema=false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    public abstract NewsDao newsDao();
    public abstract UserDao userDao();
    public static AppDatabase get(Context context) {
        if (INSTANCE==null) synchronized(AppDatabase.class) {
            if (INSTANCE==null) INSTANCE= Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "zhiye.db").build();
        }
        return INSTANCE;
    }
}
