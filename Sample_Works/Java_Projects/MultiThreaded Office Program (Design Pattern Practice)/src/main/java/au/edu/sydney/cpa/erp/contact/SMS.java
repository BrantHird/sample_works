package au.edu.sydney.cpa.erp.contact;

import au.edu.sydney.cpa.erp.auth.AuthModule;
import au.edu.sydney.cpa.erp.auth.AuthToken;

public class SMS {
    public static void sendInvoice(AuthToken token, String clientFName, String clientLName, String data, String phone) {
        if (!AuthModule.authenticate(token)) {
            throw new SecurityException("Invalid authorisation");
        }
        System.out.println("Now SMSing " + clientFName + " " + clientLName + " on " + phone + "!");
        System.out.println(data);
    }
}

