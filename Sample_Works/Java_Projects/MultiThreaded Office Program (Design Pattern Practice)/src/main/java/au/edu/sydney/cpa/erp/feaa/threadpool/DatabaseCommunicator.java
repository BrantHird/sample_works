package au.edu.sydney.cpa.erp.feaa.threadpool;

import au.edu.sydney.cpa.erp.auth.AuthToken;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseCommunicator {


    public static List<Future<String>> getClientFields(AuthToken token, int id){

        //Setting Up ThreadPool
        ExecutorService pool = Executors.newFixedThreadPool(11);

        List<Callable<String>> todo = new ArrayList<>(11);

        //Setting up fieldNames
        List<String> fieldList = new ArrayList<>();
        fieldList.add("fName");
        fieldList.add("lName");
        fieldList.add("phoneNumber");
        fieldList.add("emailAddress");
        fieldList.add("address");
        fieldList.add("suburb");
        fieldList.add("state");
        fieldList.add("postCode");
        fieldList.add("internal accounting");
        fieldList.add("businessName");
        fieldList.add("pigeonCoopID");


        //Creating Threads and adding to callable list
        for(int i = 0 ; i < 11 ; i ++ ){
            Callable<String> cl = new ClientThread(token, id, fieldList.get(i));
            todo.add(cl);

        }

        //Setting Up Answers List
        List<Future<String>> answers = null;

        try {
            //Invoking Threads
            answers = pool.invokeAll(todo);

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        //Shutting down thread pool.
        pool.shutdown();

        return answers ;
    }
}
