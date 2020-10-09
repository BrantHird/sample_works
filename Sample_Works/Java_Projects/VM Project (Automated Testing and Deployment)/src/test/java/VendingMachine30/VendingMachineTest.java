import VendingMachine30.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import java.util.*;

import static org.junit.Assert.*;

public class VendingMachineTest {
    private static VendingMachine vm;
    private static ArrayList<String> itemNames;

    @BeforeClass
    public static void setupBeforeAll(){
        vm = new VendingMachine("password");
        vm.addItem("choc1", 3.40, 5, "chocolate", "01/01/2019");
        vm.addItem("choc2", 3.30, 7, "chocolate", "01/01/2019");
        vm.addItem("chips1", 5.00, 2, "chips", "01/01/2019");
        vm.addItem("drink1", 4, 5, "drink", "01/01/2019");
        vm.addItem("lolly1", 5, 6, "lolly", "01/01/2019");
        vm.addItem("drink2", 3.5, 10, "drink", "01/01/2019");

        itemNames = new ArrayList<String>();

        itemNames.add("choc1");
        itemNames.add("choc2");
        itemNames.add("chips1");
        itemNames.add("drink1");
        itemNames.add("lolly1");
        itemNames.add("drink2");
    }

    @After
    public void cleanup() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }


    @Test
    public void addItem() {
        VendingMachine vm = new VendingMachine("pass");
        assertTrue(vm.addItem("drink3", 3, 4, "drink", "01/01/2019"));
        assertTrue(vm.addItem("lolly3", 3, 4, "lolly", "01/01/2019"));
        assertFalse(vm.addItem("drink3", 3, 4, "invalid-type", "01/01/2019"));

        assertFalse(vm.addItem("drink3", -1, 4, "drink", "01/01/2019"));
        assertFalse(vm.addItem("drink3", 3, -1, "drink", "01/01/2019"));
    }

    @Test
    public void removeItem() {
        VendingMachine vm = new VendingMachine("password");
        vm.addItem("choc1", 3.40, 5, "chocolate", "01/01/2019");
        vm.addItem("choc2", 3.30, 7, "chocolate", "01/01/2019");
        assertEquals(2, vm.getNumberItems());
        assertTrue(vm.removeItem("choc1"));
        assertEquals(1, vm.getNumberItems());
        assertFalse(vm.removeItem("invalid-item"));
        assertEquals(1, vm.getNumberItems());
    }

    @Test
    public void getItemQuantity() {
        assertEquals(5, vm.getItemQuantity("choc1"));
        assertEquals(7, vm.getItemQuantity("choc2"));
        assertEquals(2, vm.getItemQuantity("chips1"));
        assertEquals(5, vm.getItemQuantity("drink1"));
        assertEquals(6, vm.getItemQuantity("lolly1"));
        assertEquals(10, vm.getItemQuantity("drink2"));
    }

    @Test
    public void getItemQuantityFail() {
        assertEquals(-1, vm.getItemQuantity("does not exist"));
    }

    @Test
    public void getAllItems() {
        ArrayList<Item> a = vm.getAllItems();
        assertEquals(a.size(), 6);

        for(int i = 0; i < a.size(); i++){
            assertEquals(itemNames.get(i), a.get(i).getName());
        }

    }

    @Test
    public void setItemQuantity() {
        assertEquals(5, vm.getItemQuantity("choc1"));

        assertTrue(vm.setItemQuantity("choc1", 6));
        assertEquals(6, vm.getItemQuantity("choc1"));

        assertFalse(vm.setItemQuantity("choc1", -1));
        assertFalse(vm.setItemQuantity("invalid-item", 0));
    }

    @Test
    public void getItemName() {
        assertEquals("choc1", vm.getItemName(1));
        assertEquals("drink1", vm.getItemName(4));

        assertNull(vm.getItemName(10));
        assertNull(vm.getItemName(-1));
    }

    @Test
    public void takeItem() {
        VendingMachine vm = new VendingMachine("password");
        vm.addItem("choc1", 3.40, 5, "chocolate", "01/01/2019");
        vm.addItem("choc2", 3.30, 7, "chocolate", "01/01/2019");

        assertFalse(vm.takeItem("choc2", -1));
        assertTrue(vm.takeItem("choc2", 2));
        assertEquals(5, vm.getItemQuantity("choc2"));
        assertFalse(vm.takeItem("choc2", 6));
        assertFalse(vm.takeItem("invalid-item", 1));
    }


    @Test
    public void getNumberItems(){
        VendingMachine vm = new VendingMachine("pass");

        assertEquals(0, vm.getNumberItems());
        vm.addItem("chips3", 5.00, 2, "chips", "01/01/2019");

        assertEquals(1, vm.getNumberItems());
        vm.removeItem("invalid item");
        assertEquals(1, vm.getNumberItems());
        vm.removeItem("chips3");
        assertEquals(0, vm.getNumberItems());
    }

    @Test
    public void testMaxCapacity()
    {
        VendingMachine vm = new VendingMachine("123");

        assertEquals(10, vm.getMaxItemCapacity());

        assertEquals(false, vm.addItem("timtam", 2.50, 11, "chocolate", "01/01/2019"));
        assertEquals(0, vm.getNumberItems());

        assertEquals(true, vm.addItem("timtam", 2.50, 10, "chocolate", "01/02/2019"));
        assertEquals(1, vm.getNumberItems());

        assertEquals("timtam",vm.getItemName(1));
        assertNull(vm.getItemName(2));
    }

    @Test
    public void testTakeTooMuch()
    {
        VendingMachine vm = new VendingMachine("123");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        vm.addItem("pringles", 5.00, 4, "chips", "01/04/2019");

        String expected = "There are only 4 available, Please select the exact amount or lower\n";
        assertEquals(false, vm.takeItem("pringles", 5));
        assertEquals(4,vm.getItemQuantity("pringles"));

    }


    @Test
    public void printMachineGreeting() {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        vm.printMachineGreeting();
        assertEquals("Please select an option to view\n", o.toString());
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    @Test
    public void updateItems() {

    }

    @Test
    public void printTooMuchErrorMessage() {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        vm.printTooMuchErrorMessage("choc1");
        assertEquals("There are only 5 available, Please select the exact amount or lower\n", o.toString());
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    @Test
    public void printNotEnoughMessage() {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        vm.printNotEnoughMessage();
        assertEquals("Please input a value of at least 1\n", o.toString());
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    @Test
    public void getItem() {
        Item i = vm.getItem(1);
        assertEquals(i.getID(), 1);
        assertEquals(i.getName(), "choc1");
        assertEquals(i.getType(), "chocolate");
        assertEquals(i.getExpiry(), "01/01/2019");
        assertEquals(i.getQuantity(), 5);
        assertEquals(i.getPrice(), 3.4, 0);
    }

    @Test
    public void getItemFail() {
        assertEquals(null, vm.getItem(100));
    }

    @Test
    public void testPassword(){
        assertTrue(vm.password("password"));
        assertFalse(vm.password("Password"));
        assertFalse(vm.password("wrong"));
    }

    @Test
    public void testChangePassword(){
        vm.changePassword("new");
        assertTrue(vm.password("new"));
        assertFalse(vm.password("password"));
        vm.changePassword("password");
    }

    @Test
    public void getMaxItemCapacity() {
        assertEquals(10, vm.getMaxItemCapacity());
    }

    @Test
    public void addTransactionTest() {
        VendingMachine machine = new VendingMachine("password");
        LinkedHashMap<Item, Integer> items = new LinkedHashMap<Item, Integer>();
        Date d = new Date();
        items.put(new Lolly("lolly1", 3.4, 4, "30/04/2020", 1), 2);
        machine.addTransaction(items, d, 6.8, 0);
        assertEquals(2, machine.getTransactionTracker());
    }

    @Test
    public void addCancelledTransactionTest() {
        VendingMachine machine = new VendingMachine("password");
        LinkedHashMap<Item, Integer> items = new LinkedHashMap<Item, Integer>();
        Date d = new Date();
        items.put(new Lolly("lolly1", 3.4, 4, "30/04/2020", 1), 2);
        machine.addCancelledTransaction(items, d, 6.8);
        assertEquals(2, machine.getTransactionTracker());
    }

    @Test
    public void setTransactionsTest() {
        LinkedHashMap<Item, Integer> items = new LinkedHashMap<Item, Integer>();
        items.put(new Lolly("lolly1", 3.4, 4, "30/04/2020", 1), 2);
        items.put(new Lolly("lolly2", 3.2, 4, "30/04/2020", 1), 1);
        Transaction t = new Transaction(items, false, new Date(), 10, 1, 1);
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(t);
        vm.setTransactions(transactions);
        assertEquals(transactions, vm.getTransactions());
    }

    @Test
    public void setCancelledTransactionsTest() {
        LinkedHashMap<Item, Integer> items = new LinkedHashMap<Item, Integer>();
        items.put(new Lolly("lolly1", 3.4, 4, "30/04/2020", 1), 2);
        items.put(new Lolly("lolly2", 3.2, 4, "30/04/2020", 1), 1);
        Transaction t = new Transaction(items, true, new Date(), 10, 0, 1);
        ArrayList<Transaction> cancelledTransactions = new ArrayList<Transaction>();
        cancelledTransactions.add(t);
        vm.setCancelledTransactions(cancelledTransactions);
        assertEquals(cancelledTransactions, vm.getCancelledTransactions());
    }
}