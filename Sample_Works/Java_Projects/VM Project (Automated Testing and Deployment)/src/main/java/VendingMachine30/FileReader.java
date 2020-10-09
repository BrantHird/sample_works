package VendingMachine30;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileReader {

    public String password;
    public List<String> name = new ArrayList<>();
    public List<String> expiry = new ArrayList<>();
    public List<Double> price = new ArrayList<>();
    public List<Integer> quantity = new ArrayList<>();
    public List<String> Type = new ArrayList<>();
    public List<String> inputLine = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private ArrayList<Transaction> cancelledTransactions = new ArrayList<>();


    public FileReader() throws Exception {

        try {
            Scanner scanner = new Scanner(new File("src/main/Resources/password.txt"));
            this.password = scanner.nextLine();
            Scanner scanner1 = new Scanner(new File("src/main/Resources/input.txt"));
            int i = 0;
            inputLine = new ArrayList<>();
            while (scanner1.hasNextLine()) {
                String input = scanner1.nextLine();
                inputLine = Arrays.asList(input.split(","));

                name.add(inputLine.get(0));
                price.add(Double.parseDouble(inputLine.get(1)));
                quantity.add(Integer.parseInt(inputLine.get(2)));
                Type.add(inputLine.get(3));
                expiry.add(inputLine.get(4));

            }


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public List<String> getName(){
        return name; }

    public List<String> getType(){
        return Type; }

    public List<String> getExpiry(){
        return expiry; }

    public List<Double> getPrice(){
        return price; }

    public List<Integer> getQuantity(){
        return quantity; }

    public String getPassword(){
        return password;
    }

    public ArrayList<Transaction> getTransactions(){
        updateTransactions();
        return transactions;
    }

    public ArrayList<Transaction> getCancelledTransactions(){
        updateTransactions();
        return cancelledTransactions;
    }

    public void updateTransactions(){
        boolean nextTransaction = true;
        transactions = new ArrayList<>();
        cancelledTransactions = new ArrayList<>();

        try {

            Scanner scanner2 = new Scanner(new File("src/main/Resources/RecordedTransactions.txt"));
            Transaction t ;
            LinkedHashMap<Item, Integer> hm = new LinkedHashMap<>();
            inputLine = new ArrayList<>();


            while(scanner2.hasNextLine()){
                String input = scanner2.nextLine();


                if(nextTransaction){
                    nextTransaction = false ;
                    inputLine = Arrays.asList(input.split(","));
                    hm = new LinkedHashMap<>();
                    String name = inputLine.get(1);
                    double price = Double.parseDouble(inputLine.get(2));
                    String expiry = inputLine.get(5);
                    int amount = Integer.parseInt(inputLine.get(6));
                    Item newItem = new userItem(name, price, 1, expiry, 1);

                    hm.put(newItem, amount);
                }
                else {

                    inputLine = Arrays.asList(input.split(","));

                    if(inputLine.get(0).toLowerCase().equals("item")){
                        inputLine = Arrays.asList(input.split(","));
                        String name = inputLine.get(1);
                        double price = Double.parseDouble(inputLine.get(2));
                        String expiry = inputLine.get(5);
                        int amount = Integer.parseInt(inputLine.get(6));
                        Item newItem = new userItem(name, price, 1, expiry, 1);
                        if(hm.containsKey(newItem)){
                            hm.put(newItem, hm.get(newItem) + amount);
                        }else{
                            hm.put(newItem, amount);
                        }
                    }

                    else{

                        boolean isCancelled = Boolean.parseBoolean(inputLine.get(0));
                        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                        Date expiry = format.parse(inputLine.get(1));
                        double total = Double.parseDouble(inputLine.get(2));
                        double change = Double.parseDouble(inputLine.get(3));
                        int id = Integer.parseInt(inputLine.get(4));

                        t = new Transaction(hm, isCancelled, expiry, total, change, id);

                        if(isCancelled){
                            cancelledTransactions.add(t);
                        }

                        else{
                            transactions.add(t);
                        }

                        nextTransaction = true ;
                    }

                }
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



}
