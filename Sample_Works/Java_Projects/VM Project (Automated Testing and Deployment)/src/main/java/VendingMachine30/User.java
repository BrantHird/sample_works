package VendingMachine30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class User {
    private LinkedHashMap<Item, Integer> selectedItems;

    //To be used in app class to track user selections
    public User(){

        selectedItems = new LinkedHashMap<Item, Integer>();
    }

    //Calculate and return line total
    public double getLineTotal() {
        double lineTotal = 0;
        for(Item item : selectedItems.keySet()){
            lineTotal += item.getPrice() * selectedItems.get(item);
        }
        return lineTotal;
    }

    //Get all selected Items
    public LinkedHashMap<Item, Integer> getSelectedItems() {

        return selectedItems;
    }

    //Add item which also specifies quantity WITHIN the item class.
    public void addItem(Item newItem, int quantity){
        if(selectedItems.containsKey(newItem)){
            selectedItems.put(newItem, selectedItems.get(newItem) + quantity);
        }else{
            selectedItems.put(newItem, quantity);
        }
    }

    //Completely remove an item based on name identifier passed as String
    public void removeItem(String removeItem){
        for(Item item: selectedItems.keySet()){
            if(item.getName().equals(removeItem)){
                selectedItems.remove(item);
                break;
            }
        }
    }


    //To be used when a transaction is cancelled
    public void resetItems(){
        selectedItems = new LinkedHashMap<Item, Integer>();
    }

}
