package VendingMachine30;

public class userItem extends AbstractItem implements Item {
    public userItem (String name, double price, int quantity, String expiry, int id){
        super(name, price, quantity, expiry, id);
    }

    public String getType(){
        return null;
    }
}
