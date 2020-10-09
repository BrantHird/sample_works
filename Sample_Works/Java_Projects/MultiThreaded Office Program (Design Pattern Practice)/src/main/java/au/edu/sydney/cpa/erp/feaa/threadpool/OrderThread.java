package au.edu.sydney.cpa.erp.feaa.threadpool;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.database.TestDatabase;
import au.edu.sydney.cpa.erp.ordering.Order;


/**
 * Thread to add a given order to database. Utilised as part of
 * thread pool implemented by UnitOfWork.
 */
public class OrderThread implements Runnable {

    AuthToken token ;
    Order order;

    /**
     * Thread must be passed appropriate Authentication Token to access database
     * and appropriate order to add.
     * @param token
     * @param order
     */

    public OrderThread(AuthToken token, Order order){
        this.token = token ;
        this.order = order;
    }


    /**
     * Method to add order to database.
     */

    @Override
    public void run() {
        TestDatabase.getInstance().saveOrder(token, order);
    }


}
