package VendingMachine30;

public interface Item {

    double getPrice();
    String getName();
    int getQuantity();
    boolean setQuantity(int newQuantity) ;
    int getID();
    String getExpiry();

    String getType();
    boolean setPrice(double newPrice);
}
