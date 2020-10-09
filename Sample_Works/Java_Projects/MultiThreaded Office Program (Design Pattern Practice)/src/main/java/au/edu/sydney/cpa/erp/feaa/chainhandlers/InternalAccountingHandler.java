package au.edu.sydney.cpa.erp.feaa.chainhandlers;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.InternalAccounting;
import au.edu.sydney.cpa.erp.ordering.Client;

/**
 * Refer to ChainHandler Interface and CarrierPigeonHandler class for descriptions.
 */

public class InternalAccountingHandler implements ChainHandler {
    ChainHandler nextChainHandler;

    @Override
    public void setNextChainHandler(ChainHandler nextChainHandler) {

        this.nextChainHandler = nextChainHandler;


    }

    @Override
    public boolean handle(AuthToken token, Client client, String data, String type) {
        String internalAccounting = client.getInternalAccounting();
        String businessName = client.getBusinessName();
        if (type.toUpperCase().equals("INTERNAL ACCOUNTING")) {
            if(null != internalAccounting && null != businessName){
                InternalAccounting.sendInvoice(token, client.getFName(), client.getLName(), data, internalAccounting,businessName);
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
