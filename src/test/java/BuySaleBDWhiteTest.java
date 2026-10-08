import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Sale;
import testOperations.TestDataAccess;

public class BuySaleBDWhiteTest {

    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA = new TestDataAccess();

    private String buyerEmail;
    private String sellerEmail;
    private Sale validSale;

    @Before
    public void defaultValues() {
        buyerEmail = "jon@ehu.eus";
        sellerEmail = "ane@ehu.eus";
        validSale = new Sale("baloia", "deskribapena", 0, 10.0f, new Date(), null, null, false);
    }

    @Test
    public void test1_DenaZuzena() {
        testDA.open();
        testDA.createRegistered(buyerEmail, "Jon", 20.0f); 
        testDA.createRegistered(sellerEmail, "Ane", 10.0f);
        Sale dbSale = testDA.addSaleToSeller(sellerEmail, validSale);
        testDA.close();

        try {
            sut.open();
            String result = sut.buySale(dbSale, buyerEmail);
            sut.close();

            assertNull(result);
        } finally {
            testDA.open();
            testDA.removeRegistered(buyerEmail);
            testDA.removeRegistered(sellerEmail);
            testDA.close();
        }
    }

    @Test
    public void test2_ErosleaEzDago() {
        testDA.open();
        Sale dbSale = testDA.addSaleToSeller(sellerEmail, validSale);
        testDA.close();

        try {
            sut.open();
            String result = sut.buySale(dbSale, "ezDago@ehu.eus");
            sut.close();

            assertNotNull(result);
            assertTrue(result.contains("Error"));
        } finally {
            testDA.open();
            testDA.removeSale(dbSale);
            testDA.removeRegistered(sellerEmail);
            testDA.close();
        }
    }

    @Test
    public void test3_SalmentaEzDago() {
        testDA.open();
        testDA.createRegistered(buyerEmail, "Jon", 20.0f);
        testDA.close();

        try {
            sut.open();
            Sale fakeSale = new Sale("fake", "desc", 0, 10.0f, new Date(), null, null, false);
            fakeSale.setSaleNumber(99999);
            String result = sut.buySale(fakeSale, buyerEmail);
            sut.close();

            assertNotNull(result);
            assertTrue(result.contains("Error"));
        } finally {
            testDA.open();
            testDA.removeRegistered(buyerEmail);
            testDA.close();
        }
    }

    @Test
    public void test4_AutokompraSaiakera() {
        testDA.open();
        testDA.createRegistered(sellerEmail, "Ane", 50.0f);
        Sale dbSale = testDA.addSaleToSeller(sellerEmail, validSale);
        testDA.close();

        try {
            sut.open();
            String result = sut.buySale(dbSale, sellerEmail);
            sut.close();

            assertEquals("Can't buy own sale", result);
        } finally {
            testDA.open();
            testDA.removeSale(dbSale);
            testDA.removeRegistered(sellerEmail);
            testDA.close();
        }
    }

    @Test
    public void test5_DiruNahikorikEz() {
        testDA.open();
        testDA.createRegistered(buyerEmail, "Jon", 5.0f);
        testDA.createRegistered(sellerEmail, "Ane", 10.0f);
        Sale dbSale = testDA.addSaleToSeller(sellerEmail, validSale);
        testDA.close();

        try {
            sut.open();
            String result = sut.buySale(dbSale, buyerEmail);
            sut.close();

            assertEquals("Not enough money", result);
        } finally {
            testDA.open();
            testDA.removeRegistered(buyerEmail);
            testDA.removeRegistered(sellerEmail);
            testDA.close();
        }
    }

    @Test
    public void test6_SalbuespenaEtaRollback() {
        testDA.open();
        testDA.createRegistered(buyerEmail, "Jon", 20.0f);
        testDA.close();

        try {
            sut.open();
            String result = sut.buySale(null, buyerEmail);
            sut.close();

            assertNotNull(result);
            assertTrue(result.contains("Error"));
        } finally {
            testDA.open();
            testDA.removeRegistered(buyerEmail);
            testDA.close();
        }
    }
}