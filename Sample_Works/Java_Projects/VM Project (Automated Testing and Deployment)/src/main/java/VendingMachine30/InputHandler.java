package VendingMachine30;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class InputHandler {

    Scanner scanner;

    public InputHandler(Scanner scanner){
        this.scanner = scanner;
    }



    public int handler() throws NoSuchElementException{
        try {
            String nextIntString = scanner.nextLine(); //get the number as a single line
            int nextInt = Integer.parseInt(nextIntString); //convert the string to an int so that we can find out what the user wants to do.
            return nextInt;

        } catch
        (NumberFormatException e) {
            System.err.println(e.getMessage());
        }
        return -1;
    }


    public double getMoney() throws NoSuchElementException{
        try {
            String nextDoubleString = scanner.nextLine(); //get the number as a single line
            double nextDouble = Double.parseDouble(nextDoubleString); //convert the string to an int so that we can find out what the user wants to do.
            return nextDouble;
            }
         catch (NumberFormatException e){
            System.err.println(e.getMessage());
        }
        return -1;
    }


    public String getPassword() throws NoSuchElementException{
        try {
            String pw = scanner.nextLine(); //get the number as a single line
            return pw;

        } catch
        (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getItem()throws NoSuchElementException{
        try {
            String item = scanner.nextLine(); //get the number as a single line
            return item;

        } catch
        (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

}
