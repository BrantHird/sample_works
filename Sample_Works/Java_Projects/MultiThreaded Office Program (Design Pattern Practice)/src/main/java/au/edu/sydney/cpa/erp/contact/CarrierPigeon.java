package au.edu.sydney.cpa.erp.contact;

import au.edu.sydney.cpa.erp.auth.AuthModule;
import au.edu.sydney.cpa.erp.auth.AuthToken;

public class CarrierPigeon {
    public static void sendInvoice(AuthToken token, String clientFName, String clientLName, String data, String pigeonCoopID) {
        if (!AuthModule.authenticate(token)) {
            throw new SecurityException("Invalid authorisation");
        }
        System.out.println("Now sending carrier pigeon to " + clientFName + " " + clientLName + " from coop " + pigeonCoopID + "!");
        System.out.println(data);
    }
}
