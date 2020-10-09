package au.edu.sydney.cpa.erp.feaa.reports;

import au.edu.sydney.cpa.erp.ordering.Report;

import java.util.*;


/*
Note from Tim:
Don't modify this file. It is not the real
database and won't actually fix anything.
 */

public class ReportDatabase {

    private static final Map<String, Report> reportTypes;
    private static final String[] reportTypeNameVersion = {
            "Payroll Tax Report 1",
            "Capital Gains Tax Report 1",
            "Overall Expenditure Report 1",
            "Dark Costs Report 1",
            "Petty Cash Report 1",
            "Enron Fund Report 1",
            "COVID-19 Report 1",
            "COVID-20 Report 1",
            "Brawndo Investment Report 1",
            "Philanthropy Report 1",
            "Payroll Tax Report 2",
            "Capital Gains Tax Report 2",
            "Overall Expenditure Report 2",
            "Dark Costs Report 2",
            "Petty Cash Report 2",
            "Enron Fund Report 2",
            "COVID-19 Report 2",
            "COVID-20 Report 2",
            "Brawndo Investment Report 2",
            "Philanthropy Report 2"
    };

    static {
        reportTypes = new HashMap<>();

        for (String name: reportTypeNameVersion) {
            reportTypes.put(name, new ReportImpl(name.substring(0, name.length()-1), getReportCommission(), getReportData(), getReportData(), getReportData(), getReportData(), getReportData()));
        }
    }

    /**
     * Note from Tim:
     * The version number is entirely arbitrary and not used anywhere else, so we can't use it as a composite key...
     * There are also more than 2 versions per name in the full system.
     *
     * The recreation/copying here is simulating a networked database connection
     */
    public static Collection<Report> getTestReports() {

        Collection<Report> originals = reportTypes.values();
        List<Report> result = new ArrayList<>();

        for (Report original: originals) {
            result.add(new ReportImpl(original.getReportName(),
                    original.getCommission(),
                    original.getLegalData().clone(),
                    original.getCashFlowData().clone(),
                    original.getMergesData().clone(),
                    original.getTallyingData().clone(),
                    original.getDeductionsData().clone()));
        }

        return result;
    }

    private static double[] getReportData() {

        /*
        Note from Tim:
        If your machine's memory can't handle the whole product data, you could set this lower while you work out
        a way to stop the RAM explosion? We do need to be able to get at the whole product though.
        The database doesn't though...
         */

        double[] result = new double[500000];
        Random random = new Random();

        for (int i = 0; i < result.length; i++) {
            result[i] = random.nextDouble();
        }

        return result;
    }

    private static double getReportCommission() {
        Random random = new Random();

        return 1.0 + 99.0 * random.nextDouble();
    }
}
