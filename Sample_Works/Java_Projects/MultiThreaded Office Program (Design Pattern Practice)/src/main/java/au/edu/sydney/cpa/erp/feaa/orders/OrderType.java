package au.edu.sydney.cpa.erp.feaa.orders;

import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 *
 * Interface for OrderType under the Bridge Design Pattern.
 * Is passed to an Order upon instantiation.
 * This Type mutates an its encapsulating "order" based on its use e.g. Audit or Regular Accounting.
 *
 */

public interface OrderType {


    StringBuilder longDesc();

    String generateInvoiceData(StringBuilder sb);

    double getTotalCommission();

    double getBaseTotal();

    boolean isFinalised();

    Map<Report, Integer> getReports();

    String shortDesc();

    void finalise();

    int getClient();

    int getReportEmployeeCount(Report report);

    Set<Report> getAllReports();

    void setReport(Report report, int count);

    LocalDateTime getOrderData();

    int getOrderID();

}
