package au.edu.sydney.cpa.erp.feaa.chainhandlers;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.SMS;
import au.edu.sydney.cpa.erp.ordering.Client;


/**
 * Refer to ChainHandler Interface and CarrierPigeonHandler class for descriptions.
 */

public class SMSHandler implements ChainHandler {
    ChainHandler nextChainHandler;

    @Override
    public void setNextChainHandler(ChainHandler nextChainHandler) {

        this.nextChainHandler = nextChainHandler;


    }

    @Override
    public boolean handle(AuthToken token, Client client, String data, String type) {
        String smsPhone = client.getPhoneNumber();
        if (type.equals("SMS")) {
            if(null != smsPhone) {
                SMS.sendInvoice(token, client.getFName(), client.getLName(), data, smsPhone);
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
