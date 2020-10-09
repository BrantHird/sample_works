package au.edu.sydney.cpa.erp.auth;

import java.util.UUID;

public class AuthToken {
    private final UUID auth;

    public AuthToken() {
        this.auth = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AuthToken) {
            AuthToken compare = (AuthToken) o;
            return compare.getAuth().equals(this.getAuth());
        }

        return false;
    }

    public UUID getAuth() {
        return auth;
    }
}
