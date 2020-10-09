package VendingMachine30;
import java.util.*;
import java.text.SimpleDateFormat;

public class Transaction {
    private LinkedHashMap<Item, Integer> items;
    private boolean cancelled;
    private Date time;
    private double total;
    private double change;
    private int id;

    public Transaction(LinkedHashMap<Item, Integer> items, boolean cancelled, Date time, double total, double change, int id) {
        this.items = items;
        this.cancelled = cancelled;
        this.time = time;
        this.total = total;
        this.change = change;
        this.id = id;
    }

    public boolean withinTimeFrame(Date compared) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String comparedDate = formatter.format(compared);
        String tDate = formatter.format(this.time);

        if (tDate.equals(comparedDate)) {
            return true;
        } else {
            return false;
        }
    }

    public void printTransaction() {
        System.out.printf("TRANSACTION [%d]\n", this.id);
        System.out.println("====================");
        int i = 1;
        ArrayList<Item> itemList = new ArrayList<Item>(this.items.keySet());
        Collections.sort(itemList, new Comparator<Item>() {
            @Override
            public int compare(Item s1, Item s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });
        for (Item item : itemList){
            System.out.printf("%d. %s x %d ($%.2f each)\n", i, item.getName(),  items.get(item), item.getPrice());
            i++ ;
        }
        System.out.printf("Total was: $%.2f\n", this.total);
        if (!this.cancelled) {
            System.out.printf("User paid $%.2f\n", this.total + this.change);
            System.out.printf("$%.2f change was returned to user\n", this.change);
        }
        System.out.println("====================");
    }


    public HashMap<Item,Integer> getItems(){
        return this.items;
    }

    public boolean isCancelled(){
        return this.cancelled;
    }

    public Date getTime(){
        return this.time;
    }

    public double getTotal(){
        return this.total;
    }

    public double getChange(){
        return this.change;
    }

    public int getId(){
        return this.id;
    }

}
