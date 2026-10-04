
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
 * Kutxa Txuriko testak datu-base errealarekin (buySale).
 */
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
    //Bidea 1: Dena zuzena, transakzioa, saldoak, gogokoen zerrenda ez da null
    public void test1_Bidea_DenaZuzenaFavoritesEguneratua() {
        testDA.open();
        testDA.createRegistered(buyerEmail, "Jon", 20.0f); 
        testDA.createRegistered(sellerEmail, "Ane", 10.0f);
        Sale dbSale = testDA.addSaleToSeller(sellerEmail, validSale);
        testDA.addSaleToFavorites(buyerEmail, dbSale);
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
    //Bidea 2: Eroslea ez dago DBan (buyer == null)
    public void test2_Bidea_ErosleaEzDagoDBan() {
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
    //Bidea 3: Salmenta ez dago DBan (s == null)
    public void test3_Bidea_SalmentaEzDagoDBan() {
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
    //Bidea 4: Salmentak ez du saltzaileari loturarik (seller == null)
    public void test4_Bidea_SalmentakEzDuSaltzaileariLoturarik() {
        testDA.open();
        testDA.createRegistered(buyerEmail, "Jon", 20.0f);
        Sale orphanSale = new Sale("orph", "desc", 0, 10.0f, new Date(), null, null, false);
        testDA.persistSaleDirectly(orphanSale);
        testDA.close();

        try {
            sut.open();
            String result = sut.buySale(orphanSale, buyerEmail);
            sut.close();

            assertNull(result);
        } finally {
            testDA.open();
            testDA.removeSale(orphanSale);
            testDA.removeRegistered(buyerEmail);
            testDA.close();
        }
    }

    @Test
    //Bidea 5: Autokompra saiakera (buyer.getEmail().equals(seller.getEmail()))
    public void test5_Bidea_AutokompraSaiakera() {
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
    //Bidea 6: Diru nahikorik ez (buyer.getMoneyKop() < price)
    public void test6_Bidea_DiruNahikorikEz() {
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
    //Bidea 7: Gogokoen zerrenda null da
    public void test7_Bidea_FavoritesNullDago() {
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
    //Bidea 9 eta 10: Salbuespena gertatzen da try blokean (Rollback kudeaketa)
    public void test9_10_Bidea_SalbuespenaEtaRollback() {
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