package au.edu.sydney.cpa.erp.database;

import au.edu.sydney.cpa.erp.auth.AuthModule;
import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.ordering.Order;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class TestDatabase {
    private int nextOrderID = 1;

    private static final TestDatabase instance = new TestDatabase();
    /*
    Note from Tim:
    This is set up to do the same things in the same amount of time as the 'real' database.
    Because it's networked and relational DB based object identity can't be assumed
     */
    private final List<Order> orders = new ArrayList<>();
    private final List<Integer> clients = new ArrayList<>();

    private TestDatabase(){
        clients.add(0);
        clients.add(1);
        clients.add(2);
        clients.add(3);
    }

    public static TestDatabase getInstance() {
        return instance;
    }

    public void saveOrder(AuthToken token, Order order) {
        if (!AuthModule.authenticate(token)) {
            throw new SecurityException("Invalid authorisation");
        }

        for (Order iter : orders) {
            if (iter.getOrderID() == order.getOrderID()) {
                orders.remove(iter);
                break;
            }
        }


        simulateSlowDatabase(10, "Saving order");

        orders.add(order.copy());
    }

    public List<Integer> getClientIDs(AuthToken token) {
        if (!AuthModule.authenticate(token)) {
            throw new SecurityException("Invalid authorisation");
        }

        simulateSlowDatabase(2, "Getting clients");

        return new ArrayList<>(clients);
    }

    public String getClientField(AuthToken token, int id, String fieldName) {
        if (!AuthModule.authenticate(token)) {
            throw new SecurityException("Invalid authorisation");
        }

        simulateSlowDatabase(1, "Getting client field");

        switch (fieldName) {
            case "fName":
                switch (id) {
                    case 0:
                        return "Bob";
                    case 1:
                        return "Zhi";
                    case 2:
                        return "Dhanvi";
                    case 3:
                        return "Felix";
                    default:
                        return null;
                }
            case "lName":
                switch (id) {
                    case 0:
                        return "Smith";
                    case 1:
                        return "Liu";
                    case 2:
                        return "Kargan";
                    case 3:
                        return "The Cat";
                    default:
                        return null;
                }
            case "phoneNumber":
                switch (id) {
                    case 0:
                        return "12345";
                    case 2:
                        return "67890";
                    case 1:
                    case 3:
                    default:
                        return null;
                }
            case "emailAddress":
                switch (id) {
                    case 0:
                        return "bob@gmail.com";
                    case 1:
                        return "zhi@gmail.com";
                    case 3:
                        return "Felix@yahoo.com";
                    case 2:
                    default:
                        return null;
                }
            case "address":
                switch (id) {
                    case 0:
                        return "123 Fake St";
                    case 2:
                        return "17 Blue Rd";
                    case 1:
                    case 3:
                    default:
                        return null;
                }
            case "suburb":
                switch (id) {
                    case 0:
                        return "Darlington";
                    case 2:
                        return "Camden";
                    case 1:
                    case 3:
                    default:
                        return null;
                }
            case "state":
                switch (id) {
                    case 0:
                    case 2:
                        return "NSW";
                    case 1:
                    case 3:
                    default:
                        return null;
                }
            case "postCode":
                switch (id) {
                    case 0:
                        return "2008";
                    case 2:
                        return "2570";
                    case 1:
                    case 3:
                    default:
                        return null;
                }
            case "internal accounting":
                switch (id) {
                    case 1:
                    case 3:
                        return "Ben Affleck";
                    case 0:
                    case 2:
                    default:
                        return null;
                }
            case "businessName":
                switch (id) {
                    case 1:
                        return "Flowers by Zhi";
                    case 3:
                        return "Felix's Pet Food";
                    case 0:
                    case 2:
                    default:
                        return null;
                }
            case "pigeonCoopID":
                switch (id) {
                    case 0:
                        return "17";
                    case 3:
                        return "96";
                    case 1:
                    case 2:
                    default:
                        return null;
                }
            default:
                throw new IllegalArgumentException("Invalid client field " + fieldName);
        }
    }

    public Order getOrder(AuthToken token, int id) {
        if (!AuthModule.authenticate(token)) {
            throw new SecurityException("Invalid authorisation");
        }

        for (Order iter : orders) {
            if (iter.getOrderID() == id) {
                return iter.copy();
            }
        }

        return null;
    }

    public boolean removeOrder(AuthToken token, int id) {
        if (!AuthModule.authenticate(token)) {
            throw new SecurityException("Invalid authorisation");
        }

        for (Order iter : orders) {
            if (iter.getOrderID() == id) {
                orders.remove(iter);
                return true;
            }
        }

        return false;

    }

    public List<Order> getOrders(AuthToken token) {
        if (!AuthModule.authenticate(token)) {
            throw new SecurityException("Invalid authorisation");
        }

        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            result.add(order.copy());
        }
        return result;
    }

    public int getNextOrderID() {
        return nextOrderID++;
    }

    private void simulateSlowDatabase(int seconds, String message) {
        /*
        Note from Tim:
        No, the real database doesn't sleep. Yes, the real database takes 10 seconds to save a record etc. The DB
        guys asked management for new servers about 4 years ago. We just have to live with it for now - can you
        help us out in the FEAA code?
         */

        try {
            System.out.print(message);
            for (int i = 0; i < seconds; i++) {
                System.out.print(".");
                Thread.sleep(1000);
            }
            System.out.print("done!\n");
        } catch (InterruptedException ignored) {}

    }
}

