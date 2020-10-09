package au.edu.sydney.cpa.erp.feaa.UoW;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.database.TestDatabase;
import au.edu.sydney.cpa.erp.feaa.threadpool.OrderThread;
import au.edu.sydney.cpa.erp.ordering.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Implements UnitOfWork Database (see interface description).
 */

public class UnitOfWorkImpl implements UnitOfWork {


    //List of changes.
    List<Order> addedOrders  ;

    //List of existing orders.
    List<Order> availableOrders;


    /**
     * Unit of work must be passed Authentication token to allow for database
     * changes and retrievals
     * @param token
     */
    public UnitOfWorkImpl(AuthToken token){
        addedOrders = new ArrayList<>();
        availableOrders = TestDatabase.getInstance().getOrders(token);
    }


    /**
     * Adds order to UnitOfWork's list of orders and list of changes.
     * If Order already exists, removes the old version and inserts the
     * new version (which is assumed to be altered).
     * @param order
     */
    public void addOrder(Order order){

        Order toRemove = null ;
        for(Order o : availableOrders){
            if(o.getOrderID() == order.getOrderID()){
                toRemove = o ;
                break ;
            }
        }

        if(toRemove != null){
            availableOrders.remove(toRemove);
        }

        availableOrders.add(order);


        Order toR = null ;
        for(Order addedOrder : addedOrders){
            if(addedOrder.getOrderID() == order.getOrderID()){
                toR = addedOrder;
                break ;
            }
        }

        if(toR != null){
            addedOrders.remove(toRemove);
        }

        addedOrders.add(order);
    }

    public void removeOrder(int order){
        Order remove = null;
        for(Order o : availableOrders){
            if(o.getOrderID() == order) {
                remove = o;
                break ;
            }
        }

        if(remove != null){
            availableOrders.remove(remove);
        }

        Order rm = null;
        for(Order o : addedOrders){
            if(o.getOrderID() == order) {
                rm = o;
                break ;
            }
        }

        if(rm != null){
            addedOrders.remove(remove);
        }

    }


    /**
     * Returns all existing orders.
     * @return
     */

    public List<Order> getAllOrders(){
        return this.availableOrders;
    }

    /**
     * Checks for a specific order in current storage. If not present,
     * checks the database and adds to local storage if found.
     * @param token
     * @param orderID
     * @return
     */

    public Order getOrder(AuthToken token, int orderID){

        for(Order o : availableOrders){
            if(o.getOrderID() == orderID) {
                return o;
            }
        }

        Order toAdd = TestDatabase.getInstance().getOrder(token, orderID) ;

        if(toAdd == null){
            return null ;
        }

        else{
            availableOrders.add(toAdd);
            return toAdd ;
        }

    }

    /**
     * Commits all changes to the database using thread pooling.
     * @param token
     */
    public void commitChanges(AuthToken token){

        //Setting Up ThreadPool
        ExecutorService pool = Executors.newFixedThreadPool(addedOrders.size());

        //Creating Threads and Executing within threadPool
        for(int i = 0 ; i < addedOrders.size() ; i ++ ){
            Runnable orderThread = new OrderThread(token, addedOrders.get(i));
            pool.execute(orderThread);
        }

        pool.shutdown();

    }











}
