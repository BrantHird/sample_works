package au.edu.sydney.cpa.erp.ordering;

import java.time.LocalDateTime;
import java.util.Set;

public interface Order {
    int getOrderID();
    double getTotalCommission();
    LocalDateTime getOrderDate();
    void setReport(Report report, int employeeCount);
    Set<Report> getAllReports();
    int getReportEmployeeCount(Report report);
    String generateInvoiceData();
    int getClient();
    void finalise();
    Order copy();
    String shortDesc();
    String longDesc();
}
