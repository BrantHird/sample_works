package au.edu.sydney.cpa.erp.feaa.orders;

import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Order for Plain Orders.
 *  Uses inbuilt methods and given OrderType to generate appropriate result.
 */



public class Plain implements Order {


    private OrderType orderType ;

    public Plain(OrderType orderType){
        this.orderType = orderType;
    }


    @Override
    public LocalDateTime getOrderDate() {
        return orderType.getOrderData();
    }

    @Override
    public void setReport(Report report, int employeeCount) {
        orderType.setReport(report, employeeCount);
    }

    @Override
    public Set<Report> getAllReports() {
        return orderType.getAllReports();
    }

    @Override
    public int getReportEmployeeCount(Report report) {
        // We can't rely on equal reports having the same object identity since they get
        // rebuilt over the network, so we have to check for presence and same values

        return orderType.getReportEmployeeCount(report);
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

        Order copyReport = new Plain(orderType);

        for(Report r : orderType.getReports().keySet()){
            copyReport.setReport(r, orderType.getReports().get(r));
        }

        return copyReport;

    }

    @Override
    public String shortDesc() {
        return orderType.shortDesc();
    }

    @Override
    public String longDesc() {
        StringBuilder reportSB = orderType.longDesc();

        return String.format(orderType.isFinalised() ? "" : "*NOT FINALISED*\n" +
                        "Order details (id #%d)\n" +
                        "Date: %s\n" +
                        "Reports:\n" +
                        "%s" +
                        "Total cost: $%,.2f\n",
                orderType.getOrderID(),
                orderType.getOrderData().format(DateTimeFormatter.ISO_LOCAL_DATE),
                reportSB.toString(),
                getTotalCommission()
        );
    }

    @Override
    public String generateInvoiceData() {
        StringBuilder sb = new StringBuilder();

        sb.append("Thank you for your Crimson Permanent Assurance accounting order!\n");
        sb.append("The cost to provide these services: $");
        sb.append(String.format("%,.2f", getTotalCommission()));
        sb.append("\nPlease see below for details:\n");

        return orderType.generateInvoiceData(sb);
    }

    @Override
    public double getTotalCommission() {
        return orderType.getTotalCommission();
    }


    @Override
    public int getOrderID() {
        return orderType.getOrderID();
    }



}
