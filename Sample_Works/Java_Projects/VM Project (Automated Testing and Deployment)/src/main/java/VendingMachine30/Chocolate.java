package VendingMachine30;

public class Chocolate extends AbstractItem implements Item {

    public Chocolate(String name, double price, int quantity, String expiry, int id){
        super(name, price, quantity, expiry, id);
    }

    public String getType(){
        return "chocolate";
    }
}


