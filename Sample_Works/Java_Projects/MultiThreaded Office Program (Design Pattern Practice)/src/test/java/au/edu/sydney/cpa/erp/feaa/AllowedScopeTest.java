package au.edu.sydney.cpa.erp.feaa;

import au.edu.sydney.cpa.erp.auth.AuthModule;
import au.edu.sydney.cpa.erp.auth.AuthToken;
import au.edu.sydney.cpa.erp.database.TestDatabase;
import au.edu.sydney.cpa.erp.ordering.Client;
import au.edu.sydney.cpa.erp.ordering.Order;
import au.edu.sydney.cpa.erp.ordering.Report;
import au.edu.sydney.cpa.erp.feaa.reports.ReportDatabase;
import au.edu.sydney.cpa.erp.feaa.reports.ReportImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest( { TestDatabase.class, AuthModule.class, ReportDatabase.class})
public class AllowedScopeTest {

    private TestDatabase mockedDB;
    private FEAAFacade facade;
    private AuthToken mockedToken;
    private Report mockedProd100;
    private Report mockedProd300;

    @Before
    public void setup() {
        // We need to stick an instance in the static class here, so we revert to native Mockito
        mockedDB = mock(TestDatabase.class);
        Whitebox.setInternalState(TestDatabase.class, "instance", mockedDB);

        mockStatic(AuthModule.class);

        mockedProd100 = mock(Report.class);
        when(mockedProd100.getCommission()).thenReturn(100.0);
        when(mockedProd100.getReportName()).thenReturn("Fake Product");

        mockedProd300 = mock(Report.class);
        when(mockedProd300.getCommission()).thenReturn(300.0);
        when(mockedProd300.getReportName()).thenReturn("Fake Report 2");

        facade = new FEAAFacade();
    }

    private void setupLogin() {
        mockedToken = mock(AuthToken.class);
        when(AuthModule.login("username", "password")).thenReturn(mockedToken);
        when(AuthModule.authenticate(mockedToken)).thenReturn(true); // This works for cross-use like db or contact

        facade.login("username", "password");
    }

    @Test
    public void login() {
        when(AuthModule.login("username", "password")).thenReturn(mock(AuthToken.class));

        assertTrue(facade.login("username", "password"));
        verifyStatic(AuthModule.class);
        AuthModule.login(eq("username"), eq("password"));

        assertFalse(facade.login("something else", "password"));
        verifyStatic(AuthModule.class);
        AuthModule.login(eq("something else"), eq("password"));
    }

    @Test
    public void getAllOrders() {
        boolean thrown = false;
        try {
            facade.getAllOrders();
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);

        setupLogin();

        doThrow(new AssertionError("Unexpected Logout Interaction")).when(AuthModule.class);
        AuthModule.logout(any());

        Order mockedOrder1 = mock(Order.class);
        when(mockedOrder1.getOrderID()).thenReturn(1001);
        Order mockedOrder2 = mock(Order.class);
        when(mockedOrder2.getOrderID()).thenReturn(2002);
        when(mockedDB.getOrders(mockedToken)).thenReturn(Arrays.asList(mockedOrder1, mockedOrder2));

        List<Integer> result = facade.getAllOrders();
        assertEquals(2, result.size());
        assertTrue(result.contains(1001));
        assertTrue(result.contains(2002));

        verify(mockedDB).getOrders(mockedToken);
    }

    @Test
    public void createOrder() {
        boolean thrown = false;
        try {
            facade.createOrder(0, LocalDateTime.now(), false, false, 0, 0, 0, 0);
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);

        setupLogin();
        doThrow(new AssertionError("Unexpected Logout Interaction")).when(AuthModule.class);
        AuthModule.logout(any());

        when(mockedDB.getClientIDs(mockedToken)).thenReturn(Arrays.asList(1, 2, 3));

        thrown = false;
        try {
            facade.createOrder(-1, LocalDateTime.now(), false, false, 0, 0, 0, 0);
        } catch (IllegalArgumentException ignore) {
            thrown = true;
        }

        assertTrue("Accepts invalid Client ID", thrown);

        Integer testOrderID = facade.createOrder(1, LocalDateTime.now(), false, false, 0, 0, 0, 0);
        assertNull("Accepts invalid discountType", testOrderID);
        testOrderID = facade.createOrder(1, LocalDateTime.now(), false, false, 3, 0, 0, 0);
        assertNull("Accepts invalid discountType", testOrderID);
    }

    @Test
    public void testOrderCriticalAudit() {
        setupLogin();
        when(mockedDB.getClientIDs(mockedToken)).thenReturn(Arrays.asList(1, 2, 3));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        doNothing().when(mockedDB).saveOrder(eq(mockedToken), captor.capture());

        int testOrderID = facade.createOrder(2, LocalDateTime.now(), true, false, 2, 10, -1, -1);
        facade.logout();

        verify(mockedDB).saveOrder(eq(mockedToken), any());
        Order order = captor.getValue();
        assertEquals(testOrderID, order.getOrderID());

        order.setReport(mockedProd100, 30);
        order.setReport(mockedProd300, 1);
        assertEquals(3630, order.getTotalCommission(), 0.0001);

        String patternString = "\\*NOT FINALISED\\*\\nOrder details \\(id #0\\)\\nDate: [0-9]{4}-[0-9]{2}-[0-9]{2}\\nReports:\\n\\tReport name: Fake Product\\tEmployee Count: 30\\tCommission per employee: \\$100.00\\tSubtotal: \\$3,000.00\\n\\tReport name: Fake Report 2\\tEmployee Count: 1\\tCommission per employee: \\$300.00\\tSubtotal: \\$300.00\\nCritical Loading: \\$330.00\\nTotal cost: \\$3,630.00\\n";
        Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);

        assertTrue(pattern.matcher(order.longDesc()).matches());

        assertEquals("ID:0 $3,630.00", order.shortDesc());

        assertEquals("Your priority business account has been charged: $3,630.00\n" +
                "Please see your internal accounting department for itemised details.", order.generateInvoiceData());

        assertNotSame(order, order.copy());

    }

    @Test
    public void testOrderCriticalAuditSched() {
        setupLogin();
        when(mockedDB.getClientIDs(mockedToken)).thenReturn(Arrays.asList(1, 2, 3));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        doNothing().when(mockedDB).saveOrder(eq(mockedToken), captor.capture());

        int testOrderID = facade.createOrder(2, LocalDateTime.now(), true, true, 2, 10, -1, 10);
        facade.logout();

        verify(mockedDB).saveOrder(eq(mockedToken), any());
        Order order = captor.getValue();
        assertEquals(testOrderID, order.getOrderID());

        order.setReport(mockedProd100, 30);
        order.setReport(mockedProd300, 1);
        assertEquals(36300, order.getTotalCommission(), 0.0001);

        String patternString = "\\*NOT FINALISED\\*\\nOrder details \\(id #0\\)\\nDate: [0-9]{4}-[0-9]{2}-[0-9]{2}\\nNumber of quarters: 10\\nReports:\\n\\tReport name: Fake Product\\tEmployee Count: 30\\tCommission per employee: \\$100.00\\tSubtotal: \\$3,000.00\\n\\tReport name: Fake Report 2\\tEmployee Count: 1\\tCommission per employee: \\$300.00\tSubtotal: \\$300.00\\nCritical Loading: \\$3,300.00\\nRecurring cost: \\$3,630.00\\nTotal cost: \\$36,300.00\\n";
        Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);

        assertTrue(pattern.matcher(order.longDesc()).matches());

        assertEquals("ID:0 $3,630.00 per quarter, $36,300.00 total", order.shortDesc());

        assertEquals("Your priority business account will be charged: $3,630.00 each quarter for 10 quarters, with a total overall cost of: $36,300.00\n" +
                "Please see your internal accounting department for itemised details.", order.generateInvoiceData());

        assertNotSame(order, order.copy());
    }

    @Test
    public void testOrderCriticalRegular() {
        setupLogin();
        when(mockedDB.getClientIDs(mockedToken)).thenReturn(Arrays.asList(1, 2, 3));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        doNothing().when(mockedDB).saveOrder(eq(mockedToken), captor.capture());

        int testOrderID = facade.createOrder(2, LocalDateTime.now(), true, false, 1, 10, 10, -1);
        facade.logout();

        verify(mockedDB).saveOrder(eq(mockedToken), any());
        Order order = captor.getValue();
        assertEquals(testOrderID, order.getOrderID());

        order.setReport(mockedProd100, 10);
        order.setReport(mockedProd300, 1);

        String patternString = "\\*NOT FINALISED\\*\\nOrder details \\(id #0\\)\\nDate: [0-9]{4}-[0-9]{2}-[0-9]{2}\\nReports:\\n\\tReport name: Fake Product\\tEmployee Count: 10\\tCommission per employee: \\$100\\.00\\tSubtotal: \\$1,000.00\\n\\tReport name: Fake Report 2\\tEmployee Count: 1\\tCommission per employee: \\$300.00\\tSubtotal: \\$300.00\\nCritical Loading: \\$130.00\\nTotal cost: \\$1,430.00\\n";

        Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);


        assertTrue(pattern.matcher(order.longDesc()).matches());

        assertEquals(1430, order.getTotalCommission(), 0.0001);
        assertEquals("ID:0 $1,430.00", order.shortDesc());

        assertEquals("Your priority business account has been charged: $1,430.00\n" +
                "Please see your internal accounting department for itemised details.", order.generateInvoiceData());

        assertNotSame(order, order.copy());
    }

    @Test
    public void testOrderCriticalRegularSched() {
        setupLogin();
        when(mockedDB.getClientIDs(mockedToken)).thenReturn(Arrays.asList(1, 2, 3));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        doNothing().when(mockedDB).saveOrder(eq(mockedToken), captor.capture());

        int testOrderID = facade.createOrder(2, LocalDateTime.now(), true, true, 1, 10, 10, 10);
        facade.logout();

        verify(mockedDB).saveOrder(eq(mockedToken), any());
        Order order = captor.getValue();
        assertEquals(testOrderID, order.getOrderID());

        order.setReport(mockedProd100, 10);
        order.setReport(mockedProd300, 1);

        String patternString = "\\*NOT FINALISED\\*\\nOrder details \\(id #0\\)\\nDate: [0-9]{4}-[0-9]{2}-[0-9]{2}\\nNumber of quarters: 10\\nReports:\\n\\tReport name: Fake Product\\tEmployee Count: 10\\tCommission per employee: \\$100.00\\tSubtotal: \\$1,000.00\\n\\tReport name: Fake Report 2\\tEmployee Count: 1\\tCommission per employee: \\$300.00\\tSubtotal: \\$300.00\\nCritical Loading: \\$1,300.00\\nRecurring cost: \\$1,430.00\\nTotal cost: \\$14,300.00\\n";

        Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);

        assertTrue(pattern.matcher(order.longDesc()).matches());

        assertEquals(14300, order.getTotalCommission(), 0.0001);

        assertEquals("ID:0 $1,430.00 per quarter, $14,300.00 total", order.shortDesc());

        assertEquals("Your priority business account will be charged: $1,430.00 each quarter for 10 quarters, with a total overall cost of: $14,300.00\n" +
                "Please see your internal accounting department for itemised details.", order.generateInvoiceData());

        assertNotSame(order, order.copy());
    }

    @Test
    public void testOrderStandardAudit() {
        setupLogin();
        when(mockedDB.getClientIDs(mockedToken)).thenReturn(Arrays.asList(1, 2, 3));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        doNothing().when(mockedDB).saveOrder(eq(mockedToken), captor.capture());

        int testOrderID = facade.createOrder(2, LocalDateTime.now(), false, false, 2, -1, -1, -1);
        facade.logout();

        verify(mockedDB).saveOrder(eq(mockedToken), any());
        Order order = captor.getValue();
        assertEquals(testOrderID, order.getOrderID());

        order.setReport(mockedProd100, 10);
        order.setReport(mockedProd300, 1);

        assertEquals(1300, order.getTotalCommission(), 0.0001);

        String patternString = "\\*NOT FINALISED\\*\\nOrder details \\(id #0\\)\\nDate: [0-9]{4}-[0-9]{2}-[0-9]{2}\\nReports:\\n\\tReport name: Fake Product\\tEmployee Count: 10\\tCommission per employee: \\$100.00\\tSubtotal: \\$1,000.00\\n\\tReport name: Fake Report 2\\tEmployee Count: 1\\tCommission per employee: \\$300.00\\tSubtotal: \\$300.00\\nTotal cost: \\$1,300.00\\n";

        Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);

        assertTrue(pattern.matcher(order.longDesc()).matches());

        assertEquals("ID:0 $1,300.00", order.shortDesc());

        assertEquals("Thank you for your Crimson Permanent Assurance accounting order!\n" +
                "The cost to provide these services: $1,300.00\n" +
                "Please see below for details:\n" +
                "\tReport name: Fake Product\tEmployee Count: 10\tCost per employee: $100.00\tSubtotal: $1,000.00\n" +
                "\tReport name: Fake Report 2\tEmployee Count: 1\tCost per employee: $300.00\tSubtotal: $300.00\n", order.generateInvoiceData());

        assertNotSame(order, order.copy());
    }

    @Test
    public void testOrderStandardAuditSched() {
        setupLogin();
        when(mockedDB.getClientIDs(mockedToken)).thenReturn(Arrays.asList(1, 2, 3));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        doNothing().when(mockedDB).saveOrder(eq(mockedToken), captor.capture());

        int testOrderID = facade.createOrder(2, LocalDateTime.now(), false, true, 2, -1, -1, 10);
        facade.logout();

        verify(mockedDB).saveOrder(eq(mockedToken), any());
        Order order = captor.getValue();
        assertEquals(testOrderID, order.getOrderID());

        order.setReport(mockedProd100, 10);
        order.setReport(mockedProd300, 1);

        String patternString = "\\*NOT FINALISED\\*\\nOrder details \\(id #0\\)\\nDate: [0-9]{4}-[0-9]{2}-[0-9]{2}\\nNumber of quarters: 10\\nReports:\\n\\tReport name: Fake Product\\tEmployee Count: 10\\tCommission per employee: \\$100.00\\tSubtotal: \\$1,000.00\\n\\tReport name: Fake Report 2\\tEmployee Count: 1\\tCommission per employee: \\$300.00\\tSubtotal: \\$300.00\\nRecurring cost: \\$1,300.00\\nTotal cost: \\$13,000.00\\n";

        Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);

        assertTrue(pattern.matcher(order.longDesc()).matches());

        assertEquals(13000, order.getTotalCommission(), 0.0001);

        assertEquals("ID:0 $1,300.00 per quarter, $13,000.00 total", order.shortDesc());

        assertEquals("Thank you for your Crimson Permanent Assurance accounting order!\n" +
                "The cost to provide these services: $1,300.00 each quarter, with a total overall cost of: $13,000.00\n" +
                "Please see below for details:\n" +
                "\tReport name: Fake Product\tEmployee Count: 10\tCost per employee: $100.00\tSubtotal: $1,000.00\n" +
                "\tReport name: Fake Report 2\tEmployee Count: 1\tCost per employee: $300.00\tSubtotal: $300.00\n", order.generateInvoiceData());

        assertNotSame(order, order.copy());
    }

    @Test
    public void testOrderStandardRegular() {
        setupLogin();
        when(mockedDB.getClientIDs(mockedToken)).thenReturn(Arrays.asList(1, 2, 3));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        doNothing().when(mockedDB).saveOrder(eq(mockedToken), captor.capture());

        int testOrderID = facade.createOrder(2, LocalDateTime.now(), false, false, 1, 10, 10, -1);
        facade.logout();

        verify(mockedDB).saveOrder(eq(mockedToken), any());
        Order order = captor.getValue();
        assertEquals(testOrderID, order.getOrderID());

        order.setReport(mockedProd100, 10);
        order.setReport(mockedProd300, 1);

        String patternString = "\\*NOT FINALISED\\*\\nOrder details \\(id #0\\)\\nDate: [0-9]{4}-[0-9]{2}-[0-9]{2}\\nReports:\\n\\tReport name: Fake Product\\tEmployee Count: 10\\tCommission per employee: \\$100.00\\tSubtotal: \\$1,000.00\\n\\tReport name: Fake Report 2\\tEmployee Count: 1\\tCommission per employee: \\$300.00\\tSubtotal: \\$300.00\\nTotal cost: \\$1,300.00\\n";

        Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);

        assertTrue(pattern.matcher(order.longDesc()).matches());

        assertEquals(1300, order.getTotalCommission(), 0.0001);
        assertEquals("ID:0 $1,300.00", order.shortDesc());

        assertEquals("Thank you for your Crimson Permanent Assurance accounting order!\n" +
                "The cost to provide these services: $1,300.00\n" +
                "Please see below for details:\n" +
                "\tReport name: Fake Product\tEmployee Count: 10\tCost per employee: $100.00\tSubtotal: $1,000.00\n" +
                "\tReport name: Fake Report 2\tEmployee Count: 1\tCost per employee: $300.00\tSubtotal: $300.00\n", order.generateInvoiceData());

        assertNotSame(order, order.copy());
    }

    @Test
    public void testOrderStandardRegularSched() {
        setupLogin();
        when(mockedDB.getClientIDs(mockedToken)).thenReturn(Arrays.asList(1, 2, 3));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        doNothing().when(mockedDB).saveOrder(eq(mockedToken), captor.capture());

        int testOrderID = facade.createOrder(2, LocalDateTime.now(), false, true, 1, 10, 10, 10);
        facade.logout();

        verify(mockedDB).saveOrder(eq(mockedToken), any());
        Order order = captor.getValue();
        assertEquals(testOrderID, order.getOrderID());

        order.setReport(mockedProd100, 10);
        order.setReport(mockedProd300, 1);

        String patternString = "\\*NOT FINALISED\\*\\nOrder details \\(id #0\\)\\nDate: [0-9]{4}-[0-9]{2}-[0-9]{2}\\nNumber of quarters: 10\\nReports:\\n\\tReport name: Fake Product\\tEmployee Count: 10\\tCommission per employee: \\$100.00\\tSubtotal: \\$1,000.00\\n\\tReport name: Fake Report 2\\tEmployee Count: 1\\tCommission per employee: \\$300.00\\tSubtotal: \\$300.00\\nRecurring cost: \\$1,300.00\\nTotal cost: \\$13,000.00\\n";

        Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);

        assertTrue(pattern.matcher(order.longDesc()).matches());

        assertEquals(13000, order.getTotalCommission(), 0.0001);
        assertEquals("ID:0 $1,300.00 per quarter, $13,000.00 total", order.shortDesc());

        assertEquals("Thank you for your Crimson Permanent Assurance accounting order!\n" +
                "The cost to provide these services: $1,300.00 each quarter, with a total overall cost of: $13,000.00\n" +
                "Please see below for details:\n" +
                "\tReport name: Fake Product\tEmployee Count: 10\tCost per employee: $100.00\tSubtotal: $1,000.00\n" +
                "\tReport name: Fake Report 2\tEmployee Count: 1\tCost per employee: $300.00\tSubtotal: $300.00\n", order.generateInvoiceData());

        assertNotSame(order, order.copy());
    }

    @Test
    public void getAllClientIDs() {
        boolean thrown = false;
        try {
            facade.getAllOrders();
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);

        setupLogin();
        doThrow(new AssertionError("Unexpected Logout Interaction")).when(AuthModule.class);
        AuthModule.logout(any());

        when(mockedDB.getClientIDs(mockedToken)).thenReturn(Arrays.asList(1, 4, 7));

        List<Integer> result = facade.getAllClientIDs();
        result.sort(Comparator.naturalOrder());

        assertEquals(Arrays.asList(1, 4, 7), result);
        verify(mockedDB).getClientIDs(mockedToken);
    }

    @Test
    public void getClient() {
        boolean thrown = false;
        try {
            facade.getClient(1);
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);

        setupLogin();
        doThrow(new AssertionError("Unexpected Logout Interaction")).when(AuthModule.class);
        AuthModule.logout(any());

        when(mockedDB.getClientField(mockedToken, 1, "fName")).thenReturn("First");
        when(mockedDB.getClientField(mockedToken, 1, "lName")).thenReturn("Last");
        when(mockedDB.getClientField(mockedToken, 1, "phoneNumber")).thenReturn("12345");
        //when(mockedDB.getClientField(mockedToken, 1, "emailAddress")).thenReturn("email@provider.com");
        when(mockedDB.getClientField(mockedToken, 1, "address")).thenReturn("123 Fake St");
        when(mockedDB.getClientField(mockedToken, 1, "suburb")).thenReturn("Springfield");
        when(mockedDB.getClientField(mockedToken, 1, "state")).thenReturn("NSW");
        when(mockedDB.getClientField(mockedToken, 1, "postCode")).thenReturn("2830");
        //when(mockedDB.getClientField(mockedToken, 1, "internal accounting")).thenReturn("Frank");
        when(mockedDB.getClientField(mockedToken, 1, "businessName")).thenReturn("Qwik-E-Mart");
        when(mockedDB.getClientField(mockedToken, 1, "pigeonCoopID")).thenReturn("117");

        Client result = facade.getClient(1);

        assertEquals("First", result.getFName());
        assertEquals("Last", result.getLName());
        assertEquals("12345", result.getPhoneNumber());
        assertNull(result.getEmailAddress());
        assertEquals("123 Fake St", result.getAddress());
        assertEquals("Springfield", result.getSuburb());
        assertEquals("NSW", result.getState());
        assertEquals("2830", result.getPostCode());
        assertNull(result.getInternalAccounting());
        assertEquals("Qwik-E-Mart", result.getBusinessName());
        assertEquals("117", result.getPigeonCoopID());

        when(mockedDB.getClientField(mockedToken, 1, "emailAddress")).thenReturn("email@provider.com");
        when(mockedDB.getClientField(mockedToken, 1, "internal accounting")).thenReturn("Frank");

        result = facade.getClient(1);
        assertEquals("email@provider.com", result.getEmailAddress());
        assertEquals("Frank", result.getInternalAccounting());
    }

    @Test
    public void removeOrder() {
        boolean thrown = false;
        try {
            facade.removeOrder(1);
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);

        setupLogin();
        doThrow(new AssertionError("Unexpected Logout Interaction")).when(AuthModule.class);
        AuthModule.logout(any());

        when(mockedDB.removeOrder(mockedToken, 1)).thenReturn(true);
        when(mockedDB.removeOrder(mockedToken, 2)).thenReturn(false);

        boolean result = facade.removeOrder(1);

        assertTrue(result);
        verify(mockedDB).removeOrder(mockedToken, 1);
        verifyNoMoreInteractions(mockedDB);

        result = facade.removeOrder(2);

        assertFalse(result);
        verify(mockedDB).removeOrder(mockedToken, 2);
        verifyNoMoreInteractions(mockedDB);
    }

    @Test
    public void getAllReports() {
        boolean thrown = false;
        try {
            facade.getAllReports();
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);

        setupLogin();
        doThrow(new AssertionError("Unexpected Logout Interaction")).when(AuthModule.class);
        AuthModule.logout(any());

        mockStatic(ReportDatabase.class);

        Collection<Report> response = Collections.singletonList(new ReportImpl("test report", 1.0, null, null, null, null, null));

        when(ReportDatabase.getTestReports()).thenReturn(response);

        assertEquals(response, facade.getAllReports());

        verifyStatic(ReportDatabase.class);
        ReportDatabase.getTestReports();
    }

    @Test
    public void finaliseOrder() {
        boolean thrown = false;
        try {
            facade.finaliseOrder(1, Arrays.asList("first", "second"));
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);

        setupLogin();

        when(mockedDB.getClientField(mockedToken, 1, "fName")).thenReturn("First");
        when(mockedDB.getClientField(mockedToken, 1, "lName")).thenReturn("Last");
        when(mockedDB.getClientField(mockedToken, 1, "phoneNumber")).thenReturn("12345");

        Order mockedOrder = mock(Order.class);
        when(mockedOrder.generateInvoiceData()).thenReturn("Invoice data for mocked order");
        when(mockedOrder.getClient()).thenReturn(1);
        when(mockedDB.getOrder(mockedToken, 1)).thenReturn(mockedOrder);

        PrintStream resetOut = System.out;
        ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));

        facade.finaliseOrder(1, Arrays.asList(
                "Carrier Pigeon",
                "Email",
                "Mail",
                "Merchandiser",
                "Phone call"
        ));

        String standardOutput = capturedOut.toString();
        System.setOut(resetOut);

        assertThat(standardOutput, containsString("Invoice data for mocked order"));
        assertThat(standardOutput, containsString("Now robodialling First Last at 12345!"));

        /*
         Consider the following default contact order a standard requirement

        if (contactPriorityAsMethods.size() == 0) { // needs setting to default
            contactPriorityAsMethods = Arrays.asList(
                    ContactMethod.MERCHANDISER,
                    ContactMethod.EMAIL,
                    ContactMethod.CARRIER_PIGEON,
                    ContactMethod.MAIL,
                    ContactMethod.PHONECALL
            );
        }

        */
    }

    @Test
    public void logout() {
        mockedToken = mock(AuthToken.class);
        when(AuthModule.login("username", "password")).thenReturn(mockedToken);
        when(AuthModule.authenticate(mockedToken)).thenReturn(true); // This works for cross-dependencies like db or contact

        facade.login("username", "password");

        facade.logout();

        verifyStatic(AuthModule.class);
        AuthModule.logout(mockedToken);

        boolean thrown = false;
        try {
            facade.getAllOrders();
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    @Test
    public void getOrderTotalCost() {
        boolean thrown = false;
        try {
            facade.getOrderTotalCommission(1);
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);

        setupLogin();
        doThrow(new AssertionError("Unexpected Logout Interaction")).when(AuthModule.class);
        AuthModule.logout(any());

        Order mockedOrder = mock(Order.class);
        when(mockedOrder.getTotalCommission()).thenReturn(1234.56);

        when(mockedDB.getOrder(mockedToken, 1)).thenReturn(mockedOrder);

        assertEquals(1234.56, facade.getOrderTotalCommission(1), 0.0001);
        verify(mockedOrder).getTotalCommission();
    }

    @Test
    public void orderLineSet() {
        boolean thrown = false;
        try {
            facade.orderLineSet(1, null, 1);
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);

        setupLogin();

        Report mockedReport = mock(Report.class);
        Order mockedOrder = mock(Order.class);
        when(mockedDB.getOrder(mockedToken, 1)).thenReturn(mockedOrder);

        facade.orderLineSet(1, mockedReport, 17);
        facade.logout();

        verify(mockedOrder).setReport(mockedReport, 17);
    }

    @Test
    public void getOrderLongDesc() {
        boolean thrown = false;
        try {
            facade.getOrderLongDesc(1);
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);

        setupLogin();
        doThrow(new AssertionError("Unexpected Logout Interaction")).when(AuthModule.class);
        AuthModule.logout(any());

        Order mockedOrder = mock(Order.class);
        when(mockedOrder.longDesc()).thenReturn("a long desc");

        when(mockedDB.getOrder(mockedToken, 1)).thenReturn(mockedOrder);

        assertEquals("a long desc", facade.getOrderLongDesc(1));
        verify(mockedOrder).longDesc();
    }

    @Test
    public void getOrderShortDesc() {
        boolean thrown = false;
        try {
            facade.getOrderShortDesc(1);
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);

        setupLogin();
        doThrow(new AssertionError("Unexpected Logout Interaction")).when(AuthModule.class);
        AuthModule.logout(any());

        Order mockedOrder = mock(Order.class);
        when(mockedOrder.shortDesc()).thenReturn("a short desc");

        when(mockedDB.getOrder(mockedToken, 1)).thenReturn(mockedOrder);

        assertEquals("a short desc", facade.getOrderShortDesc(1));
        verify(mockedOrder).shortDesc();
    }

    @Test
    public void getKnownContactMethods() {
        boolean thrown = false;
        try {
            facade.getKnownContactMethods();
        } catch (SecurityException ignored) {
            thrown = true;
        }

        assertTrue(thrown);

        setupLogin();
        doThrow(new AssertionError("Unexpected Logout Interaction")).when(AuthModule.class);
        AuthModule.logout(any());

        List<String> expected = Arrays.asList(
                "Carrier Pigeon",
                "Email",
                "Internal Accounting",
                "Mail",
                "Phone call",
                "SMS"
        );

        expected.sort(Comparator.naturalOrder());

        List<String> response = facade.getKnownContactMethods();

        response.sort(Comparator.naturalOrder());

        assertEquals(expected, response);
    }
}
