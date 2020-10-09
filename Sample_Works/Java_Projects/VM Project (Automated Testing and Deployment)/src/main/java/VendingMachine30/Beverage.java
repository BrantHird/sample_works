package VendingMachine30;

public class Beverage extends AbstractItem implements Item{

    public Beverage(String name, double price, int quantity, String expiry, int id){
        super(name, price, quantity, expiry, id);
    }

    public String getType(){
        return "drink";
    }
}
