package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.ordering.Client;

import java.util.List;


public interface ContactHandlerInterface {

    void linkChain();
    boolean sendInvoice(AuthToken token, Client client, List<String> priority, String data);
    List<String> getKnownMethods();

}
