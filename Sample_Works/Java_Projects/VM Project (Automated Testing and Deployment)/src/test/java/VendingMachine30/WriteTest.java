import VendingMachine30.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

import static org.junit.Assert.*;

public class WriteTest {


    @Test
    public void writePassword() {
        Write w = new Write();
        w.writePassword("PASSWORD");
        File f = new File("src/main/Resources/password.txt");
        try {
            Scanner scanner = new Scanner(f);
            String password = "";
            if (scanner.hasNextLine()) {
                password = scanner.nextLine();
            }
            assertEquals("PASSWORD", password);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void writeItems() {
        Write w = new Write();
        ArrayList<Item> items = new ArrayList<Item> ();
        items.add(new Chips("Popcorn", 2.50, 3, "06/07/2023", 1));
        items.add(new Lolly("Lollipop", 1.50, 10, "31/01/2023", 2));
        w.writeItems(items);
        String expected = "Popcorn,2.5,3,chips,06/07/2023\n" +
                "Lollipop,1.5,10,lolly,31/01/2023\n";
        File f = new File("src/main/Resources/input.txt");
        try {
            Scanner scan = new Scanner(f);
            String actual = "";
            while (scan.hasNextLine()) actual = actual + scan.nextLine() + "\n";
            assertEquals(expected, actual);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void writeTransactions() {
        File f = new File("src/main/Resources/RecordedTransactions.txt");
        try {
            PrintWriter writer = new PrintWriter(f);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        LinkedHashMap<Item, Integer> items = new LinkedHashMap<Item, Integer>();
        items.put(new Chocolate("choc1", 3.4, 4, "31/01/2020", 1), 2);
        Date d = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        Transaction t = new Transaction(items, false, d, 8, 0.5, 1);
        Write w = new Write();
        w.writeTransactions(t);
        String expected = "Item,choc1,3.4,4,chocolate,31/01/2020,2\n" +
                "false," + formatter.format(d) + ",8.0,0.5,1\n";

        try {
            Scanner scan = new Scanner(f);
            String actual = "";
            while (scan.hasNextLine()) actual = actual + scan.nextLine() + "\n";
            assertEquals(expected,actual);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}