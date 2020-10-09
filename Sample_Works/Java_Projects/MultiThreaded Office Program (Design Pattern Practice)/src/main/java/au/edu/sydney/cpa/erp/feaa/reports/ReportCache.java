package au.edu.sydney.cpa.erp.feaa.reports;

import au.edu.sydney.cpa.erp.ordering.Report;
import com.google.common.primitives.ImmutableDoubleArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Class to serve as a memory Cache for Reports and Report data.
 * Used to mitigate performance issues and improve RAM usage by preventing duplicate Reports and duplicate Report data
 */

public class ReportCache {

    static List<Report> reports = new ArrayList<>();
    private  static List<String> names = new ArrayList<>();
    private  static List<ImmutableDoubleArray> legalData = new ArrayList<>();
    private  static  List<ImmutableDoubleArray> cashFlowData = new ArrayList<>();
    private static List<ImmutableDoubleArray>  mergesData = new ArrayList<>();
    private static List<ImmutableDoubleArray>  tallyingData = new ArrayList<>();
    private static List<ImmutableDoubleArray> deductionsData = new ArrayList<>();


    /**
     * Method to add reports to memory Cache
     * @param r (reports to add)
     */
    public static void setReports(Collection<Report> r){
        reports.addAll(r);
    }

    /**
     * Method to return Cached reports.
     * @return stored reports.
     */
    public static List<Report> getReports(){
        return reports;
    }


    /**
     * Method to add Report Name String to Cache
     * @param name
     */
    public static void addName(String name){
        names.add(name);
    }

    /**
     * Method to check for an existing name within the report Cache
     * Returns name if found. Returns Null if not found.
     * @param name
     * @return name
     */
    public static String getName(String name){
        for(String n : names){
            if(n.equals(name)) {
                return n;

            }
        }
        return null ;
    }

    /**
     * Method to add a Report's Legal Data to memory Cache
     * @param ld
     */
    public static void addLegalData(ImmutableDoubleArray ld){
        legalData.add(ld);
    }

    /**
     * Method to check if a duplicate of a given legal data already exists within the Memory Cache
     * Returns original copy if found.
     * Returns null if legal data hasnt been previously stored.
     * @param ld
     * @return
     */

    public static ImmutableDoubleArray getLegalData(ImmutableDoubleArray ld){
        for(ImmutableDoubleArray d : legalData){
            if(d.asList().equals(ld.asList())){
                return d  ;
            }
        }

        return null;
    }

    /**
     * Method to add a Report's Cashflow Data to memory Cache
     * @param cfd
     */

    public static void addCashFlowData(ImmutableDoubleArray cfd){
        cashFlowData.add(cfd);
    }

    /**
     * Method to check if a duplicate of a given cashflow data already exists within the Memory Cache
     * Returns original copy if found.
     * Returns null if cashflow data hasnt been previously stored.
     * @param cfd
     * @return
     */

    public static ImmutableDoubleArray getCashFlowData(ImmutableDoubleArray cfd){
        for(ImmutableDoubleArray d : cashFlowData){
            if(d.asList().equals(cfd.asList())){
                return d  ;
            }
        }
        return null;
    }


    /**
     * Method to add a Report's Merges Data to memory Cache
     * @param md
     */
    public static void addMergesData(ImmutableDoubleArray md){
        mergesData.add(md);
    }

    /**
     * Method to check if a duplicate of a given merges data already exists within the Memory Cache
     * Returns original copy if found.
     * Returns null if merges data hasnt been previously stored.
     * @param md
     * @return
     */


    public static ImmutableDoubleArray getMergersData(ImmutableDoubleArray md){
        for(ImmutableDoubleArray d : mergesData){
            if(d.asList().equals(md.asList())){
                return d  ;
            }
        }
        return null;
    }


    /**
     * Method to add a Report's Tallying Data to memory Cache
     * @param td
     */

    public static void addTallyingData(ImmutableDoubleArray td){
        tallyingData.add(td);
    }

    /**
     * Method to check if a duplicate of a given tallying data already exists within the Memory Cache
     * Returns original copy if found.
     * Returns null if tallying data hasnt been previously stored.
     * @param td
     * @return
     */

    public static ImmutableDoubleArray getTallyingData(ImmutableDoubleArray td){
        for(ImmutableDoubleArray d : tallyingData){
            if(d.asList().equals(td.asList())){
                return d  ;
            }
        }
        return null;
    }




    /**
     * Method to add a Report's Deductions Data to memory Cache
     * @param dd
     */
    public static void addDeductionsData(ImmutableDoubleArray dd){
        deductionsData.add(dd);
    }

    /**
     * Method to check if a duplicate of a given deductions data already exists within the Memory Cache
     * Returns original copy if found.
     * Returns null if deductions data hasnt been previously stored.
     * @param dd
     * @return
     */

    public static ImmutableDoubleArray getDeductionsData(ImmutableDoubleArray dd){
        for(ImmutableDoubleArray d : deductionsData){
            if(d.asList().equals(dd.asList())){
                return d  ;
            }
        }
        return null;
    }











}
