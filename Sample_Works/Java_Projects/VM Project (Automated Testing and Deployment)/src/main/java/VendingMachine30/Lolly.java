package VendingMachine30;

public class Lolly extends AbstractItem implements Item {
    public Lolly (String name, double price, int quantity, String expiry, int id){
        super(name, price, quantity, expiry, id);
    }
    public String getType(){
        return "lolly";
    }
}
