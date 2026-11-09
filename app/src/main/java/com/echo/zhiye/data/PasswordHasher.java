package com.echo.zhiye.data;

import android.util.Base64;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {
    private static final int ITERATIONS=120000;
    private PasswordHasher() {}
    public static String newSalt() {
        byte[] bytes=new byte[16]; new SecureRandom().nextBytes(bytes);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }
    public static String hash(String password, String salt) {
        PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),
                Base64.decode(salt, Base64.NO_WRAP), ITERATIONS, 256);
        try {
            return Base64.encodeToString(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded(), Base64.NO_WRAP);
        } catch(Exception e) { throw new IllegalStateException("密码加密失败", e); }
        finally { spec.clearPassword(); }
    }
    public static boolean verify(String password, String salt, String expected) {
        return hash(password, salt).equals(expected);
    }
}
