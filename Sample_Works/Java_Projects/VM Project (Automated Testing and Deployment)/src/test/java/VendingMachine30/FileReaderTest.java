import VendingMachine30.FileReader;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class FileReaderTest {
    private static FileReader f;

    @BeforeClass
    public static void setup() {
        try {
            f = new FileReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getName() {
        assertEquals(f.getName(), f.name);
    }

    @Test
    public void getType() {
        assertEquals(f.getType(), f.Type);
    }

    @Test
    public void getExpiry() {
        assertEquals(f.getExpiry(), f.expiry);
    }

    @Test
    public void getPrice() {
        assertEquals(f.getPrice(), f.price);
    }

    @Test
    public void getQuantity() {
        assertEquals(f.getQuantity(), f.quantity);
    }

    @Test
    public void getPassword() {
        assertEquals(f.getPassword(), f.password);
    }
}