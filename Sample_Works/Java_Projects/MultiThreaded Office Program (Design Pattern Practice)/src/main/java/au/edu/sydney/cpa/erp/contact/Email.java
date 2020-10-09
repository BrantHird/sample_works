package au.edu.sydney.cpa.erp.contact;

import au.edu.sydney.cpa.erp.auth.AuthModule;
import au.edu.sydney.cpa.erp.auth.AuthToken;

public class Email {
    public static void sendInvoice(AuthToken token, String clientFName, String clientLName, String data, String email) {
        if (!AuthModule.authenticate(token)) {
            throw new SecurityException("Invalid authorisation");
        }
        System.out.println("Now emailing " + clientFName + " " + clientLName + " at " + email + "!");
        System.out.println(data);
    }
}
