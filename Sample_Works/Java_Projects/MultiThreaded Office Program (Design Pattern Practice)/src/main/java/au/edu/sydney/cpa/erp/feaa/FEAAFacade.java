package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthModule;
import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.database.TestDatabase;
import au.edu.sydney.cpa.erp.feaa.UoW.UnitOfWork;
import au.edu.sydney.cpa.erp.feaa.UoW.UnitOfWorkImpl;
import au.edu.sydney.cpa.erp.feaa.orders.*;
import au.edu.sydney.cpa.erp.feaa.reports.ReportCache;
import au.edu.sydney.cpa.erp.feaa.reports.ReportDatabase;
import au.edu.sydney.cpa.erp.ordering.Client;
import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("Duplicates")
public class FEAAFacade {
    private AuthToken token;
    private ContactHandlerInterface contactHandler;
    private UnitOfWork unitOfWorkImpl;


    public FEAAFacade(){

        //Set up ContactHandler and it's internal chain of responsibility.
        //Set unit of work field to be null.
        this.contactHandler = new ContactHandler();
        this.contactHandler.linkChain();
        this.unitOfWorkImpl = null ;
    }
    

    public boolean login(String userName, String password) {
        token = AuthModule.login(userName, password);
        return null != token;
    }

    public List<Integer> getAllOrders() {
        if (null == token) {
            throw new SecurityException();
        }

        if(unitOfWorkImpl == null){
            unitOfWorkImpl = new UnitOfWorkImpl(token);
        }

        //Utilise unit of work class to get all orders locally for better performance.
        List<Order> orders = unitOfWorkImpl.getAllOrders();

        List<Integer> result = new ArrayList<>();
        for (Order order : orders) {
            result.add(order.getOrderID());
        }
        return result;
    }

    public Integer createOrder(int clientID, LocalDateTime date, boolean isCritical, boolean isScheduled, int orderType, int criticalLoadingRaw, int maxCountedEmployees, int numQuarters) {
        if (null == token) {
            throw new SecurityException();
        }

        if(unitOfWorkImpl == null){
            unitOfWorkImpl = new UnitOfWorkImpl(token);
        }

        double criticalLoading = criticalLoadingRaw / 100.0;

        Order order;

        if (!TestDatabase.getInstance().getClientIDs(token).contains(clientID)) {
            throw new IllegalArgumentException("Invalid client ID");
        }

        int id = TestDatabase.getInstance().getNextOrderID();

        if (isScheduled) {
            if (1 == orderType) { // 1 is regular accounting
                OrderType type = new RegularAccountingOrder(id, clientID, date, maxCountedEmployees);
                if (isCritical) {
                    order = new ScheduledCritical(type,criticalLoading, numQuarters);
                } else {
                    order = new Scheduled(type, numQuarters);
                }
            } else if (2 == orderType) { // 2 is audit
                    OrderType type = new AuditOrder(id, clientID, date);
                    if (isCritical) {
                        order = new ScheduledCritical(type, criticalLoading, numQuarters);
                    } else {
                        order = new Scheduled(type, numQuarters);
                    }
            } else {return null;}
        } else {
            if (1 == orderType) { //RA
                OrderType type = new RegularAccountingOrder(id, clientID, date, maxCountedEmployees);
                if (isCritical) {
                    order = new Critical(type, criticalLoading);
                } else {
                    order = new Plain(type);
                }
            } else if (2 == orderType) { //Audit
                OrderType type = new AuditOrder(id,clientID,date);
                if (isCritical) {
                    order = new Critical(type, criticalLoading);
                } else {
                    order = new Plain(type);
                }
            } else {return null;}
        }


        //Add created order to unit of work class. (To be committed to database later).
        unitOfWorkImpl.addOrder(order);

        return order.getOrderID();
    }

    public List<Integer> getAllClientIDs() {
        if (null == token) {
            throw new SecurityException();
        }

        TestDatabase database = TestDatabase.getInstance();
        return database.getClientIDs(token);
    }

    public Client getClient(int id) {
        if (null == token) {
            throw new SecurityException();
        }

        return new ClientImpl(token, id);
    }

    public boolean removeOrder(int id) {
        if (null == token) {
            throw new SecurityException();
        }

        if(unitOfWorkImpl != null){
            unitOfWorkImpl.removeOrder(id);
        }

        //Remove order from both database (instantaneous) and unit of work.
        //This decision will be discussed in report.

        TestDatabase database = TestDatabase.getInstance();
        return database.removeOrder(token, id);
    }

    public List<Report> getAllReports() {

        if (null == token) {
            throw new SecurityException();
        }

        //If ReportCache is empty, retrieve and store reports from database
        if(ReportCache.getReports().isEmpty()){
            ReportCache.setReports(ReportDatabase.getTestReports());
        }

        //Use ReportCache to return reports locally (performant)
        return ReportCache.getReports();


    }


    public boolean finaliseOrder(int orderID, List<String> contactPriority) {
        if (null == token) {
            throw new SecurityException();
        }


        if(unitOfWorkImpl == null){
            unitOfWorkImpl = new UnitOfWorkImpl(token);
        }


        if (contactPriority == null || contactPriority.size() == 0) { // needs setting to default

            List<String> knownMethods = contactHandler.getKnownMethods();

            contactPriority = Arrays.asList(
                    knownMethods.get(3),
                    knownMethods.get(1),
                    knownMethods.get(0),
                    knownMethods.get(4),
                    knownMethods.get(5)
            );
        }

        //Retrieve order from local storage using unit of work class.
        Order order = unitOfWorkImpl.getOrder(token, orderID);

        order.finalise();

        //Update order in unit of work class.
        unitOfWorkImpl.addOrder(order);

        //Utilise contactHandler to send invoice.
        return contactHandler.sendInvoice(token, getClient(order.getClient()), contactPriority, order.generateInvoiceData());
    }

    public void logout() {

        if(unitOfWorkImpl != null) {
            //Utilise unit of work class to commit changes to database using thread pooling for performance
            unitOfWorkImpl.commitChanges(token);
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        AuthModule.logout(token);

        token = null;
    }

    public double getOrderTotalCommission(int orderID) {
        if (null == token) {
            throw new SecurityException();
        }

        if(unitOfWorkImpl == null){
            unitOfWorkImpl = new UnitOfWorkImpl(token);
        }

        //Utilise unit of work class to retrieve order locally.
        Order order = unitOfWorkImpl.getOrder(token, orderID);

        if (null == order) {
            return 0.0;
        }

        return order.getTotalCommission();
    }

    public void orderLineSet(int orderID, Report report, int numEmployees) {
        if (null == token) {
            throw new SecurityException();
        }

        if(unitOfWorkImpl == null){
            unitOfWorkImpl = new UnitOfWorkImpl(token);
        }

        //Utilise unit of work class to retrieve order locally.
        Order order = unitOfWorkImpl.getOrder(token, orderID);

        if (null == order) {
            System.out.println("got here");
            return;
        }

        order.setReport(report, numEmployees);

        //Update report in unit of work class.
        unitOfWorkImpl.addOrder(order);
    }

    public String getOrderLongDesc(int orderID) {
        if (null == token) {
            throw new SecurityException();
        }

        if(unitOfWorkImpl == null){
            unitOfWorkImpl = new UnitOfWorkImpl(token);
        }

        //Utilise unit of work class to retrieve order locally.
        Order order = unitOfWorkImpl.getOrder(token, orderID);

        if (null == order) {
            return null;
        }

        return order.longDesc();
    }

    public String getOrderShortDesc(int orderID) {
        if (null == token) {
            throw new SecurityException();
        }

        if(unitOfWorkImpl == null){
            unitOfWorkImpl = new UnitOfWorkImpl(token);
        }

        //Utilise unit of work class to retrieve order locally.
        Order order = unitOfWorkImpl.getOrder(token, orderID);

        if (null == order) {
            return null;
        }

        return order.shortDesc();
    }

    public List<String> getKnownContactMethods() {
        if (null == token) {
            throw new SecurityException();
        }
        return contactHandler.getKnownMethods();
    }
}
