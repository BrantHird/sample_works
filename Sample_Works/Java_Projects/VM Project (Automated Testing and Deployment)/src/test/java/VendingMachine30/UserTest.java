import VendingMachine30.Beverage;
import VendingMachine30.Chips;
import VendingMachine30.Item;
import VendingMachine30.User;
import org.junit.Test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;

public class UserTest {



    @Test
    public void testGetLineTotal()
    {
        User tom = new User();
        Beverage wine = new Beverage("Wine", 2, 1, "28/10/2019", 1);
        Chips french_fries = new Chips("French Fries", 2.1, 1, "29/10/2019", 2);
        tom.addItem(wine, 1);
        tom.addItem(french_fries, 1);

        assertEquals(4.1, tom.getLineTotal(), 0);
    }

    @Test
    public void testGetSelectedItems()
    {
        User tom = new User();
        Beverage wine = new Beverage("Wine", 2, 1, "28/10/2019", 1);
        Chips french_fries = new Chips("French Fries", 2.1, 1, "29/10/2019", 2);

        tom.addItem(wine, 1);
        HashMap<Item, Integer> tom_item = new HashMap<>();
        tom_item.put(wine, 1);

        assertEquals(tom_item, tom.getSelectedItems());

        tom_item.put(french_fries, 1);
        tom.addItem(french_fries, 1);
        assertEquals(tom_item, tom.getSelectedItems());
    }

    @Test
    public void testAddItem()
    {
        User tom = new User();
        Beverage wine = new Beverage("Wine", 2, 1, "28/10/2019", 1);
        Chips french_fries = new Chips("French Fries", 2.1, 1, "29/10/2019", 2);

        assertEquals(0, tom.getSelectedItems().size());

        tom.addItem(wine, 3);
        tom.addItem(french_fries, 2);

        assertEquals(2, tom.getSelectedItems().size());


        assertEquals(true, tom.getSelectedItems().containsKey(wine));
        assertEquals(true, tom.getSelectedItems().containsKey(french_fries));

    }

    @Test
    public void testAddExistingItem() {
        User tom = new User();
        Beverage wine = new Beverage("Wine", 2, 1, "28/10/2019", 1);
        tom.addItem(wine, 1);
        assertEquals(true, tom.getSelectedItems().containsKey(wine));
        Integer expected = 1;
        assertEquals(expected, tom.getSelectedItems().get(wine));

        expected = 2;
        tom.addItem(wine, 1);
        assertEquals(true, tom.getSelectedItems().containsKey(wine));
        assertEquals(expected, tom.getSelectedItems().get(wine));
    }

    @Test
    public void testRemoveItem()
    {
        User tom = new User();
        Beverage wine = new Beverage("Wine", 2, 1, "28/10/2019", 1);
        Chips french_fries = new Chips("French Fries", 2.1, 1, "29/10/2019", 2);

        assertEquals(0, tom.getSelectedItems().size());
        tom.addItem(wine, 1);
        tom.addItem(french_fries, 1);

        // Cannot remove element while iterating. Use Iterator object
        tom.removeItem("French Fries");
        assertEquals(1, tom.getSelectedItems().size());
    }

    @Test
    public void testResetItems()
    {
        User tom = new User();
        Beverage wine = new Beverage("Wine", 2, 1, "28/10/2019", 1);
        Chips french_fries = new Chips("French Fries", 2.1, 1, "29/10/2019", 2);

        assertEquals(0, tom.getSelectedItems().size());
        tom.addItem(wine, 1);
        tom.addItem(french_fries, 1);

        tom.resetItems();
        assertEquals(0, tom.getSelectedItems().size());
    }


}