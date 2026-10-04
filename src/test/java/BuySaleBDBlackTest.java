
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

/**
 * Kutxa Beltzeko testak datu-base errealarekin (buySale).
 */
public class BuySaleBDBlackTest {

    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA = new TestDataAccess();

    private String validBuyerEmail;
    private String validSellerEmail;
    private Sale validSale;

    @Before
    public void defaultValues() {
        validBuyerEmail = "jon@ehu.eus";
        validSellerEmail = "ane@ehu.eus";
        validSale = new Sale("baloia", "deskribapena", 0, 10.0f, new Date(), null, null, false);
    }

    @Test
    //1. Sarrera egokiak, erosketa arrakastatsua
    public void test1_ErosketaEgokia() {
        testDA.open();
        testDA.createRegistered(validBuyerEmail, "Jon", 20.0f);
        testDA.createRegistered(validSellerEmail, "Ane", 10.0f);
        Sale dbSale = testDA.addSaleToSeller(validSellerEmail, validSale);
        testDA.close();

        try {
            sut.open();
            String result = sut.buySale(dbSale, validBuyerEmail);
            sut.close();

            assertNull(result);
        } finally {
            testDA.open();
            testDA.removeRegistered(validBuyerEmail);
            testDA.removeRegistered(validSellerEmail);
            testDA.close();
        }
    }

    @Test
    //2. BuyerEmail null da
    public void test2_BuyerEmailNull() {
        testDA.open();
        Sale dbSale = testDA.addSaleToSeller(validSellerEmail, validSale);
        testDA.close();

        try {
            sut.open();
            String result = sut.buySale(dbSale, null);
            sut.close();

            assertNotNull(result);
            assertTrue(result.contains("Error"));
        } finally {
            testDA.open();
            testDA.removeSale(dbSale);
            testDA.removeRegistered(validSellerEmail);
            testDA.close();
        }
    }

    @Test
    //3. Sale objektua null da
    public void test3_SaleNullDa() {
        testDA.open();
        testDA.createRegistered(validBuyerEmail, "Jon", 20.0f);
        testDA.close();

        try {
            sut.open();
            String result = sut.buySale(null, validBuyerEmail);
            sut.close();

            assertNotNull(result);
            assertTrue(result.contains("Error"));
        } finally {
            testDA.open();
            testDA.removeRegistered(validBuyerEmail);
            testDA.close();
        }
    }

    @Test
    //4. Eroslea ez dago datu-basean
    public void test4_ErosleaEzDagoDBan() {
        testDA.open();
        Sale dbSale = testDA.addSaleToSeller(validSellerEmail, validSale);
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
            testDA.removeRegistered(validSellerEmail);
            testDA.close();
        }
    }

    @Test
    //5. Salmenta ez dago datu-basean (edo ez da aurkitzen)
    public void test5_SaleEzDagoDBan() {
        testDA.open();
        testDA.createRegistered(validBuyerEmail, "Jon", 20.0f);
        testDA.close();

        try {
            sut.open();
            Sale fakeSale = new Sale("fake", "desc", 0, 10.0f, new Date(), null, null, false);
            fakeSale.setSaleNumber(99999);
            String result = sut.buySale(fakeSale, validBuyerEmail);
            sut.close();

            assertNotNull(result);
            assertTrue(result.contains("Error"));
        } finally {
            testDA.open();
            testDA.removeRegistered(validBuyerEmail);
            testDA.close();
        }
    }

    @Test
    //6. Salmentak ez du saltzaileari loturarik (user = null)
    public void test6_SaltzaileaNull() {
        testDA.open();
        testDA.createRegistered(validBuyerEmail, "Jon", 20.0f);
        // Salmenta bat sortu saltzaileari lotu gabe
        Sale orphanSale = new Sale("orph", "desc", 0, 10.0f, new Date(), null, null, false);
        testDA.persistSaleDirectly(orphanSale);
        testDA.close();

        try {
            sut.open();
            String result = sut.buySale(orphanSale, validBuyerEmail);
            sut.close();

            assertNull(result);
        } finally {
            testDA.open();
            testDA.removeSale(orphanSale);
            testDA.removeRegistered(validBuyerEmail);
            testDA.close();
        }
    }

    @Test
    //7. Autokompra saiakera (eroslea eta saltzailea berberak dira)
    public void test7_Autokompra() {
        testDA.open();
        testDA.createRegistered(validSellerEmail, "Ane", 50.0f);
        Sale dbSale = testDA.addSaleToSeller(validSellerEmail, validSale);
        testDA.close();

        try {
            sut.open();
            String result = sut.buySale(dbSale, validSellerEmail);
            sut.close();

            assertEquals("Can't buy own sale", result);
        } finally {
            testDA.open();
            testDA.removeSale(dbSale);
            testDA.removeRegistered(validSellerEmail);
            testDA.close();
        }
    }

    @Test
    //8. Erosleak ez du diru nahikorik
    public void test8_DiruNahikorikEz() {
        testDA.open();
        testDA.createRegistered(validBuyerEmail, "Jon", 5.0f); // Saldoa gutxiago prezioa baino (10.0)
        testDA.createRegistered(validSellerEmail, "Ane", 10.0f);
        Sale dbSale = testDA.addSaleToSeller(validSellerEmail, validSale);
        testDA.close();

        try {
            sut.open();
            String result = sut.buySale(dbSale, validBuyerEmail);
            sut.close();

            assertEquals("Not enough money", result);
        } finally {
            testDA.open();
            testDA.removeRegistered(validBuyerEmail);
            testDA.removeRegistered(validSellerEmail);
            testDA.close();
        }
    }

    @Test
    //9. Salbuespena try blokean (DB konexio/irakurketa errorea simulatzeko parametro baliogabearekin)
    public void test9_ExceptionDB() {
        testDA.open();
        testDA.createRegistered(validBuyerEmail, "Jon", 20.0f);
        testDA.close();

        try {
            sut.open();
            // Pasatuz sale bat curos linean errorea emateko
            String result = sut.buySale(null, validBuyerEmail);
            sut.close();

            assertNotNull(result);
            assertTrue(result.contains("Error"));
        } finally {
            testDA.open();
            testDA.removeRegistered(validBuyerEmail);
            testDA.close();
        }
    }
}