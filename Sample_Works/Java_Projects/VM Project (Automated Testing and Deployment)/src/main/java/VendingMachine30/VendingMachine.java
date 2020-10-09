package VendingMachine30;

import java.util.*;
import java.text.SimpleDateFormat;

public class VendingMachine {

    protected static final int MAX_ITEM_CAPACITY = 10 ;
    private ArrayList<Item> items;
    private String password;
    private int itemTracker;
    private ArrayList<Transaction> transactions;
    private ArrayList<Transaction> cancelledTransactions;
    private int transactionTracker;


    public VendingMachine(String password){
        this.password = password;
        items = new ArrayList<Item>();
        itemTracker = 1;
        transactionTracker = 1;
        transactions = new ArrayList<Transaction> ();
        cancelledTransactions = new ArrayList<Transaction> ();
    }

    public boolean password(String pw){
        return password.equals(pw);
    }

    public void changePassword(String pw){
        this.password = pw;
    }

    public void printMachineGreeting(){
        System.out.println("Please select an option to view");
    }

    public void updateItems(){
        Write write = new Write();
        write.writeItems(items);
    }


    public void printTooMuchErrorMessage(String name){
        int quantity = getItemQuantity(name);
        System.out.format("There are only %d available, Please select the exact amount or lower\n",quantity);
    }

    public void printNotEnoughMessage(){
        System.out.println("Please input a value of at least 1");
    }


    public boolean addItem(String item, double cost, int quantity, String type, String expiry){
        if(cost < 0 || quantity < 0 || quantity > MAX_ITEM_CAPACITY) {
            return false;
        }
        if(type.toLowerCase().equals("drink")) {
            Item newItem = new Beverage(item, cost, quantity, expiry, itemTracker);
            items.add(newItem);
            itemTracker++;
            return true;
        }

        else if(type.toLowerCase().equals("chocolate")) {
            Item newItem = new Chocolate(item, cost, quantity, expiry, itemTracker);
            items.add(newItem);
            itemTracker++;
            return true;
        }

        else if(type.toLowerCase().equals("lolly")) {
            Item newItem = new Lolly(item, cost, quantity, expiry, itemTracker);
            items.add(newItem);
            itemTracker++;
            return true;
        }

        else if(type.toLowerCase().equals("chips")) {
            Item newItem = new Chips(item, cost, quantity, expiry, itemTracker);
            items.add(newItem);
            itemTracker++;
            return true;
        }

        else {
            return false;
        }

    }

    public boolean removeItem(String item) {
        Item toBeRemoved = null;
        for (Item removeItem : items) {
            if (removeItem.getName().toLowerCase().equals(item.toLowerCase())) {
                toBeRemoved = removeItem;
                break;
            }
        }
        if(toBeRemoved != null){
            items.remove(toBeRemoved);
            return true;
        }else{
            return false;
        }
    }

    public int getItemQuantity(String item){

            for (Item quantityItem : items) {
                if (quantityItem.getName().toLowerCase().equals(item.toLowerCase())) {
                    return quantityItem.getQuantity();
                }
            }
            return - 1;
        }

    public ArrayList<Item> getAllItems(){
        return items ;
    }

    public boolean setItemQuantity(String item, int quantity){

        for (Item quantityItem : items) {
            if (quantityItem.getName().toLowerCase().equals(item.toLowerCase())) {
                return quantityItem.setQuantity(quantity);
            }
        }
        return false;

    }

    public String getItemName(int id){
        for(Item i: items){
            if(i.getID() == id){
                return i.getName();
            }
        }
        return null;
    }

    public boolean takeItem(String item, int quantityTaken){
        if(quantityTaken  < 0){
            return false;
        }
        for (Item quantityItem : items) {
            if (quantityItem.getName().toLowerCase().equals(item.toLowerCase())) {
                return quantityItem.setQuantity(quantityItem.getQuantity() - quantityTaken);
            }
        }
        return false;
    }

    public Item getItem(int ID){
        for(Item item : items){
            if(item.getID() == ID){
                return item ;
            }
        }
        return null ;
    }


    public int getNumberItems(){
        return items.size();
    }

    public int getMaxItemCapacity() { return this.MAX_ITEM_CAPACITY; }

    public void addTransaction(LinkedHashMap<Item, Integer> items, Date time, double total, double change) {;
        Transaction thisTransaction = new Transaction(items, false, time, total, change, getTransactionTracker());
        Write w = new Write();
        w.writeTransactions(thisTransaction);
        transactions.add(thisTransaction);
        this.transactionTracker++ ;
    }

    public void addCancelledTransaction(LinkedHashMap<Item, Integer> items, Date time, double total) {
        Transaction thisTransaction = new Transaction(items, true, time, total, 0, getTransactionTracker());
        Write w = new Write();
        w.writeTransactions(thisTransaction);
        cancelledTransactions.add(thisTransaction);
        this.transactionTracker++;
    }

    public int getTransactionTracker() { return this.transactionTracker; }

    public ArrayList<Transaction> getTransactions() {
        return this.transactions;
    }

    public ArrayList<Transaction> getCancelledTransactions() {
        return this.cancelledTransactions;
    }

    public void setTransactions(ArrayList<Transaction> t){
        this.transactions = t;
    }

    public void setCancelledTransactions(ArrayList<Transaction> t){
        this.cancelledTransactions = t;
    }

}

