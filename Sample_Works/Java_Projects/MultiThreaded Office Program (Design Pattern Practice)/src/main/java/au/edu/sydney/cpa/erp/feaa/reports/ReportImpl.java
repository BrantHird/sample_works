package au.edu.sydney.cpa.erp.feaa.reports;

import au.edu.sydney.cpa.erp.ordering.Report;
import com.google.common.primitives.ImmutableDoubleArray;

import java.util.Objects;


/**
 *  Employed a variant of the  ValueObject design pattern to check for equality between reports using
 *  a Report's data, making a Report immutable and a function of its stored variables.
 *
 *  Also used Report Cache here to prevent a Report from containing duplicate data already present in
 *  another Report.
 */
public class ReportImpl implements Report {

    private final String name;
    private final double commissionPerEmployee;
    private final ImmutableDoubleArray legalData;
    private final ImmutableDoubleArray cashFlowData;
    private final ImmutableDoubleArray mergesData;
    private final ImmutableDoubleArray tallyingData;
    private final ImmutableDoubleArray deductionsData;

    public ReportImpl(String name,
                      double commissionPerEmployee,
                      double[] legalData,
                      double[] cashFlowData,
                      double[] mergesData,
                      double[] tallyingData,
                      double[] deductionsData) {

        /**
         *
         * As an example of preventing duplication:
         * Here an original copy of name is attempted to be retrieved from the Cache
         * If the name is found and returned, this instance is used by the Report as
         * opposed to the instance passed to the parameter, thereby freeing up the
         * memory utilised by the "duplicate" which loses its reference.
         *
         *
         * If the name is not found, we add it to the Cache.
         */

        if(name != null) {

            String s = ReportCache.getName(name);

            if (s == null) {
                this.name = name;
                ReportCache.addName(name);
            } else {
                this.name = s;
            }
        }
        else{
            this.name = null;
        }

        this.commissionPerEmployee = commissionPerEmployee;


        /**
         * All paramters are null-checked and parameters are set to null if appropriate.
         */

        if(legalData != null) {
            ImmutableDoubleArray ld = ReportCache.getLegalData(ImmutableDoubleArray.copyOf(legalData));
            if (ld == null) {
                ImmutableDoubleArray ldata = ImmutableDoubleArray.copyOf(legalData);
                this.legalData = ldata;
                ReportCache.addLegalData(ldata);
            } else {
                this.legalData = ld;
            }
        }
        else{
            this.legalData = null ;
        }


        if(cashFlowData != null) {
            ImmutableDoubleArray cf = ReportCache.getCashFlowData(ImmutableDoubleArray.copyOf(cashFlowData));
            if (cf == null) {
                ImmutableDoubleArray cfd = ImmutableDoubleArray.copyOf(cashFlowData);
                this.cashFlowData = cfd;
                ReportCache.addCashFlowData(cfd);
            } else {
                this.cashFlowData = cf;
            }
        }
        else{
            this.cashFlowData = null ;
        }



        if(mergesData != null) {
            ImmutableDoubleArray md = ReportCache.getMergersData(ImmutableDoubleArray.copyOf(mergesData));

            if (md == null) {

                ImmutableDoubleArray mdata = ImmutableDoubleArray.copyOf(mergesData);
                this.mergesData = mdata;
                ReportCache.addMergesData(mdata);
            }
            else {
                this.mergesData = md;
            }
        }
        else{
            this.mergesData = null ;
        }



        if(tallyingData != null) {

            ImmutableDoubleArray td = ReportCache.getTallyingData(ImmutableDoubleArray.copyOf(tallyingData));

            if (td == null) {
                ImmutableDoubleArray tdata = ImmutableDoubleArray.copyOf(tallyingData);
                this.tallyingData = tdata;
                ReportCache.addTallyingData(tdata);
            } else {
                this.tallyingData = td;
            }
        }
        else{
            this.tallyingData = null ;
        }


        if(deductionsData != null) {

            ImmutableDoubleArray dd = ReportCache.getDeductionsData(ImmutableDoubleArray.copyOf(deductionsData));

            if (dd == null) {
                ImmutableDoubleArray ddata = ImmutableDoubleArray.copyOf(deductionsData);
                this.deductionsData = ddata;
                ReportCache.addDeductionsData(ddata);
            } else {

                this.deductionsData = dd;

            }
        }
        else{
            this.deductionsData = null ;
        }
    }

    @Override
    public String getReportName() {
        return name;
    }

    @Override
    public double getCommission() {
        return commissionPerEmployee;
    }

    @Override
    public double[] getLegalData() {

        if(legalData == null){
            return null ;
        }

        return legalData.toArray();

    }

    @Override
    public double[] getCashFlowData() {

        if(cashFlowData == null){
            return null ;
        }

        return  cashFlowData.toArray();

    }

    @Override
    public double[] getMergesData() {

        if(mergesData == null){
            return null;
        }

       return mergesData.toArray();
    }

    @Override
    public double[] getTallyingData() {

        if(tallyingData == null){
            return null ;
        }

        return  tallyingData.toArray();
    }

    @Override
    public double[] getDeductionsData() {

        if(deductionsData == null){
            return null ;
        }

        return deductionsData.toArray();
    }

    @Override
    public String toString() {

        return String.format("%s", name);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportImpl report = (ReportImpl) o;
        return Double.compare(report.commissionPerEmployee, commissionPerEmployee) == 0 &&
                Objects.equals(name, report.name) &&
                Objects.equals(legalData, report.legalData) &&
                Objects.equals(cashFlowData, report.cashFlowData) &&
                Objects.equals(mergesData, report.mergesData) &&
                Objects.equals(tallyingData, report.tallyingData) &&
                Objects.equals(deductionsData, report.deductionsData);
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, commissionPerEmployee, legalData, cashFlowData, mergesData, tallyingData, deductionsData);
    }
}
