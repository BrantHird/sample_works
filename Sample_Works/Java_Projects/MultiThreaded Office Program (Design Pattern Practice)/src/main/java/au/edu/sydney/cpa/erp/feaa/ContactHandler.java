package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.feaa.chainhandlers.*;
import au.edu.sydney.cpa.erp.ordering.Client;

import java.util.Arrays;
import java.util.List;

/**
 * Utilises the Chain of Responsibility Design Pattern to handle requests to send invoices
 * using a given priority list, Client and Data.
 */
public class ContactHandler implements ContactHandlerInterface {

    private ChainHandler chainHandler;


    /**
     * Sets up the chain of responsibility by creating and linking ChainHandlers together.
     */
    public void linkChain(){

        ChainHandler phoneHandler = new PhoneCallHandler();
        ChainHandler mailHandler = new MailHandler();
        ChainHandler internalAccountingHandler = new InternalAccountingHandler();
        ChainHandler smsHandler = new SMSHandler();
        ChainHandler emailHandler = new EmailHandler();
        chainHandler = new CarrierPigeonHandler();


        phoneHandler.setNextChainHandler(mailHandler);
        mailHandler.setNextChainHandler(internalAccountingHandler);
        internalAccountingHandler.setNextChainHandler(smsHandler);
        smsHandler.setNextChainHandler(emailHandler);
        chainHandler.setNextChainHandler(phoneHandler);

    }

    /**
     * Sends appropriate invoice utilising the Chain of Responsibility created.
     * Returns true if the request could be handled for the given client and contact method priorities.
     * Returns false if the request could not be handled.
     * @param token
     * @param client
     * @param priority
     * @param data
     *
     */
    public boolean sendInvoice(AuthToken token, Client client, List<String> priority, String data) {
        for (String method : priority) {

            boolean handled =  chainHandler.handle(token, client, data, method);

            if(handled){
                return true ;
            }

        }

        return false;
    }

    /**
     * Method to return known contact methods.
     * @return
     */

    public List<String> getKnownMethods() {
        return Arrays.asList(
                "Carrier Pigeon",
                "Email",
                "Mail",
                "Internal Accounting",
                "Phone call",
                "SMS"
        );
    }
}
