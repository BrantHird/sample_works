package au.edu.sydney.cpa.erp.feaa.chainhandlers;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.Mail;
import au.edu.sydney.cpa.erp.ordering.Client;

/**
 * Refer to ChainHandler Interface and CarrierPigeonHandler class for descriptions.
 */

public class MailHandler implements ChainHandler {
    ChainHandler nextChainHandler;

    @Override
    public void setNextChainHandler(ChainHandler nextChainHandler) {

        this.nextChainHandler = nextChainHandler;


    }
    @Override
    public boolean handle(AuthToken token, Client client, String data, String type) {

        String address = client.getAddress();
        String suburb = client.getSuburb();
        String state = client.getState();
        String postcode = client.getPostCode();
        if (type.toUpperCase().equals("MAIL")) {
            if(null != address && null != suburb &&
                    null != state && null != postcode) {
                Mail.sendInvoice(token, client.getFName(), client.getLName(), data, address, suburb, state, postcode);
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
