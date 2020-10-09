import VendingMachine30.*;
import VendingMachine30.FileReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.Assert.*;

public class AppTest {

    private VendingMachine machine = new VendingMachine("password");
    private App app ;
    private Printer printer = new Printer(machine);


    private PrintStream sysOut;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        sysOut = System.out;
        System.setOut(new PrintStream(outContent));
        machine.addItem("Snickers",3.0,5,"chocolate","10/09/2019");
        machine.addItem("Smith Chips",5.0,10,"chips","11/09/2019");
        machine.addItem( "Coke",3.5,1,"drink","12/09/2019");
        machine.addItem( "Skittles",1.5,10,"lolly","13/09/2019");
    }

    @After
    public void revertStreams() {
        System.setOut(sysOut);
    }


    @Test
    public void testPassword() throws Exception {

        FileReader file = new FileReader();
        String pw = file.getPassword();

        assertEquals("PASSWORD",pw);

        App.setPassword(machine, "NEWPW");
        file = new FileReader();
        assertEquals("NEWPW",file.getPassword());

        App.setPassword(machine, "PASSWORD");

    }

    @Test
    public void fillMachine() {
        app.fillMachine(machine);

        assertEquals(outContent.toString(), "All items completely filled!\n");
    }

    @Test
    public void alterStock() {
        InputHandler input = new InputHandler(new Scanner("6\n1\n11\n-1\n9\n"));
        App.alterStock(machine, input, printer);

        String expected = "Current Stock Levels\n" + "[ID] NAME QUANTITY\n" + "[1] Snickers: 5\n" + "[2] Smith Chips: 10\n" +
                "[3] Coke: 1\n" + "[4] Skittles: 10\n" + "Please Select Item to Alter Stock\n" + "Invalid input. Please input a proper value\n" + "Please Input a Valid Item ID\n" + "Current Stock Levels\n" + "[ID] NAME QUANTITY\n" +
                "[1] Snickers: 5\n" + "[2] Smith Chips: 10\n" + "[3] Coke: 1\n" + "[4] Skittles: 10\n" + "Please Select Item to Alter Stock\n" +
                "Please Input Quantity of Stock\n" + "The maximum capacity of a product is 10. Please input a value of 10 or less.\n" + "Invalid input. Please input a proper value\n" +
                "You have set the quantity of [1] Snickers to 9.\n";
        assertEquals(expected, outContent.toString());
    }

    @Test
    public void changePrice() {
        InputHandler input = new InputHandler(new Scanner("5\n1\n-1\n2.50\n"));
        App.changePrice(machine, input, printer);

        String expected = "Current Item Prices\n" + "[ID] NAME PRICE\n" + "[1] Snickers: $3.00\n" + "[2] Smith Chips: $5.00\n" +
                "[3] Coke: $3.50\n" + "[4] Skittles: $1.50\n" + "Please Select Item to Change Price\n" + "Invalid input. Please input a proper value\n" +
                "Please Input a Valid Item ID\n" + "Current Item Prices\n" + "[ID] NAME PRICE\n" + "[1] Snickers: $3.00\n" +
                "[2] Smith Chips: $5.00\n" + "[3] Coke: $3.50\n" + "[4] Skittles: $1.50\n" + "Please Select Item to Change Price\n" + "Please Input Item Price\n" +
                "Invalid input. Please input a proper value\n" + "Please Input Item Price\n" + "You have set the price of [1] Snickers to 2.50.\n";
        assertEquals(expected, outContent.toString());
    }

    @Test()
    public void testExecuteApp1(){
//        System.setOut(sysOut);
        InputStream stdin = System.in;
        StringBufferInputStream s = new StringBufferInputStream("4\n1\nwrong\n2\n2\n0\n3\n");
        System.setIn(s);
        try{
            App.main(null);
        }catch(Exception e){
            throw new AssertionError("Unexpected exception in main method");
        }
        System.setIn(stdin);
        String expected = "Hello! Welcome to Agile Team 30's Vending Machine!\n" + "Please select an option to continue: \n" + "1.Staff Menu\n" +
                "2.Purchase\n" + "3.Exit\n" + "Invalid input. Please input a proper value\n" + "Please select an option to continue: \n" +
                "1.Staff Menu\n" + "2.Purchase\n" + "3.Exit\n" + "Please select an option to continue: \n" + "1.Staff Menu\n" +
                "2.Purchase\n" + "3.Exit\n" + "You have chosen the staff menu\n" + "Hello, please input the STAFF password\n" + "Sorry that was the wrong password\n" +
                "Would you like to: \n" + "1. Try again\n" + "2. Return to main menu\n" + "Please select an option to continue: \n" +
                "1.Staff Menu\n" + "2.Purchase\n" + "3.Exit\n" + "Welcome to the AgileTeam30 Vending Machine\n" + "Please select an option to view\n" +
                "1. Drinks\n" + "2. Chips\n" + "3. Chocolates\n" + "4. Lollies\n" + "5. All Items\n" + "Press 0 to return to main menu.\n" +
                "Please select an option to continue: \n" + "1.Staff Menu\n" + "2.Purchase\n" + "3.Exit\n" +
                "Thanks for visiting AgileTeam30's Vending Machine\n" + "See you Again!\n";

        assertEquals(expected, outContent.toString());
    }

    @Test()
    public void testStaffLogic1(){
        InputHandler input = new InputHandler(new Scanner("6\nchoc,4.50,4,chocolate,12/12/2019\n7\nchoc\n8\n"));
        App.staffLogic(printer, input, machine, null);

        String expected = "Please pick an option from the STAFF Menu\n" + "1. Alter Stock Quantities\n" + "2. Completely Fill Stock\n" +
                "3. Change Item Price\n" + "4. Generate Reports\n" + "5. Set Staff Password\n" + "6. Add Item\n" + "7. Remove Item\n" + "8. Exit Staff Menu\n"
                + "You have chosen to add an item\n" + "Please input the details of your new item in the following format:\n" + "name,price,quantity,type,expirydate\n" +
                "Where type is either : \n" + "chocolate\n" + "chips\n" + "drink\n" + "lolly\n" + "And where expiry date is in form DD/MM/YYYY\n" +
                "Successfully added item !\n" + "The current items in the vending machine are:\n" + "[ID] NAME PRICE\n" + "[1] Snickers: $3.00\n" +
                "[2] Smith Chips: $5.00\n" + "[3] Coke: $3.50\n" + "[4] Skittles: $1.50\n" + "[5] choc: $4.50\n" + "Please pick an option from the STAFF Menu\n" +
                "1. Alter Stock Quantities\n" + "2. Completely Fill Stock\n" + "3. Change Item Price\n" + "4. Generate Reports\n" +
                "5. Set Staff Password\n" + "6. Add Item\n" + "7. Remove Item\n" + "8. Exit Staff Menu\n" +
                "You have chosen to remove an item\n" + "Please input the name of the item you wish to remove\n" + "[ID] NAME PRICE\n" + "[1] Snickers: $3.00\n" + "[2] Smith Chips: $5.00\n" + "[3] Coke: $3.50\n" +
                "[4] Skittles: $1.50\n" + "[5] choc: $4.50\n" + "Item successfully removed!\n" + "Here are the items currently left in the vending machine\n" +
                "[ID] NAME PRICE\n" +  "[1] Snickers: $3.00\n" + "[2] Smith Chips: $5.00\n" + "[3] Coke: $3.50\n" + "[4] Skittles: $1.50\n"
                 + "Please pick an option from the STAFF Menu\n" + "1. Alter Stock Quantities\n" + "2. Completely Fill Stock\n" +
                "3. Change Item Price\n" + "4. Generate Reports\n" + "5. Set Staff Password\n" + "6. Add Item\n" + "7. Remove Item\n" +
                "8. Exit Staff Menu\n" + "You have exited Staff Menu\n";

        assertEquals(expected, outContent.toString());

    }

    @Test
    public void testUserLogic1(){
        InputHandler input = new InputHandler(new Scanner("5\n1\n2\n2\n2\n2\n1\n"));
        User user = new User();
//        System.setOut(sysOut);
        App.userLogic(printer, input, machine, user);

        String expected = "Please select an option to view\n" + "1. Drinks\n" + "2. Chips\n" + "3. Chocolates\n" + "4. Lollies\n" + "5. All Items\n" + "Press 0 to return to main menu.\n" +
                "Here are all the items Available\n" + "Which would you like to purchase?\n" + "[ID] NAME PRICE\n" + "[1] Snickers: $3.00\n" +
                "[2] Smith Chips: $5.00\n" + "[3] Coke: $3.50\n" + "[4] Skittles: $1.50\n" + "How many would you like?\n" + "You have added 2 Snickers\n" +
                "This costs $6.00 \n" + "Would you like to:\n" + "1. Continue shopping\n" + "2. View cart\n" + "3. Finalise purchase\n" + "4. Cancel transaction\n" +
                "=========== CURRENT VENDING SELECTIONS ===========\n" + "1. Snickers x 2 ($3.00 each)\n" + "====================================================\n" +
                "Your Line Total is: 6.00\n" + "Would you like to:\n" + "1. Continue shopping\n" + "2. Finalise purchase\n" + "3. Cancel transaction\n" +
                "The items in you cart are: \n" + "1. Snickers x 2 ($3.00 each)\n" + "Your Line Total is: 6.00\n" + "Please select which note/coin you want to input.\n" +
                "1. $20\n" + "2. $10\n" + "3. $5\n" + "4. $2\n" + "5. $1\n" + "6. 50c\n" + "7. 20c\n" + "8. 10c\n" +
                "Please input the amount of $10 note you want to insert.\n" + "Your change is as follows: \n" + "1. $20: 0.\n" + "2. $10: 0.\n" + "3. $5:  0.\n" + "4. $2:  2.\n" +
                "5. $1:  0.\n" + "6. 50c: 0.\n" + "7. 20c: 0.\n" + "8. 10c: 0.\n" + "$4.00 Total change given.\n" +
                "Thank you for your purchase!\n";

        assertEquals(expected, outContent.toString());

    }

    @Test()
    public void testUserLogic2(){
        InputHandler input = new InputHandler(new Scanner("1\n1\n3\n1\n1\n2\n1\n2\n1\n1\n3\n2\n1\n1\n1\n4\n1\n4\n1\n1\n0\n"));
        User user = new User();
        App.userLogic(printer, input, machine, user);

        String expected = "Please select an option to view\n" + "1. Drinks\n" + "2. Chips\n" + "3. Chocolates\n" + "4. Lollies\n" + "5. All Items\n" +
                "Press 0 to return to main menu.\n" + "Here are the Drinks Available\n" + "Which would you like to purchase?\n" + "[ID] NAME PRICE\n" +
                "[3] Coke: $3.50\n" + "Invalid input. Please input a proper value\n" + "Here are the Drinks Available\n" + "Which would you like to purchase?\n" +
                "[ID] NAME PRICE\n" + "[3] Coke: $3.50\n" + "How many would you like?\n" + "You have added 1 Coke\n" + "This costs $3.50 \n" +
                "Would you like to:\n" + "1. Continue shopping\n" + "2. View cart\n" + "3. Finalise purchase\n" + "4. Cancel transaction\n" +
                "Please select an option to view\n" + "1. Drinks\n" + "2. Chips\n" + "3. Chocolates\n" + "4. Lollies\n" + "5. All Items\n" +
                "Press 0 to return to main menu.\n" + "Here are the Chips Available\n" + "Which would you like to purchase?\n" + "[ID] NAME PRICE\n" +
                "[2] Smith Chips: $5.00\n" + "Invalid input. Please input a proper value\n" + "Here are the Chips Available\n" + "Which would you like to purchase?\n" +
                "[ID] NAME PRICE\n" + "[2] Smith Chips: $5.00\n" + "How many would you like?\n" + "You have added 1 Smith Chips\n" +
                "This costs $5.00 \n" + "Would you like to:\n" + "1. Continue shopping\n" + "2. View cart\n" + "3. Finalise purchase\n" + "4. Cancel transaction\n" +
                "Please select an option to view\n" + "1. Drinks\n" + "2. Chips\n" + "3. Chocolates\n" + "4. Lollies\n" + "5. All Items\n" +
                "Press 0 to return to main menu.\n" + "Here are the Chocolates Available\n" + "Which would you like to purchase?\n" + "[ID] NAME PRICE\n" +
                "[1] Snickers: $3.00\n" + "Invalid input. Please input a proper value\n" + "Here are the Chocolates Available\n" + "Which would you like to purchase?\n" +
                "[ID] NAME PRICE\n" + "[1] Snickers: $3.00\n" + "How many would you like?\n" + "You have added 1 Snickers\n" + "This costs $3.00 \n" +
                "Would you like to:\n" + "1. Continue shopping\n" + "2. View cart\n" + "3. Finalise purchase\n" + "4. Cancel transaction\n" +
                "Please select an option to view\n" + "1. Drinks\n" + "2. Chips\n" + "3. Chocolates\n" + "4. Lollies\n" + "5. All Items\n" +
                "Press 0 to return to main menu.\n" + "Here are the Lollies Available\n" + "Which would you like to purchase?\n" + "[ID] NAME PRICE\n" +
                "[4] Skittles: $1.50\n" + "Invalid input. Please input a proper value\n" + "Here are the Lollies Available\n" + "Which would you like to purchase?\n" +
                "[ID] NAME PRICE\n" + "[4] Skittles: $1.50\n" + "How many would you like?\n" + "You have added 1 Skittles\n" +
                "This costs $1.50 \n" + "Would you like to:\n" + "1. Continue shopping\n" + "2. View cart\n" + "3. Finalise purchase\n" + "4. Cancel transaction\n" +
                "Please select an option to view\n" + "1. Drinks\n" + "2. Chips\n" + "3. Chocolates\n" + "4. Lollies\n" + "5. All Items\n" +
                "Press 0 to return to main menu.\n";

        assertEquals(expected, outContent.toString());
    }

    @Test
    public void testSelectItem(){
        InputHandler input = new InputHandler(new Scanner("11\n0\n1\n5\n4\n"));
        User user = new User();
        int transactionNo = machine.getTransactionTracker();
        boolean result = App.selectItem(3, machine, input, printer, user);
        assertFalse(result);

        String expected = "How many would you like?\n" + "There are only 1 available, Please select the exact amount or lower\n" +
                "How many would you like?\n" + "Please input a value of at least 1\n" + "How many would you like?\n" + "You have added 1 Coke\n" +
                "This costs $3.50 \n" + "Would you like to:\n" + "1. Continue shopping\n" + "2. View cart\n" + "3. Finalise purchase\n" +
                "4. Cancel transaction\n" + "Invalid input. Please input a proper value\n" + "Would you like to:\n" + "1. Continue shopping\n" +
                "2. View cart\n" + "3. Finalise purchase\n" + "4. Cancel transaction\n";

        assertEquals(expected, outContent.toString());
        assertEquals(transactionNo + 1, machine.getTransactionTracker());
        assertEquals(1, machine.getCancelledTransactions().size());
        assertEquals(0, user.getSelectedItems().size());
    }

    @Test
    public void testPayment(){
        InputHandler input = new InputHandler(new Scanner("8\n1\n7\n1\n6\n1\n5\n1\n4\n1\n3\n1\n2\n1\n1\n2\n"));
        double change = App.paymentSystem(40, input);
        // allows for floating point error
        assertEquals(18.8, change, 0.01);
        String expected = "Your Line Total is: 40.00\n" + "Please select which note/coin you want to input.\n" + "1. $20\n" + "2. $10\n" +
                "3. $5\n" + "4. $2\n" + "5. $1\n" + "6. 50c\n" + "7. 20c\n" + "8. 10c\n" + "Please input the amount of 10c coins you want to insert.\n" +
                "Accepted $0.10.\n" + "Running payment total: $0.10 (what you have paid).\n" + "Your Line Total is: 39.90\n" + "Please select which note/coin you want to input.\n" +
                "1. $20\n" + "2. $10\n" + "3. $5\n" + "4. $2\n" + "5. $1\n" + "6. 50c\n" + "7. 20c\n" + "8. 10c\n" + "Please input the amount of 20c coins you want to insert.\n" + "Accepted $0.20.\n" +
                "Running payment total: $0.30 (what you have paid).\n" + "Your Line Total is: 39.70\n" + "Please select which note/coin you want to input.\n" +
                "1. $20\n" + "2. $10\n" + "3. $5\n" + "4. $2\n" + "5. $1\n" + "6. 50c\n" + "7. 20c\n" + "8. 10c\n" + "Please input the amount of 50c coins you want to insert.\n" +
                "Accepted $0.50.\n" + "Running payment total: $0.80 (what you have paid).\n" + "Your Line Total is: 39.20\n" + "Please select which note/coin you want to input.\n" +
                "1. $20\n" + "2. $10\n" + "3. $5\n" + "4. $2\n" + "5. $1\n" + "6. 50c\n" + "7. 20c\n" + "8. 10c\n" + "Please input the amount of $1 coin you want to insert.\n" +
                "Accepted $1.00.\n" + "Running payment total: $1.80 (what you have paid).\n" + "Your Line Total is: 38.20\n" +
                "Please select which note/coin you want to input.\n" + "1. $20\n" + "2. $10\n" + "3. $5\n" + "4. $2\n" + "5. $1\n" + "6. 50c\n" + "7. 20c\n" + "8. 10c\n" +
                "Please input the amount of $2 coin you want to insert.\n" + "Accepted $2.00.\n" + "Running payment total: $3.80 (what you have paid).\n" +
                "Your Line Total is: 36.20\n" + "Please select which note/coin you want to input.\n" + "1. $20\n" + "2. $10\n" + "3. $5\n" +
                "4. $2\n" + "5. $1\n" + "6. 50c\n" + "7. 20c\n" + "8. 10c\n" + "Please input the amount of $5 note you want to insert.\n" +
                "Accepted $5.00.\n" + "Running payment total: $8.80 (what you have paid).\n" + "Your Line Total is: 31.20\n" + "Please select which note/coin you want to input.\n" +
                "1. $20\n" + "2. $10\n" + "3. $5\n" + "4. $2\n" + "5. $1\n" + "6. 50c\n" + "7. 20c\n" + "8. 10c\n" + "Please input the amount of $10 note you want to insert.\n" + "Accepted $10.00.\n" +
                "Running payment total: $18.80 (what you have paid).\n" + "Your Line Total is: 21.20\n" + "Please select which note/coin you want to input.\n" +
                "1. $20\n" + "2. $10\n" + "3. $5\n" + "4. $2\n" + "5. $1\n" + "6. 50c\n" + "7. 20c\n" + "8. 10c\n" + "Please input the amount of $20 note you want to insert.\n" + "Your change is as follows: \n" +
                "1. $20: 0.\n" + "2. $10: 1.\n" + "3. $5:  1.\n" + "4. $2:  1.\n" + "5. $1:  1.\n" + "6. 50c: 1.\n" + "7. 20c: 1.\n" + "8. 10c: 1.\n" +
                "$18.80 Total change given.\n";

        assertEquals(expected, outContent.toString());
    }

    @Test
    public void generateStockReport() {
        app.generateStockReport(machine,printer);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        String value  = formatter.format(date);

        String expected = "AGILETEAM30 VENDING MACHINE REPORT\n" + "GENERATED ON " + value + "\n" + "====================\n" +
                "\tSUMMARY\n" + "Current Number of Items: " + machine.getNumberItems() +"\n" + "Current Stock Levels\n[ID] NAME QUANTITY\n" + "[1] Snickers: 5\n" +
                "[2] Smith Chips: 10\n" + "[3] Coke: 1\n" + "[4] Skittles: 10\n" + "====================\n" + "CHIPS:\n" + "[ID] NAME PRICE\n" +
                "[2] Smith Chips: $5.00\n" + "\tQUANTITY: 10 / 10\n" + "\tEXP: 11/09/2019\n" + "CHOCOLATES:\n" + "[ID] NAME PRICE\n" +
                "[1] Snickers: $3.00\n" + "\tQUANTITY: 5 / 10\n" + "\tEXP: 10/09/2019\n" + "DRINKS:\n" + "[ID] NAME PRICE\n" +
                "[3] Coke: $3.50\n" + "\tQUANTITY: 1 / 10\n" + "\tEXP: 12/09/2019\n" + "LOLLIES:\n" + "[ID] NAME PRICE\n" + "[4] Skittles: $1.50\n" +
                "\tQUANTITY: 10 / 10\n" + "\tEXP: 13/09/2019\n" + "\n" + "END OF REPORT\n" + "====================\n";

        assertEquals(expected, outContent.toString());
    }

    @Test
    public void generateReports() {

    }

    @Test
    public void generateDailyTransactionsNoTransaction() {
        app.generateDailyTransactionReport(machine);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        String value = formatter.format(date);
        String expected = "AGILETEAM30 DAILY TRANSACTION REPORT\n" +
                "GENERATED ON " + value + "\n" +
                "====================\n" +
                "No transactions found\n" +
                "\n" +
                "END OF REPORT\n" +
                "====================\n";
        assertEquals(expected, outContent.toString());
    }

    @Test
    public void generateCancelledTransactionsNoTransaction() {
        app.generateCancelledTransactionReport(machine);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        String value = formatter.format(date);
        String expected = "AGILETEAM30 CANCELLED TRANSACTIONS REPORT\n" +
                "GENERATED ON " + value + "\n" +
                "====================\n" +
                "No transactions found\n" +
                "\n" +
                "END OF REPORT\n" +
                "====================\n";
        assertEquals(expected, outContent.toString());
    }

    @Test
    public void testCorrectCheckPassword(){
        InputHandler correctInput = new InputHandler(new Scanner("password\n"));
        boolean correct = App.checkPassword(printer, correctInput, machine);
        String expected = "Hello, please input the STAFF password\nThat was the correct password!\n";
        assertTrue(correct);
        assertEquals(expected, outContent.toString());

    }

    @Test
    public void testWrongCheckPassword(){
        InputHandler wrongInput = new InputHandler(new Scanner("wrong\n1\nwrong\n3\n2\n"));
        boolean wrong = App.checkPassword(printer, wrongInput, machine);
        String expected = "Hello, please input the STAFF password\n" +
                "Sorry that was the wrong password\n" + "Would you like to: \n" + "1. Try again\n" + "2. Return to main menu\n" +
                "Hello, please input the STAFF password\n" + "Sorry that was the wrong password\n" + "Would you like to: \n" + "1. Try again\n" + "2. Return to main menu\n"
                + "Invalid input. Please input a proper value\n";
        assertFalse(wrong);
        assertEquals(expected, outContent.toString());
    }
}
