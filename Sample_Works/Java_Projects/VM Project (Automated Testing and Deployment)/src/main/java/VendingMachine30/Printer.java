package VendingMachine30;

import java.util.ArrayList;

public class Printer {
    private final VendingMachine machine;

    public Printer(VendingMachine machine) {
        this.machine = machine;
    }


    public void printGreeting() {
        System.out.println("Hello! Welcome to Agile Team 30's Vending Machine!");
    }

    public void printSelectPrompt() {
        System.out.println("Please select an option to continue: \n1.Staff Menu\n2.Purchase\n3.Exit");
    }

    public void printTryAgain() {
        System.out.println("Would you like to: ");
        System.out.println("1. Try again");
        System.out.println("2. Return to main menu");

    }

    public void printStaffMenu() {
        System.out.println("Please pick an option from the STAFF Menu");
        System.out.println("1. Alter Stock Quantities");
        System.out.println("2. Completely Fill Stock");
        System.out.println("3. Change Item Price");
        System.out.println("4. Generate Reports");
        System.out.println("5. Set Staff Password");
        System.out.println("6. Add Item");
        System.out.println("7. Remove Item");
        System.out.println("8. Exit Staff Menu");


    }


    public void printCategoryOptions() {
        System.out.println("1. Drinks");
        System.out.println("2. Chips");
        System.out.println("3. Chocolates");
        System.out.println("4. Lollies");
        System.out.println("5. All Items");
    }


    public void printAllItemOptions() {
        ArrayList<Item> items = machine.getAllItems();
        System.out.println("[ID] NAME PRICE");
        for (Item item : items) {
            System.out.print("[" + item.getID() + "] ");
            System.out.print(item.getName() + ": ");
            System.out.print("$");
            System.out.printf("%.2f\n", item.getPrice());
        }
    }

    public void printAllItemStock() {
        ArrayList<Item> items = machine.getAllItems();
        System.out.println("[ID] NAME QUANTITY");
        for (Item item : items) {

            System.out.print("[" + item.getID() + "] ");
            System.out.print(item.getName() + ": ");
            System.out.printf("%d\n", item.getQuantity());
        }
    }


    public void printChocolates(boolean staff) {
        ArrayList<Item> items = machine.getAllItems();
        System.out.println("[ID] NAME PRICE");
        for (Item item : items) {
            if (item.getClass().equals(Chocolate.class)) {
                System.out.print("[" + item.getID() + "] ");
                System.out.print(item.getName() + ": ");
                System.out.print("$");
                System.out.printf("%.2f\n", item.getPrice());
                if (staff) {
                    System.out.printf("\tQUANTITY: %d / 10\n", item.getQuantity());
                    System.out.printf("\tEXP: %s\n", item.getExpiry());
                }
            }
        }
    }

    public void printDrinks(boolean staff) {
        ArrayList<Item> items = machine.getAllItems();
        System.out.println("[ID] NAME PRICE");
        for (Item item : items) {
            if (item.getClass().equals(Beverage.class)) {
                System.out.print("[" + item.getID() + "] ");
                System.out.print(item.getName() + ": ");
                System.out.print("$");
                System.out.printf("%.2f\n", item.getPrice());
                if (staff) {
                    System.out.printf("\tQUANTITY: %d / 10\n", item.getQuantity());
                    System.out.printf("\tEXP: %s\n", item.getExpiry());
                }
            }
        }
    }

    public void printChips(boolean staff) {
        ArrayList<Item> items = machine.getAllItems();
        System.out.println("[ID] NAME PRICE");
        for (Item item : items) {
            if (item.getClass().equals(Chips.class)) {
                System.out.print("[" + item.getID() + "] ");
                System.out.print(item.getName() + ": ");
                System.out.print("$");
                System.out.printf("%.2f\n", item.getPrice());
                if (staff) {
                    System.out.printf("\tQUANTITY: %d / 10\n", item.getQuantity());
                    System.out.printf("\tEXP: %s\n", item.getExpiry());
                }
            }
        }
    }


    public void printLollies(boolean staff) {
        ArrayList<Item> items = machine.getAllItems();
        System.out.println("[ID] NAME PRICE");
        for (Item item : items) {
            if (item.getClass().equals(Lolly.class)) {
                System.out.print("[" + item.getID() + "] ");
                System.out.print(item.getName() + ": ");
                System.out.print("$");
                System.out.printf("%.2f\n", item.getPrice());
                if (staff) {
                    System.out.printf("\tQUANTITY: %d / 10\n", item.getQuantity());
                    System.out.printf("\tEXP: %s\n", item.getExpiry());
                }
            }
        }
    }


    public void printPasswordMenu() {
        System.out.println("Hello, please input the STAFF password");
    }

    public void printIncorrectPasswordMessage() {
        System.out.println("Sorry that was the wrong password");
    }

    public void printCorrectPasswordMessage() {
        System.out.println("That was the correct password!");
    }

    public void printInvalidInputMessage() {
        System.out.println("Invalid input. Please input a proper value");
    }

    public void printContinuePurchase() {
        System.out.println("Would you like to:");
        System.out.println("1. Continue shopping");
        System.out.println("2. View cart");
        System.out.println("3. Finalise purchase");
        System.out.println("4. Cancel transaction");
    }

    public void printContinuePurchaseInCart() {
        System.out.println("Would you like to:");
        System.out.println("1. Continue shopping");
        System.out.println("2. Finalise purchase");
        System.out.println("3. Cancel transaction");
    }

    public void printReportsOptions() {
        System.out.println("Which kind of report would you like to view:");
        System.out.println("1. Stock Report");
        System.out.println("2. Daily Transactions");
        System.out.println("3. Cancelled Transactions");
    }


}
