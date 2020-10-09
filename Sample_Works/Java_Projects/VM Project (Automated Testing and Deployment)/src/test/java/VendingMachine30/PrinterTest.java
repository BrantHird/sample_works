import VendingMachine30.*;
import org.junit.AfterClass;
import org.junit.Test;
import java.util.ArrayList;
import java.io.*;


import org.junit.After;
import static org.junit.Assert.*;

public class PrinterTest {

    @After
    public void cleanup() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }


    @Test
    public void printTryAgain() {
        String password = "test";
        VendingMachine vm = new VendingMachine(password);
        Printer p = new Printer(vm);
        String expected =  "Would you like to: \n" +
                           "1. Try again\n" +
                "2. Return to main menu\n" ;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        p.printTryAgain();
        String output = outContent.toString();
        assertEquals(expected, output);
    }


    @Test
    public void printAllItemStock() {
        String password = "test";
        VendingMachine vm = new VendingMachine(password);

        Printer p = new Printer(vm);

        String expected =  "[ID] NAME QUANTITY\n" +
                "[1] " + "Coke: 1\n";

        boolean k = vm.addItem("Coke", 3, 1, "drink", "20/2/2019");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        p.printAllItemStock();
        String output = outContent.toString();
        assertEquals(expected, output);

    }


    @Test
    public void updateItems() {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        String password = "test";
        VendingMachine vm = new VendingMachine(password);
        ArrayList<Item> contents = new ArrayList<Item>();
        contents.add(new Chocolate("choc1", 3.40, 5, "01/01/2019", 1));
        contents.add(new Chocolate("choc2", 3.30, 7, "01/01/2019", 2));
        contents.add(new Chips("chips1", 5.00, 2, "01/01/2019", 3));
    }

    @Test
    public void printGreeting() {
        VendingMachine vm = new VendingMachine("password");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printGreeting();
        String expected = "Hello! Welcome to Agile Team 30's Vending Machine!\n";
        String actual = o.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void printSelectPrompt() {
        VendingMachine vm = new VendingMachine("password");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printSelectPrompt();
        String expected = "Please select an option to continue: \n" +
                "1.Staff Menu\n" +
                "2.Purchase\n" +
                "3.Exit\n";
        String actual = o.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void printStaffMenu() {
        VendingMachine vm = new VendingMachine("password");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printStaffMenu();



        String expected = "Please pick an option from the STAFF Menu\n" +
                "1. Alter Stock Quantities\n" +
                "2. Completely Fill Stock\n" +
                "3. Change Item Price\n" +
                "4. Generate Reports\n" +
                "5. Set Staff Password\n" +
                "6. Add Item\n" +
                "7. Remove Item\n" +
                "8. Exit Staff Menu\n";
        String actual = o.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void printCategoryOptions() {
        VendingMachine vm = new VendingMachine("password");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printCategoryOptions();
        String expected = "1. Drinks\n" +
                "2. Chips\n" +
                "3. Chocolates\n" +
                "4. Lollies\n" +
                "5. All Items\n";
        String actual = o.toString();
        assertEquals(expected, actual);
    }



    @Test
    public void testPrintAllItemOptions() {

        VendingMachine vm = new VendingMachine("password");
        vm.addItem("timtam", 3.50, 1, "chocolate", "02/02/2019");
        vm.addItem("coffee", 2.00, 1, "drink", "03/02/2019");
        vm.addItem("chitato", 4.00, 2, "chips", "04/02/2019");
        vm.addItem("candy", 1.00, 1, "lolly", "05/02/2019");

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printAllItemOptions();

        String expected = "[ID] NAME PRICE\n" +
                "[1] timtam: $3.50\n" +
                "[2] coffee: $2.00\n" +
                "[3] chitato: $4.00\n" +
                "[4] candy: $1.00\n";
        assertEquals(expected, o.toString());
    }

    @Test
    public void testPrintChocolatessStaff() {
        VendingMachine vm = new VendingMachine("password");
        vm.addItem("timtam", 3.50, 1, "chocolate", "02/02/2019");
        vm.addItem("coffee", 2.00, 1, "drink", "03/02/2019");
        vm.addItem("chitato", 4.00, 2, "chips", "04/02/2019");
        vm.addItem("candy", 1.00, 1, "lolly", "05/02/2019");

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printChocolates(true);

        String expected = "[ID] NAME PRICE\n" +
                "[1] timtam: $3.50\n" +
                "\tQUANTITY: 1 / 10\n" +
                "\tEXP: 02/02/2019\n";
        assertEquals(expected, o.toString());
    }

    @Test
    public void testPrintChocolatesUser() {
        VendingMachine vm = new VendingMachine("password");
        vm.addItem("timtam", 3.50, 1, "chocolate", "02/02/2019");
        vm.addItem("coffee", 2.00, 1, "drink", "03/02/2019");
        vm.addItem("chitato", 4.00, 2, "chips", "04/02/2019");
        vm.addItem("candy", 1.00, 1, "lolly", "05/02/2019");

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printChocolates(false);

        String expected = "[ID] NAME PRICE\n" +
                "[1] timtam: $3.50\n";
        assertEquals(expected, o.toString());
    }

    @Test
    public void testPrintDrinksStaff() {
        VendingMachine vm = new VendingMachine("password");
        vm.addItem("timtam", 3.50, 1, "chocolate", "02/02/2019");
        vm.addItem("coffee", 2.00, 1, "drink", "03/02/2019");
        vm.addItem("chitato", 4.00, 2, "chips", "04/02/2019");
        vm.addItem("candy", 1.00, 1, "lolly", "05/02/2019");

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printDrinks(true);

        String expected = "[ID] NAME PRICE\n" +
                "[2] coffee: $2.00\n" +
                "\tQUANTITY: 1 / 10\n" +
                "\tEXP: 03/02/2019\n";
        assertEquals(expected, o.toString());
    }

    @Test
    public void testPrintDrinksUser() {
        VendingMachine vm = new VendingMachine("password");
        vm.addItem("timtam", 3.50, 1, "chocolate", "02/02/2019");
        vm.addItem("coffee", 2.00, 1, "drink", "03/02/2019");
        vm.addItem("chitato", 4.00, 2, "chips", "04/02/2019");
        vm.addItem("candy", 1.00, 1, "lolly", "05/02/2019");

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printDrinks(false);

        String expected = "[ID] NAME PRICE\n" +
                "[2] coffee: $2.00\n";
        assertEquals(expected, o.toString());
    }

    @Test
    public void testPrintChipsStaff() {
        VendingMachine vm = new VendingMachine("password");
        vm.addItem("timtam", 3.50, 1, "chocolate", "02/02/2019");
        vm.addItem("coffee", 2.00, 1, "drink", "03/02/2019");
        vm.addItem("chitato", 4.00, 2, "chips", "04/02/2019");
        vm.addItem("candy", 1.00, 1, "lolly", "05/02/2019");

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printChips(true);

        String expected = "[ID] NAME PRICE\n" +
                "[3] chitato: $4.00\n" +
                "\tQUANTITY: 2 / 10\n" +
                "\tEXP: 04/02/2019\n";
        assertEquals(expected, o.toString());
    }

    @Test
    public void testPrintChipsUser()
    {
        VendingMachine vm = new VendingMachine("password");
        vm.addItem("timtam", 3.50, 1, "chocolate", "02/02/2019");
        vm.addItem("coffee", 2.00, 1, "drink", "03/02/2019");
        vm.addItem("chitato", 4.00, 2, "chips", "04/02/2019");
        vm.addItem("candy", 1.00, 1, "lolly", "05/02/2019");

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printChips(false);

        String expected = "[ID] NAME PRICE\n" +
                "[3] chitato: $4.00\n";
        assertEquals(expected, o.toString());
    }

    @Test
    public void testPrintLolliesStaff() {
        VendingMachine vm = new VendingMachine("password");
        vm.addItem("timtam", 3.50, 1, "chocolate", "02/02/2019");
        vm.addItem("coffee", 2.00, 1, "drink", "03/02/2019");
        vm.addItem("chitato", 4.00, 2, "chips", "04/02/2019");
        vm.addItem("candy", 1.00, 1, "lolly", "05/02/2019");

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printLollies(true);

        String expected = "[ID] NAME PRICE\n" +
                "[4] candy: $1.00\n" +
                "\tQUANTITY: 1 / 10\n" +
                "\tEXP: 05/02/2019\n";
        assertEquals(expected, o.toString());
    }

    @Test
    public void testPrintLolliesUser()
    {
        VendingMachine vm = new VendingMachine("password");
        vm.addItem("timtam", 3.50, 1, "chocolate", "02/02/2019");
        vm.addItem("coffee", 2.00, 1, "drink", "03/02/2019");
        vm.addItem("chitato", 4.00, 2, "chips", "04/02/2019");
        vm.addItem("candy", 1.00, 1, "lolly", "05/02/2019");

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printLollies(false);

        String expected = "[ID] NAME PRICE\n" +
                "[4] candy: $1.00\n";
        assertEquals(expected, o.toString());
    }

    @Test
    public void printPasswordMenu() {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        VendingMachine vm = new VendingMachine("password");
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printPasswordMenu();
        String expected = "Hello, please input the STAFF password\n";
        String actual = o.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void printIncorrectPasswordMessage() {
        VendingMachine vm = new VendingMachine("password");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printIncorrectPasswordMessage();
        String expected = "Sorry that was the wrong password\n";
        String actual = o.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void printCorrectPasswordMessage() {
        VendingMachine vm = new VendingMachine("password");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printCorrectPasswordMessage();
        String expected = "That was the correct password!\n";
        String actual = o.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void printInvalidInputMessage() {
        VendingMachine vm = new VendingMachine("password");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printInvalidInputMessage();
        String expected = "Invalid input. Please input a proper value\n";
        String actual = o.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testPrintContinuePurchaseInCart()
    {
        VendingMachine vm = new VendingMachine("password");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printContinuePurchaseInCart();



        String expected = "Would you like to:\n" +
                "1. Continue shopping\n" +
                "2. Finalise purchase\n" +
                "3. Cancel transaction\n";
        String actual = o.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testPrintContinuePurchase()
    {
        VendingMachine vm = new VendingMachine("password");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printContinuePurchase();

        String expected = "Would you like to:\n" +
                "1. Continue shopping\n" +
                "2. View cart\n" +
                "3. Finalise purchase\n" +
                "4. Cancel transaction\n";
        String actual = o.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testPrintReportOptions() {
        VendingMachine vm = new VendingMachine("password");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        System.setOut(new PrintStream(o));
        Printer p = new Printer(vm);
        p.printReportsOptions();
        String expected = "Which kind of report would you like to view:\n" +
                "1. Stock Report\n" +
                "2. Daily Transactions\n" +
                "3. Cancelled Transactions\n";
        String actual = o.toString();
        assertEquals(expected, actual);
    }

}