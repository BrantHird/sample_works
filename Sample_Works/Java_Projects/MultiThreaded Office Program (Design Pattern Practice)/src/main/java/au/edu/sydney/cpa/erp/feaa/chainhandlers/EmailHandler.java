package au.edu.sydney.cpa.erp.feaa.chainhandlers;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.Email;
import au.edu.sydney.cpa.erp.ordering.Client;

/**
 * Refer to ChainHandler Interface and CarrierPigeonHandler class for descriptions.
 */

public class EmailHandler implements ChainHandler {

    ChainHandler nextChainHandler;

    @Override
    public void setNextChainHandler(ChainHandler nextChainHandler) {

        this.nextChainHandler = nextChainHandler;


    }
    @Override
    public boolean handle(AuthToken token, Client client, String data, String type) {
        String email = client.getEmailAddress();
        if (type.toUpperCase().equals("EMAIL")) {

            if(null != email) {
                Email.sendInvoice(token, client.getFName(), client.getLName(), data, email);
                return true;
            }
            else{
                return false;
            }
        }

        else{
            if(nextChainHandler != null){
                return nextChainHandler.handle(token, client, data, type);
            }
            else{
                return false;
            }
        }

    }
}
