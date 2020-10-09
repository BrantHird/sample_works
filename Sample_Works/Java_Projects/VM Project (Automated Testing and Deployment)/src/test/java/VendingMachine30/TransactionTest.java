import VendingMachine30.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Date;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

public class TransactionTest {
    private static Transaction t_success;
    private static Transaction t_fail;
    private static LinkedHashMap<Item, Integer> items_success;
    private static LinkedHashMap<Item, Integer> items_fail;
    private static Date d_success;
    private static Date d_fail;


    @BeforeClass
    public static void setup() {
        items_success = new LinkedHashMap<Item, Integer> ();
        items_success.put(new Chocolate("choc1", 3.4, 4, "31/01/2020", 1), 2);
        items_success.put(new Lolly("lolly1", 1.2, 5, "31/12/2019", 2), 1);
        d_success = new Date();
        t_success = new Transaction(items_success, false, d_success, 8, 0.5, 1);

        items_fail = new LinkedHashMap<Item, Integer> ();
        items_fail.put(new Chocolate("choc2", 2, 9, "31/10/2019", 3), 1);
        d_fail = new Date();
        t_fail = new Transaction(items_fail, true, d_fail, 2, 0, 2);
    }

    @Test
    public void withinTimeFrameSuccess() {
        Date d_success = new Date();
        assertTrue(t_success.withinTimeFrame(d_success));
    }

    @Test
    public void withinTimeFrameFail() {
        try {
            Date d_fail = new SimpleDateFormat("dd-MM-yyyy").parse("24-11-1998");
            assertFalse(t_success.withinTimeFrame(d_fail));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void printTransactionTest1() {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        String expected = "TRANSACTION [1]\n" +
                "====================\n" +
                "1. choc1 x 2 ($3.40 each)\n" +
                "2. lolly1 x 1 ($1.20 each)\n" +
                "Total was: $8.00\n" +
                "User paid $8.50\n" +
                "$0.50 change was returned to user\n" +
                "====================\n";
        t_success.printTransaction();
        String actual = o.toString();
        assertEquals(expected, actual);
        System.setOut(System.out);
    }

    @Test
    public void printTransactionTest2() {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        String expected = "TRANSACTION [2]\n" +
                "====================\n" +
                "1. choc2 x 1 ($2.00 each)\n" +
                "Total was: $2.00\n" +
                "====================\n";
        t_fail.printTransaction();
        String actual = o.toString();
        assertEquals(expected, actual);
        System.setOut(System.out);
    }

    @Test
    public void getItems() {
        assertEquals(t_success.getItems(), items_success);
        assertEquals(t_fail.getItems(), items_fail);
    }

    @Test
    public void isCancelled() {
        assertFalse(t_success.isCancelled());
        assertTrue(t_fail.isCancelled());
    }

    @Test
    public void getTime() {
        assertEquals(t_success.getTime(), d_success);
        assertEquals(t_fail.getTime(), d_fail);
    }

    @Test
    public void getTotal() {
        assertEquals(t_success.getTotal(), 8, 0);
        assertEquals(t_fail.getTotal(), 2, 0);
    }

    @Test public void getChange() {
        assertEquals(t_success.getChange(), 0.5, 0);
        assertEquals(t_fail.getChange(), 0, 0);
    }

    @Test public void getId() {
        assertEquals(t_success.getId(), 1);
        assertEquals(t_fail.getId(), 2);
    }
}