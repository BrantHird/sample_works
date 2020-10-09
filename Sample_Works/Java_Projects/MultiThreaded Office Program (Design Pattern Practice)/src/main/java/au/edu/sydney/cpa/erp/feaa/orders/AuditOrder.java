package au.edu.sydney.cpa.erp.feaa.orders;

import au.edu.sydney.cpa.erp.ordering.Report;

import java.time.LocalDateTime;
import java.util.*;

/**
 * OrderType for Audit Orders.
 * Employs methods previously shared by all Audit classes to mutate the given Order to suit Auditing.
 */

public class AuditOrder implements OrderType {

    private Map<Report, Integer> reports = new HashMap<>();
    private final int id;
    private LocalDateTime date;
    private int client;
    private boolean finalised = false;
    private int baseCommission;

    public AuditOrder(int id, int client, LocalDateTime date) {
        this.id = id;
        this.client = client;
        this.date = date;
        baseCommission = 0 ;
    }

    @Override
    public LocalDateTime getOrderData() {
        return date;
    }

    @Override
    public void setReport(Report report, int employeeCount) {
        if (finalised) throw new IllegalStateException("Order was already finalised.");

        // We can't rely on equal reports having the same object identity since they get
        // rebuilt over the network, so we have to check for presence and same values

        for (Report contained: reports.keySet()) {
                //Utilising value object comparison.
            if (contained.equals(report)) {
                report = contained;
                break;
            }
        }

        reports.put(report, employeeCount);
    }

    @Override
    public Set<Report> getAllReports() {
        return reports.keySet();
    }

    @Override
    public int getReportEmployeeCount(Report report) {
        // We can't rely on equal reports having the same object identity since they get
        // rebuilt over the network, so we have to check for presence and same values

        for (Report contained: reports.keySet()) {
            if (contained.equals(report)) {
                report = contained;
                break;
            }
        }
        Integer result = reports.get(report);
        return null == result ? 0 : result;
    }

    @Override
    public int getClient() {
        return client;
    }

    @Override
    public void finalise() {
        this.finalised = true;
    }


    @Override
    public String shortDesc() {
        return String.format("ID:%s $%,.2f", id, getTotalCommission());
    }

    @Override
    public StringBuilder longDesc() {
        StringBuilder reportSB = new StringBuilder();
        this.baseCommission = 0 ;

        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * reports.get(report);
            this.baseCommission += subtotal;

            reportSB.append(String.format("\tReport name: %s\tEmployee Count: %d\tCommission per employee: $%,.2f\tSubtotal: $%,.2f\n",
                    report.getReportName(),
                    reports.get(report),
                    report.getCommission(),
                    subtotal));
        }

        return reportSB;

    }


    @Override
    public String generateInvoiceData(StringBuilder sb) {

        List<Report> keyList = new ArrayList<>(reports.keySet());
        keyList.sort(Comparator.comparing(Report::getReportName).thenComparing(Report::getCommission));

        for (Report report : keyList) {
            double subtotal = report.getCommission() * reports.get(report);
            baseCommission += subtotal;

            sb.append("\tReport name: ");
            sb.append(report.getReportName());
            sb.append("\tEmployee Count: ");
            sb.append(reports.get(report));
            sb.append("\tCost per employee: ");
            sb.append(String.format("$%,.2f", report.getCommission()));
            sb.append("\tSubtotal: ");
            sb.append(String.format("$%,.2f\n", subtotal));
        }
        return sb.toString();
    }

    @Override
    public double getTotalCommission() {
        double cost = 0.0;
        for (Report report : reports.keySet()) {
            cost += reports.get(report) * report.getCommission();
        }
        return cost;
    }

    @Override
    public double getBaseTotal() {
        return baseCommission;
    }

    public Map<Report, Integer> getReports() {
        return reports;
    }

    @Override
    public int getOrderID() {
        return id;
    }

    public boolean isFinalised() {
        return finalised;
    }
}
