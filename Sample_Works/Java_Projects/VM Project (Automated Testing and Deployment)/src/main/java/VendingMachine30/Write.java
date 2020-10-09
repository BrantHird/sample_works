package VendingMachine30;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.*;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Write {
    public void writePassword(String pass){
        try {
            FileWriter fw = new FileWriter("src/main/Resources/password.txt");
            PrintWriter pw = new PrintWriter(fw);

            pw.println(pass);
            pw.close();
            fw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeItems(List<Item> items){

            try {
                FileWriter fw = new FileWriter("src/main/Resources/input.txt");
                PrintWriter pw = new PrintWriter(fw);
                for (int i = 0 ; i < items.size() ; i++ ) {
                    pw.print(items.get(i).getName());
                    pw.print(",");
                    pw.print(items.get(i).getPrice());
                    pw.print(",");
                    pw.print(items.get(i).getQuantity());
                    pw.print(",");
                    pw.print(items.get(i).getType());
                    pw.print(",");
                    pw.print(items.get(i).getExpiry());
                    pw.print("\n");
                }
                pw.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }

    }

    public void writeTransactions(Transaction transaction){
        try{
            FileWriter fw = new FileWriter("src/main/Resources/RecordedTransactions.txt", true);
            PrintWriter pw = new PrintWriter(fw);
            HashMap<Item,Integer> items = transaction.getItems();

            for (Item item : items.keySet()){
                pw.print("Item");
                pw.print(",");
                pw.print(item.getName());
                pw.print(",");
                pw.print(item.getPrice());
                pw.print(",");
                pw.print(item.getQuantity());
                pw.print(",");
                pw.print(item.getType());
                pw.print(",");
                pw.print(item.getExpiry());
                pw.print(",");
                pw.print(items.get(item));
                pw.print("\n");
            }
            pw.print(transaction.isCancelled());
            pw.print(",");

            DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            String d = format.format(transaction.getTime());
            pw.print(d);
            pw.print(",");
            pw.print(transaction.getTotal());
            pw.print(",");
            pw.print(transaction.getChange());
            pw.print(",");
            pw.print(transaction.getId());
            pw.print("\n");
            pw.close();
            fw.close();
            }

        catch (IOException e){
            e.printStackTrace();
        }
    }



}
