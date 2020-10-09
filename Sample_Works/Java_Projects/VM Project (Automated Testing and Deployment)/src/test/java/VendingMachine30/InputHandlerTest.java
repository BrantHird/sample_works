import VendingMachine30.FileReader;
import VendingMachine30.InputHandler;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class InputHandlerTest {


    @Test(expected = NoSuchElementException.class)
    public void testHandler() {
        InputHandler user_input = new InputHandler(new Scanner(System.in));

        assertEquals(-1, user_input.handler());

        ByteArrayInputStream in = new ByteArrayInputStream("1".getBytes());
        System.setIn(in);
        assertEquals(1, user_input.handler());

        System.setIn(System.in);
    }

    @Test
    public void handler() {
        ByteArrayInputStream in = new ByteArrayInputStream("2".getBytes());
        System.setIn(in);
        InputHandler user_input = new InputHandler(new Scanner(System.in));
        assertEquals(2, user_input.handler());
        System.setIn(System.in);
    }

    @Test
    public void handlerFail() {
        ByteArrayInputStream in = new ByteArrayInputStream("hi".getBytes());
        System.setIn(in);
        InputHandler user_input = new InputHandler(new Scanner(System.in));
        assertEquals(-1, user_input.handler());
        System.setIn(System.in);
    }

    @Test
    public void getMoney() {
        ByteArrayInputStream in = new ByteArrayInputStream("5".getBytes());
        System.setIn(in);
        InputHandler user_input = new InputHandler(new Scanner(System.in));
        assertEquals(5, user_input.getMoney(), 0);
        System.setIn(System.in);
    }

    @Test
    public void getMoneyFail() {
        ByteArrayInputStream in = new ByteArrayInputStream("hi".getBytes());
        System.setIn(in);
        InputHandler user_input = new InputHandler(new Scanner(System.in));
        assertEquals(-1, user_input.getMoney(), 0);
        System.setIn(System.in);
    }

    @Test
    public void testGetPasswordSuccess() {
        ByteArrayInputStream in = new ByteArrayInputStream("password".getBytes());
        System.setIn(in);

        InputHandler user_input = new InputHandler(new Scanner(System.in));
        assertEquals("password", user_input.getPassword());
        System.setIn(System.in);
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetPasswordFail() {
        InputHandler input = new InputHandler(new Scanner(System.in));
        input.getPassword();
    }

    @Test
    public void getItemSuccess() {
        ByteArrayInputStream in = new ByteArrayInputStream("1".getBytes());
        System.setIn(in);
        InputHandler input = new InputHandler(new Scanner(System.in));
        assertEquals("1", input.getItem());
    }

    @Test(expected = NoSuchElementException.class)
    public void getItemFail() {
        InputHandler input = new InputHandler(new Scanner(System.in));
        input.getItem();
    }

}