package com.echo.zhiye.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName="users",indices={@Index(value={"email"},unique=true)})
public class UserEntity {
    @PrimaryKey @NonNull public String id;
    @NonNull public String email;
    public String displayName,salt,passwordHash;
    public long createdAt;
    public UserEntity(@NonNull String id,@NonNull String email,String displayName,String salt,
                      String passwordHash,long createdAt) {
        this.id=id; this.email=email; this.displayName=displayName; this.salt=salt;
        this.passwordHash=passwordHash; this.createdAt=createdAt;
    }
}
