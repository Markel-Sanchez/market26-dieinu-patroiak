import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Sale;
import testOperations.TestDataAccess;

/**
 * Kutxa Beltzeko testak datu-base errealarekin (IsFavorite).
 */
public class IsFavoriteBDBlackTest {

    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA = new TestDataAccess();

    private String validEmail;
    private Sale validSale;

    /**
     * Test bakoitzaren aurretik exekutatzen den hasieraketa.
     */
    @Before
    public void defaultValues() {
        validEmail = "testUser@ehu.eus";
        // Sale berria eraikitzaile eguneratuarekin
        validSale = new Sale("baloia", "deskribapena", 0, 10.5f, new Date(), null, null, false);
    }

    @Test
    // Kutxa Beltza: Posta hutsa da (null edo string hutsa) - Baliokidetasun Klase Ez Egokia
    public void test1_PostaHutsa() {
        sut.open();
        // email parametroa null
        boolean result1 = sut.isFavorite(null, validSale);
        // email parametroa hutsik
        boolean result2 = sut.isFavorite("", validSale);
        sut.close();

        assertFalse(result1);
        assertFalse(result2);
    }

    @Test
    // Kutxa Beltza: Erabiltzailea ez dago DBan - Baliokidetasun Klase Ez Egokia
    public void test2_ErabiltzaileaEzDagoDBan() {
        sut.open();
        boolean result = sut.isFavorite("ezDaExistitzen@ehu.eus", validSale);
        sut.close();

        assertFalse(result);
    }

    @Test
    // Kutxa Beltza: Sale objektua null da - Baliokidetasun Klase Ez Egokia
    public void test3_SaleNullDa() {
        testDA.open();
        testDA.createRegistered(validEmail, "Test User");
        testDA.close();

        try {
            sut.open();
            boolean result = sut.isFavorite(validEmail, null);
            sut.close();

            assertFalse(result);
        } finally {
            testDA.open();
            testDA.removeRegistered(validEmail);
            testDA.close();
        }
    }

    @Test
    // Kutxa Beltza: Sarrera egokiak, baina salmenta EZ dago gogokoetan - Klase Egokia
    public void test4_SarreraEgokiak_EzDagoGogokoetan() {
        testDA.open();
        testDA.createRegistered(validEmail, "Test User");
        testDA.close();

        try {
            sut.open();
            boolean result = sut.isFavorite(validEmail, validSale);
            sut.close();

            assertFalse(result);
        } finally {
            testDA.open();
            testDA.removeRegistered(validEmail);
            testDA.close();
        }
    }

    @Test
    // Kutxa Beltza: Sarrera egokiak eta salmenta GOGOKOETAN dago - Klase Egokia
    public void test5_SarreraEgokiak_GogokoetanDago() {
        testDA.open();
        testDA.createRegistered(validEmail, "Test User");
        testDA.addSaleToFavorites(validEmail, validSale);
        testDA.close();

        try {
            sut.open();
            boolean result = sut.isFavorite(validEmail, validSale);
            sut.close();

            assertTrue(result);
        } finally {
            testDA.open();
            testDA.removeRegistered(validEmail);
            testDA.close();
        }
    }
    
}
