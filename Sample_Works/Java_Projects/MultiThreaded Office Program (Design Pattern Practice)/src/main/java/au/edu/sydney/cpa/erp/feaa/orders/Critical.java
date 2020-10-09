package au.edu.sydney.cpa.erp.feaa.orders;

import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Order for Critical Orders.
 *  Uses inbuilt methods and given OrderType to generate appropriate result.
 */


public class Critical implements Order {

    private double criticalLoading;
    private OrderType orderType ;


    public Critical(OrderType orderType, double criticalLoading) {
        this.orderType = orderType;
        this.criticalLoading = criticalLoading;
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
    public int getClient() {
        return orderType.getClient();
    }

    @Override
    public void finalise() {orderType.finalise();}

    protected double getCriticalLoading() {
        return this.criticalLoading;
    }

    @Override
    public Order copy() {
        Order copy = new Critical(orderType, criticalLoading);
        for (Report report : orderType.getReports().keySet()) {
            copy.setReport(report, orderType.getReports().get(report));
        }

        return copy;
    }

    @Override
    public String shortDesc() {
        return String.format("ID:%s $%,.2f", orderType.getOrderID(), getTotalCommission());
    }

    @Override
    public String longDesc() {
        double baseCommission = 0.0;
        double loadedCommission = getTotalCommission();
        StringBuilder reportSB = new StringBuilder();

        List<Report> keyList = new ArrayList<>(orderType.getReports().keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * orderType.getReports().get(report);
            baseCommission += subtotal;

            reportSB.append(String.format("\tReport name: %s\tEmployee Count: %d\tCommission per employee: $%,.2f\tSubtotal: $%,.2f\n",
                    report.getReportName(),
                    orderType.getReports().get(report),
                    report.getCommission(),
                    subtotal));
        }



        return String.format(orderType.isFinalised() ? "" : "*NOT FINALISED*\n" +
                        "Order details (id #%d)\n" +
                        "Date: %s\n" +
                        "Reports:\n" +
                        "%s" +
                        "Critical Loading: $%,.2f\n" +
                        "Total cost: $%,.2f\n",
                orderType.getOrderID(),
                orderType.getOrderData().format(DateTimeFormatter.ISO_LOCAL_DATE),
                reportSB.toString(),
                loadedCommission - baseCommission,
                loadedCommission
        );


    }

    @Override
    public String generateInvoiceData() {
        return String.format("Your priority business account has been charged: $%,.2f" +
                "\nPlease see your internal accounting department for itemised details.", getTotalCommission());
    }

    @Override
    public double getTotalCommission() {
        double cost = 0.0;
        for (Report report : orderType.getReports().keySet()) {
            cost += orderType.getReports().get(report) * report.getCommission();
        }
        cost += cost * criticalLoading;
        return cost;
    }


    @Override
    public int getOrderID() {
        return orderType.getOrderID();
    }
}
