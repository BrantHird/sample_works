package au.edu.sydney.cpa.erp.feaa.orders;

import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;
import au.edu.sydney.cpa.erp.ordering.ScheduledOrder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;


/**
 * Order for Scheduled Critical Orders.
 *  Uses inbuilt methods and given OrderType to generate appropriate result.
 */

public class ScheduledCritical implements ScheduledOrder {

    private int numQuarters;
    private OrderType orderType;
    private double criticalLoading;

    public ScheduledCritical(OrderType orderType, double criticalLoading, int numQuarters) {
        this.numQuarters = numQuarters;
        this.orderType = orderType ;
        this.criticalLoading = criticalLoading;
    }

    @Override
    public double getRecurringCost() {

        double cost = 0.0;
        for (Report report : orderType.getReports().keySet()) {
            cost += orderType.getReports().get(report) * report.getCommission();
        }
        cost += cost * criticalLoading;
        return cost;

    }

    @Override
    public int getNumberOfQuarters() {
        return numQuarters;
    }

    @Override
    public int getOrderID() {
        return orderType.getOrderID();
    }

    @Override
    public double getTotalCommission() {
        double cost = 0.0;
        for (Report report : orderType.getReports().keySet()) {
            cost += orderType.getReports().get(report) * report.getCommission();
        }
        cost += cost * criticalLoading;
        cost = cost * numQuarters;
        return cost;
    }

    @Override
    public LocalDateTime getOrderDate() {
        return orderType.getOrderData();
    }

    @Override
    public void setReport(Report report, int employeeCount) {

        orderType.setReport(report,employeeCount);

    }

    @Override
    public Set<Report> getAllReports() {
        return orderType.getAllReports();
    }

    @Override
    public int getReportEmployeeCount(Report report) {
        return orderType.getReportEmployeeCount(report);
    }

    @Override
    public String generateInvoiceData() {
        return String.format("Your priority business account will be charged: $%,.2f each quarter for %d quarters, with a total overall cost of: $%,.2f" +
                "\nPlease see your internal accounting department for itemised details.", getRecurringCost(), getNumberOfQuarters(), getTotalCommission());
    }

    @Override
    public int getClient() {
        return orderType.getClient();
    }

    @Override
    public void finalise() {

        orderType.finalise();

    }

    @Override
    public Order copy() {
        Map<Report, Integer> products = orderType.getReports();

        Order copy = new ScheduledCritical(orderType, criticalLoading, numQuarters);

        for (Report report : products.keySet()) {
            copy.setReport(report, products.get(report));
        }

        return copy;
    }

    @Override
    public String shortDesc() {
        return String.format("ID:%s $%,.2f per quarter, $%,.2f total", orderType.getOrderID(), getRecurringCost(), getTotalCommission());
    }

    @Override
    public String longDesc() {
        double loadedCostPerQuarter = getRecurringCost();
        double totalLoadedCost = this.getTotalCommission();
        StringBuilder reportSB = orderType.longDesc();

        double totalBaseCost = orderType.getBaseTotal();


        return String.format(orderType.isFinalised() ? "" : "*NOT FINALISED*\n" +
                        "Order details (id #%d)\n" +
                        "Date: %s\n" +
                        "Number of quarters: %d\n" +
                        "Reports:\n" +
                        "%s" +
                        "Critical Loading: $%,.2f\n" +
                        "Recurring cost: $%,.2f\n" +
                        "Total cost: $%,.2f\n",
                orderType.getOrderID(),
                orderType.getOrderData().format(DateTimeFormatter.ISO_LOCAL_DATE),
                numQuarters,
                reportSB.toString(),
                totalLoadedCost - (totalBaseCost * numQuarters),
                loadedCostPerQuarter,
                totalLoadedCost
        );
    }





}
