package au.edu.sydney.cpa.erp.feaa.chainhandlers;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.ordering.Client;

public interface ChainHandler {

    /**
     * Links current ChainHandler to subsequent ChainHandler
     * @param nextChainHandler
     */
    void setNextChainHandler(ChainHandler nextChainHandler);

    /**
     * Attempts to handle the current request given the data present and preferred type.
     * @param token
     * @param client
     * @param data
     * @param type
     * @return handled
     */
    boolean handle(AuthToken token, Client client, String data, String type);

}
