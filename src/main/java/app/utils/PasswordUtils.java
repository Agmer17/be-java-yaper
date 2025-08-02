package app.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    public static String HashPw(String plainPw) {
        return BCrypt.hashpw(plainPw, BCrypt.gensalt());
    }

    public static boolean verifyPw(String hashedPw, String plainPw) {
        return BCrypt.checkpw(plainPw, hashedPw);
    }
}
