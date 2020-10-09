package au.edu.sydney.cpa.erp.feaa.chainhandlers;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.PhoneCall;
import au.edu.sydney.cpa.erp.ordering.Client;


/**
 * Refer to ChainHandler Interface and CarrierPigeonHandler class for descriptions.
 */

public class PhoneCallHandler implements ChainHandler {
    ChainHandler nextChainHandler;

    @Override
    public void setNextChainHandler(ChainHandler nextChainHandler) {

        this.nextChainHandler = nextChainHandler;


    }

    @Override
    public boolean handle(AuthToken token, Client client, String data, String type) {
        String phone = client.getPhoneNumber();

        if (type.toUpperCase().equals("PHONE CALL")) {
            if(null != phone) {
                PhoneCall.sendInvoice(token, client.getFName(), client.getLName(), data, phone);
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
