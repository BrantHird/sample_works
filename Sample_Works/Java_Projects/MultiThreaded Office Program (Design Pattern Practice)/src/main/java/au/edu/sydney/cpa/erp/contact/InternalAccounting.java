package au.edu.sydney.cpa.erp.contact;

import au.edu.sydney.cpa.erp.auth.AuthModule;
import au.edu.sydney.cpa.erp.auth.AuthToken;

public class InternalAccounting {
    public static void sendInvoice(AuthToken token, String clientFName, String clientLName, String data, String deptName, String companyName) {
        if (!AuthModule.authenticate(token)) {
            throw new SecurityException("Invalid authorisation");
        }
        System.out.println(deptName + " will pass on this invoice to " + clientFName + " " + clientLName + ", from " + companyName);
        System.out.println(data);
    }
}
