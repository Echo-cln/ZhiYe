package com.echo.zhiye.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE lower(email)=lower(:email) LIMIT 1")
    UserEntity findByEmail(String email);
    @Query("SELECT * FROM users WHERE id=:id LIMIT 1")
    UserEntity findById(String id);
    @Insert(onConflict=OnConflictStrategy.ABORT)
    void insert(UserEntity user);
    @Query("UPDATE users SET displayName=:name WHERE id=:id")
    void updateDisplayName(String id,String name);
    @Query("DELETE FROM users WHERE id=:id")
    void deleteById(String id);
}
