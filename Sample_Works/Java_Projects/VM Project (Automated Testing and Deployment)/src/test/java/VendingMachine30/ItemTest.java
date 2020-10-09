import VendingMachine30.Beverage;
import VendingMachine30.Chips;
import VendingMachine30.Chocolate;
import VendingMachine30.Lolly;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.ComparisonFailure;

public class ItemTest {


    @Test
    public void testItem()

    {
        Lolly chocolate = new Lolly("Chocolate", 3.40, 1, "28/10/2019", 1);

        assertEquals(3.40, chocolate.getPrice(), 0.0);
        chocolate.setPrice(0);
        assertEquals(0, chocolate.getPrice(), 0.0);
        chocolate.setPrice(10);
        assertEquals(10, chocolate.getPrice(), 0.0);
        chocolate.setPrice(-1);
        assertEquals(10, chocolate.getPrice(), 0.0);

    }


    @Test
    public void testLolly()
    {
        Lolly chocolate = new Lolly("Chocolate", 3.40, 1, "01/01/2019", 1);
        Chips chips = new Chips("French fries", 2.11, 2, "05/10/2019", 2);
        Beverage wine = new Beverage("Wine", 15.99, 1, "07/10/2019", 3);

        assertEquals("Chocolate", chocolate.getName());
        assertEquals(3.40, chocolate.getPrice(), 0);
        assertEquals(1, chocolate.getQuantity());
        assertEquals("lolly", chocolate.getType());
    }

    @Test
    public void testChips()
    {
        Lolly chocolate = new Lolly("Chocolate", 3.40, 1, "28/10/2019", 1);
        Chips chips = new Chips("French fries", 2.11, 2, "29/10/2019", 2);
        Beverage wine = new Beverage("Wine", 15.99, 1, "30/10/2019", 3);

        assertEquals("French fries", chips.getName());
        assertEquals(2.11, chips.getPrice(), 0);
        assertEquals(2, chips.getQuantity());
        assertEquals("chips", chips.getType());

    }

    @Test
    public void testBeverage()
    {
        Lolly chocolate = new Lolly("Chocolate", 3.40, 1, "28/10/2019", 1);
        Chips chips = new Chips("French fries", 2.11, 2, "29/10/2019", 2);
        Beverage wine = new Beverage("Wine", 15.99, 1, "30/10/2019", 3);

        assertEquals("Wine", wine.getName());
        assertEquals(15.99, wine.getPrice(), 0);
        assertEquals(1, wine.getQuantity());
        assertEquals("drink", wine.getType());
    }

    @Test
    public void testChocolate()
    {
        Chocolate chocolate = new Chocolate("Chocolate", 3.40, 1, "28/10/2019", 1);

        assertEquals("Chocolate", chocolate.getName());
        assertEquals(3.40, chocolate.getPrice(), 0);
        assertEquals(1, chocolate.getQuantity());
        assertEquals("chocolate", chocolate.getType());
    }

    @Test
    public void testExpiry()
    {
        Lolly chocolate = new Lolly("Chocolate", 3.40, 1, "28/10/2019", 1);
        Chips chips = new Chips("French fries", 2.11, 2, "29/10/2019", 2);
        Beverage wine = new Beverage("Wine", 15.99, 1, "30/10/2019", 3);

        assertEquals("28/10/2019", chocolate.getExpiry());
        assertEquals("29/10/2019", chips.getExpiry());
        assertEquals("30/10/2019", wine.getExpiry());
    }

    @Test
    public void testID()
    {
        Lolly chocolate = new Lolly("Chocolate", 3.40, 1, "28/10/2019", 1);
        Chips chips = new Chips("French fries", 2.11, 2, "29/10/2019", 2);
        Beverage wine = new Beverage("Wine", 15.99, 1, "30/10/2019", 3);

        assertEquals(1, chocolate.getID());
        assertEquals(2, chips.getID());
        assertEquals(3, wine.getID());
    }

}