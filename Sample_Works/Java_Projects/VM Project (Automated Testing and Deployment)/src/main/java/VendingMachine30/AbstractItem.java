package VendingMachine30;

public abstract class AbstractItem implements Item {

    public int itemID;
    private double price;
    private String name;
    private int quantity;
    private String expiry;

    public AbstractItem(String itemName, double itemCost, int itemAmount, String expiry, int itemID){
        this.price = itemCost;
        this.name = itemName;
        this.itemID = itemID;
        this.quantity = itemAmount;
        this.expiry = expiry;
    }

    public boolean setQuantity(int newQuantity){
        if(newQuantity < 0){
            return false;
        }
        this.quantity = newQuantity;
        return true;
    }

    public boolean setPrice(double newPrice){
        if(newPrice < 0){
            return false;
        }
        this.price = newPrice;
        return true;
    }
    public double getPrice(){
        return this.price ;
    }
    public String getName(){
        return this.name ;
    }
    public int getQuantity(){
        return this.quantity;
    }
    public int getID(){
        return this.itemID;
    }
    public String getExpiry() { return this.expiry; }

}
