package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.feaa.threadpool.DatabaseCommunicator;
import au.edu.sydney.cpa.erp.ordering.Client;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ClientImpl implements Client {

    private final int id;
    private String fName;
    private String lName;
    private String phoneNumber;
    private String emailAddress;
    private String address;
    private String suburb;
    private String state;
    private String postCode;
    private String internalAccounting;
    private String businessName;
    private String pigeonCoopID;

    @SuppressWarnings("Duplicates")
    public ClientImpl(AuthToken token, int id) {

        //Setting variable that doesn't require database.
        this.id = id;

        //Utilise Database Communicator to retrieve appropriate fields from the database.
        List<Future<String>> answers = DatabaseCommunicator.getClientFields(token, id);

        try {
            //Setting appropriate fields
            this.fName = answers.get(0).get();
            this.lName = answers.get(1).get();
            this.phoneNumber = answers.get(2).get();
            this.emailAddress = answers.get(3).get();
            this.address = answers.get(4).get();
            this.suburb = answers.get(5).get();
            this.state = answers.get(6).get();
            this.postCode = answers.get(7).get();
            this.internalAccounting = answers.get(8).get();
            this.businessName = answers.get(9).get();
            this.pigeonCoopID = answers.get(10).get();

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }

    }

    public int getId() {
        return id;
    }

    @Override
    public String getFName() {
        return fName;
    }

    @Override
    public String getLName() {
        return lName;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getSuburb() {
        return suburb;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public String getPostCode() {
        return postCode;
    }

    @Override
    public String getInternalAccounting() {
        return internalAccounting;
    }

    @Override
    public String getBusinessName() {
        return businessName;
    }

    @Override
    public String getPigeonCoopID() {
        return pigeonCoopID;
    }
}

