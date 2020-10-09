package au.edu.sydney.cpa.erp.contact;

import au.edu.sydney.cpa.erp.auth.AuthModule;
import au.edu.sydney.cpa.erp.auth.AuthToken;

public class Mail {
    public static void sendInvoice(AuthToken token, String clientFName, String clientLName, String data, String address, String suburb, String state, String postcode) {
        if (!AuthModule.authenticate(token)) {
            throw new SecurityException("Invalid authorisation");
        }
        System.out.println("Now posting to " + clientFName + " " + clientLName + " at " + address + ", " + suburb + " " + state + " " + postcode + "!");
        System.out.println(data);
    }
}
