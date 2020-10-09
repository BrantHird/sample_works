package au.edu.sydney.cpa.erp.auth;

import java.util.ArrayList;
import java.util.List;

public class AuthModule {
    public static List<AuthToken> tokens = new ArrayList<>();

    public static AuthToken login(String userName, String password) {
        if ("Terry Gilliam".equals(userName) && "hunter2".equals(password)) {
            AuthToken token = new AuthToken();
            tokens.add(token);
            return token;
        }

        return null;
    }

    public static boolean authenticate(AuthToken token) {
        for (AuthToken iter : tokens) {
            if (iter.equals(token)) {
                return true;
            }
        }

        return false;
    }

    public static void logout(AuthToken token) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals(token)) {
                tokens.remove(i);
                return;
            }
        }
    }

}
