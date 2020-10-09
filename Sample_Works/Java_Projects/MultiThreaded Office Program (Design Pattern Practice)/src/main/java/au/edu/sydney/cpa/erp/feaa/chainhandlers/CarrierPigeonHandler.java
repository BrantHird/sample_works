package au.edu.sydney.cpa.erp.feaa.chainhandlers;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.contact.CarrierPigeon;
import au.edu.sydney.cpa.erp.ordering.Client;

public class CarrierPigeonHandler implements ChainHandler {


    public static ChainHandler nextChainHandler;


    /**
     * set nextChainHandler to be a subsequent ChainHandler (to be used in handle()).
     * @param nextChainHandler
     */
    @Override
    public void setNextChainHandler(ChainHandler nextChainHandler) {
        this.nextChainHandler = nextChainHandler;
    }

    /**
     * Attempt to handle the given invoice request.
     * If type does not match, pass to subsequent chain handler
     * If type matches and client data is invalid, pass to subsequent chain handler
     * If type matches and data is valid, send appropriate invoice and return true
     * If attempting to pass to subsequent handler and there is none (it is null),
     * return false and end Chain of Responsbility.
     *
     * @param token
     * @param client
     * @param data
     * @param type
     * @return
     */


    @Override
    public boolean handle(AuthToken token, Client client, String data, String type) {

        String pigeonCoopID = client.getPigeonCoopID();

        if (type.toUpperCase().equals("CARRIER PIGEON")) {

            if(null != pigeonCoopID){
                CarrierPigeon.sendInvoice(token, client.getFName(), client.getLName(), data, pigeonCoopID);
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
