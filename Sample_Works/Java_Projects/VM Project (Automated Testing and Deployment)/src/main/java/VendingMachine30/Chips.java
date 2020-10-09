package VendingMachine30;

public class Chips extends  AbstractItem implements Item{

    public Chips(String name, double price, int quantity, String expiry, int id){
        super(name, price, quantity, expiry, id);
    }

    public String getType(){
        return "chips";
    }

}
