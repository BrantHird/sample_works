package au.edu.sydney.cpa.erp.feaa.orders;

import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;
import au.edu.sydney.cpa.erp.ordering.ScheduledOrder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;


/**
 * Order for Scheduled Orders.
 *  Uses inbuilt methods and given OrderType to generate appropriate result.
 */

public class Scheduled implements ScheduledOrder {

    private int numQuarters;
    private OrderType orderType;

    public Scheduled(OrderType orderType, int numQuarters) {
        this.numQuarters = numQuarters;
        this.orderType = orderType;
    }

    @Override
    public double getRecurringCost() {
        return orderType.getTotalCommission();
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
        return orderType.getTotalCommission() * numQuarters;
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


    //

    @Override
    public String generateInvoiceData() {
        StringBuilder sb = new StringBuilder();

        sb.append("Thank you for your Crimson Permanent Assurance accounting order!\n");
        sb.append("The cost to provide these services: $");
        sb.append(String.format("%,.2f", getRecurringCost()));
        sb.append(" each quarter, with a total overall cost of: $");
        sb.append(String.format("%,.2f", getTotalCommission()));
        sb.append("\nPlease see below for details:\n");

        return orderType.generateInvoiceData(sb);
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

        Order copy = new Scheduled(orderType, numQuarters);

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

        StringBuilder sb = orderType.longDesc();

        return String.format(orderType.isFinalised() ? "" : "*NOT FINALISED*\n" +
                        "Order details (id #%d)\n" +
                        "Date: %s\n" +
                        "Number of quarters: %d\n" +
                        "Reports:\n" +
                        "%s" +
                        "Recurring cost: $%,.2f\n" +
                        "Total cost: $%,.2f\n",
                orderType.getOrderID(),
                orderType.getOrderData().format(DateTimeFormatter.ISO_LOCAL_DATE),
                numQuarters,
                sb.toString(),
                orderType.getTotalCommission(),
                this.getTotalCommission()
        );
    }


}
