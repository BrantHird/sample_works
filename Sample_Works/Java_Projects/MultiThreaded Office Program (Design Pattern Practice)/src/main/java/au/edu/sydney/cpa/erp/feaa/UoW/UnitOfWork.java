package au.edu.sydney.cpa.erp.feaa.UoW;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.ordering.Order;

import java.util.List;


/**
 * This class type serves as a local layer for database alterations
 * Uses the Unit of Work Design Pattern to increase performance by saving changes locally
 * and committing when convenient.
 */

public interface UnitOfWork {

    public void addOrder(Order order);

    public void removeOrder(int order);

    public List<Order> getAllOrders();

    public Order getOrder(AuthToken token, int orderID);

    public void commitChanges(AuthToken token);

}
