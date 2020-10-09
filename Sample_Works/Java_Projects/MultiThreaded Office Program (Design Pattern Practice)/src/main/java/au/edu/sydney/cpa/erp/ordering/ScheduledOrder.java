package au.edu.sydney.cpa.erp.ordering;

public interface ScheduledOrder extends Order {
    double getRecurringCost();
    int getNumberOfQuarters();
}
