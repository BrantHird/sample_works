import VendingMachine30.userItem;
import org.junit.Test;

import static org.junit.Assert.*;

public class userItemTest {


    @Test
    public void testInitialise()
    {
        userItem my_item = new userItem("coffee", 2.00, 1, "29/10/2019", 1);
        assertNotNull(my_item);

        assertNull(my_item.getType());

        assertEquals("coffee", my_item.getName());
        assertEquals(2, my_item.getPrice(), 0);
        assertEquals(1, my_item.getQuantity());
        assertEquals("29/10/2019", my_item.getExpiry());
        assertEquals(1, my_item.getID());
    }
}