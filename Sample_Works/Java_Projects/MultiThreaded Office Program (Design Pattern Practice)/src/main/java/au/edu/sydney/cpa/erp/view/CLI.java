package au.edu.sydney.cpa.erp.view;

import au.edu.sydney.cpa.erp.ordering.Client;
import au.edu.sydney.cpa.erp.ordering.Report;
import au.edu.sydney.cpa.erp.feaa.FEAAFacade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("Duplicates")
public class CLI {
    private static final FEAAFacade model = new FEAAFacade();

    public static void main(String[] args) {
        authMenu();
    }

    private static void authMenu() {
        int selection;

        do {
            selection = ViewUtils.displayMenu("Login menu",
                    new String[]{
                            "Login",
                            "Quit",
                    },
                    "Please enter a selection");

            switch (selection) {
                case 1:
                    login();
                    selection = -1;
                    break;
                case 2:
                    return;
                default:
                    // Can't get here
            }
        } while (-1 == selection);
    }

    private static void login() {
        String userName;
        String password;
        boolean auth;

        do {
            userName = ViewUtils.getString("Please enter your username (enter blank to cancel): ", true);
            if ("".equals(userName)) {
                return;
            }
            password = ViewUtils.getString("Please enter your password: (enter blank to cancel)", true);
            if ("".equals(password)) {
                return;
            }

            auth = model.login(userName, password);

            if (!auth) {
                System.out.println("Error, credentials rejected");
            }

        } while (!auth);

        mainMenu();
        model.logout();
    }

    private static void mainMenu() {
        int selection;

        do {
            selection = ViewUtils.displayMenu("Main menu",
                    new String[]{
                            "Client Actions",
                            "Order Actions",
                            "Report Actions",
                            "Back",
                    },
                    "Please enter a selection");

            switch (selection) {
                case 1:
                    clientMenu();
                    selection = -1;
                    break;
                case 2:
                    orderMenu();
                    selection = -1;
                    break;
                case 3:
                    reportMenu();
                    selection = -1;
                    break;
                case 4:
                    return;
                default:
                    // Can't get here
            }
        } while (-1 == selection);
    }

    private static void clientMenu() {
        int selection;

        do {
            selection = ViewUtils.displayMenu("Client menu",
                    new String[]{
                            "List all",
                            "View",
                            "Back",
                    },
                    "Please enter a selection");

            switch (selection) {
                case 1:
                    listAllClients();
                    selection = -1;
                    break;
                case 2:
                    viewClientMenu();
                    selection = -1;
                    break;
                case 3:
                    return;
                default:
                    // Can't get here
            }
        } while (-1 == selection);
    }

    private static void listAllClients() {
        List<Integer> customers = model.getAllClientIDs();

        for (Integer integer: customers) {
            System.out.println("Client id: " + integer);
        }

    }

    private static void viewClientMenu() {
        Integer customerID = ViewUtils.getInt("Please enter client id or blank to cancel", true);

        if (null == customerID) {
            return;
        }

        Client client = model.getClient(customerID);

        if (null == client) {
            System.out.println("No matching client found");
            return;
        }

        System.out.println("Client details:");
        System.out.println("ID: " + customerID);
        System.out.println("First Name: " + client.getFName());
        System.out.println("Last Name: " + client.getLName());
        System.out.println("Phone: " + client.getPhoneNumber());
        System.out.println("Email: " + client.getEmailAddress());
        System.out.println("Internal Accounting: " + client.getInternalAccounting());
        System.out.println("Business Name: " + client.getBusinessName());
        System.out.println("Street Address: " + client.getAddress());
        System.out.println("Suburb: " + client.getSuburb());
        System.out.println("State: " + client.getState());
        System.out.println("Postcode: " + client.getPostCode());
        System.out.println("Pigeon Coop ID: " + client.getPigeonCoopID());
    }

    private static void orderMenu() {
        int selection;

        do {
            selection = ViewUtils.displayMenu("Order menu",
                    new String[]{
                            "List all",
                            "View",
                            "Edit",
                            "Remove",
                            "New",
                            "Back"
                    },
                    "Please enter a selection");

            switch (selection) {
                case 1:
                    listAllOrders();
                    selection = -1;
                    break;
                case 2:
                    viewOrderMenu();
                    selection = -1;
                    break;
                case 3:
                    editOrderMenu();
                    selection = -1;
                    break;
                case 4:
                    removeOrder();
                    selection = -1;
                    break;
                case 5:
                    newOrder();
                    selection = -1;
                    break;
                case 6:
                    return;
                default:
                    // Can't get here
            }
        } while (-1 == selection);
    }

    private static void editOrderMenu() {
        int orderID = -1;

        while (-1 == orderID) {
            Integer response = ViewUtils.getInt("Enter an order id to edit, or blank to return: ", true);
            if (null == response) {
                return;
            }
            if (response < 1) {
                System.out.println("Invalid id chosen");
                continue;
            }

            orderID = response;
        }

        editOrder(orderID);
    }

    private static void listAllOrders() {
        List<Integer> orders = model.getAllOrders();
        orders.sort(Comparator.comparing(Integer::intValue));

        System.out.println("Current Orders:\n");
        for (Integer orderID: orders) {
            System.out.println(String.format("%s: $%,.2f", orderID, model.getOrderTotalCommission(orderID)));
        }
    }

    private static void viewOrderMenu() {
        int orderID = -1;

        while (-1 == orderID) {
            Integer response = ViewUtils.getInt("Enter an order id to view, or blank to return: ", true);
            if (null == response) {
                return;
            }
            if (response < 1) {
                System.out.println("Invalid id chosen");
                continue;
            }

            orderID = response;
        }

        printOrder(orderID);
    }

    private static void removeOrder() {
        int orderID = -1;

        while (-1 == orderID) {
            Integer response = ViewUtils.getInt("Enter an order id to remove, or blank to return: ", true);
            if (null == response) {
                return;
            }
            if (response < 1) {
                System.out.println("Invalid id chosen");
                continue;
            }

            orderID = response;

            boolean result = model.removeOrder(orderID);

            if (!result) {
                System.out.println("No matching order found.");
                orderID = -1;
            }
        }

        System.out.println("Order removed");

    }

    private static void newOrder() {
        Integer clientID = -1;
        LocalDateTime date = LocalDateTime.now();
        Boolean isCritical;
        Integer orderType = -1;
        Integer criticalLoading = -1;
        Boolean isScheduled;
        Integer numQuarters = -1;
        Integer maxCountedEmployees = -1;

        while (-1 == clientID) {
            clientID = ViewUtils.getInt("Enter a client ID or blank to cancel: ", true);
            if (null == clientID) {
                return;
            }

            if (model.getClient(clientID) == null) {
                System.out.println("Customer not found.");
                clientID = -1;
            }
        }

        isCritical = ViewUtils.getBoolean("Is this a critical/priority order?: ", false);
        isScheduled = ViewUtils.getBoolean("Is this a scheduled (repeating each quarter) order?: ", false);
        if (null == isCritical || null == isScheduled) return; // just here to keep the linter quiet

        while (-1 == orderType) {
            orderType = ViewUtils.getInt("Is this order for regular accounting work (1) or for auditing (2) ?", false);
            if (null == orderType || orderType < 1 || orderType > 2) {
                System.out.println("Invalid order type");
                orderType = -1;
            }
        }

        if (isCritical) {
            while (-1 == criticalLoading) {
                criticalLoading = ViewUtils.getInt("Please enter the increased commission loading for this critical order in %", false);
                if (null == criticalLoading || criticalLoading < 1) {
                    System.out.println("Invalid critical loading");
                    criticalLoading = -1;
                }
            }
        }

        if (isScheduled) {
            while (-1 == numQuarters) {
                numQuarters = ViewUtils.getInt("Please enter the number of report batches to schedule", false);
                if (null == numQuarters || numQuarters < 0) {
                    System.out.println("Invalid number of batches");
                    numQuarters = -1;
                }
            }
        }


        if (orderType == 1) {
            while (-1 == maxCountedEmployees) {
                maxCountedEmployees = ViewUtils.getInt("What is the threshold for the maximum commission for reports in this order?", false);
                if (null == maxCountedEmployees || maxCountedEmployees < 1) {
                    System.out.println("Invalid maximum employees");
                    maxCountedEmployees = -1;
                }
            }
        }

        Integer orderID = model.createOrder(clientID, date, isCritical, isScheduled, orderType, criticalLoading, maxCountedEmployees, numQuarters);

        if (null == orderID) {
            System.out.println("Order creation failed.");
        } else {
            editOrder(orderID);
        }

    }

    private static void editOrder(int orderID) {
        String input = null;

        while (!"finalise".equals(input)) {
            input = ViewUtils.getString("Enter 'list' to list reports, 'view' to view current order contents, " +
                    "'set' to set report quantity, 'finalise' to finalise the order, 'cancel' to remove the order, or 'back' to leave the order to be finished later.", false);
            switch (input) {
                case "list":
                    listAllReports();
                    break;
                case "view":
                    printOrder(orderID);
                    break;
                case "set":
                    Integer reportID = ViewUtils.getInt("Please enter a report, or blank to cancel", true);
                    if (null == reportID) break;
                    List<Report> reports = model.getAllReports();
                    if (reportID < 1 || reportID > reports.size()) {
                        System.out.println("Invalid report");
                        break;
                    }
                    Integer reportEmployeeCount = ViewUtils.getInt("Please enter the number of employees covered by this report, 0 to remove, or blank to cancel", true);
                    if (null == reportEmployeeCount) break;
                    if (reportEmployeeCount < 0) {
                        System.out.println("Invalid employee count");
                        break;
                    }

                    model.orderLineSet(orderID, reports.get(reportID - 1), reportEmployeeCount);

                    break;
                case "finalise":
                    Boolean customPriority = ViewUtils.getBoolean("Would you like to use a custom invoicing method list?", false);
                    List<String> contactMethodList = null;
                    if (null != customPriority && customPriority) {
                        contactMethodList = getCustomPriorityList();
                    }
                    if (!model.finaliseOrder(orderID, contactMethodList)) {
                        System.out.println("No matching contact method found - no invoice sent.");
                    }
                    break;
                case "cancel":
                    model.removeOrder(orderID);
                    System.out.println("Order removed.");
                    return;
                case "back":
                    System.out.println("Order " + orderID + " will be left unfinished. Use this order id to return later.");
                    return;
                default:
                    System.out.println("Unknown command");
                    break;
            }
        }
    }

    private static List<String> getCustomPriorityList() {

        String response = null;
        List<String> result = new ArrayList<>();
        List<String> available = model.getKnownContactMethods();

        while (!"end".equals(response)) {
            response = ViewUtils.getString("Enter a contact method, 'end' to finish, blank to list available methods", true);
            if ("".equals(response)) {
                System.out.println("Known methods:");
                for (String method: available) {
                    System.out.println("\t" + method);
                }
            } else if (!"end".equals(response)) {
                boolean match = false;
                for (String method: available) {
                    if (method.toLowerCase().equals(response.toLowerCase())) {
                        match = true;
                        break;
                    }
                }
                if (match) {
                    result.add(response);
                } else {
                    System.out.println("Unknown method.");
                }
            }
        }

        if (result.size() == 0) {
            return null;
        } else {
            return result;
        }
    }

    private static void reportMenu() {
        int selection;

        do {
            selection = ViewUtils.displayMenu("Report menu",
                    new String[]{
                            "List all",
                            "View details",
                            "Back",
                    },
                    "Please enter a selection");

            switch (selection) {
                case 1:
                    listAllReports();
                    selection = -1;
                    break;
                case 2:
                    showReportDetails();
                    selection = -1;
                    break;
                case 4:
                    return;
                default:
                    // Can't get here
            }
        } while (-1 == selection);
    }

    private static void listAllReports() {
        List<Report> reports = model.getAllReports();

        System.out.println("All Report Types:\n");


        for (int i = 1; i <= reports.size(); i++) {
            System.out.println(i + ". " + reports.get(i - 1));
        }
    }

    private static void showReportDetails() {
        int reportID = -1;
        List<Report> reports = model.getAllReports();

        while (-1 == reportID) {
            Integer response = ViewUtils.getInt("Enter a report type to view, or blank to return: ", true);
            if (null == response) {
                return;
            }
            if (response < 1 || response > reports.size()) {
                System.out.println("Invalid report type chosen");
                continue;
            }
            reportID = response;

        }
        Report report = reports.get(reportID - 1);

        System.out.println(String.format("%s: $%,.2f", report.getReportName(), report.getCommission()));
    }

    private static void printOrder(int orderID) {
        System.out.println(model.getOrderLongDesc(orderID));
    }
}
