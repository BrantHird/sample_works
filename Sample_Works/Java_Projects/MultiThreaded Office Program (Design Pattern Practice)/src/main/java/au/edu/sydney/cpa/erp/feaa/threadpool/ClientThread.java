package au.edu.sydney.cpa.erp.feaa.threadpool;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.database.TestDatabase;

import java.util.concurrent.Callable;

/**
 * ClientThread is used to retrieve a single client field from the database.
 * Used as part of the ThreadPool implemented by DatabaseCommunicator to streamline the Client Creation process.
 */
public class ClientThread implements Callable<String> {

    AuthToken token ;
    int id;
    String fieldName;

    /**
     * Thread must be created and passed appropriate field to retrieve, client ID to retrieve from
     * and appropriate authorisation token.
     * @param token
     * @param id
     * @param fieldName
     */

    public ClientThread(AuthToken token, int id, String fieldName){
        this.token = token ;
        this.id = id ;
        this.fieldName = fieldName ;
    }

    /**
     * Finds appropriate client field from database.
     * @return
     */
    @Override
    public String call(){
        return TestDatabase.getInstance().getClientField(token, id, fieldName);
    }


}
