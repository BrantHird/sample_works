package au.edu.sydney.cpa.erp.ordering;

public interface Report {

    /*
     * Note from Tim: You would think you could use reportName as an id, but there's more than
     * 1700 cases of duplicate name in the database where the marketing guys decided to call
     * literally the same accounting work 10 different things (with different prices)
     */
    String getReportName();
    double getCommission();
    double[] getLegalData();
    double[] getCashFlowData();
    double[] getMergesData();
    double[] getTallyingData();
    double[] getDeductionsData();
}
