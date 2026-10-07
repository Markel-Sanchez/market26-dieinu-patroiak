import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Registered;
import domain.Sale;
import testOperations.TestDataAccess;

public class IsFavoriteBDWhiteTest {

    // SUT: System Under Test
    static DataAccess sut = new DataAccess();

    // Testak exekutatzeko beharrezkoak diren eragiketa gehigarriak
    static TestDataAccess testDA = new TestDataAccess();

    private String userEmail;
    private Sale sale;

    @Before
    public void defaultValues() {
        userEmail = "testUser@ehu.eus";
        // Salmenta bat sortu
        sale = new Sale("baloia", "deskribapena", 0, 10.5f, new Date(), null, null, false);
    }

    @Test
    // Kutxa Txuria: 1. Bidea - u == null (Erabiltzailea ez dago DBan)
    public void test1_ErabiltzaileaEzDaExistitzen() {
        String fakeEmail = "fakeUser@ehu.eus";
        
        sut.open();
        boolean result = sut.isFavorite(fakeEmail, sale);
        sut.close();

        // Emaitza false izan behar da erabiltzailea ez delako aurkitu
        assertFalse(result);
    }

    @Test
    // Kutxa Txuria: 2. Bidea - s == null (Salmenta null da)
    public void test2_SalmentaNullDa() {
        // Erabiltzailea DBan sortu
        testDA.open();
        testDA.createRegistered(userEmail, "Test User"); // OHARRA: TestDataAccess-en inplementatu behar da
        testDA.close();

        try {
            sut.open();
            boolean result = sut.isFavorite(userEmail, null);
            sut.close();

            // Emaitza false izan behar da salmenta null delako
            assertFalse(result);
        } finally {
            // DB garbitu
            testDA.open();
            testDA.removeRegistered(userEmail); // OHARRA: TestDataAccess-en inplementatu behar da
            testDA.close();
        }
    }

    @Test
    // Kutxa Txuria: 3. Bidea - Erabiltzailea existitzen da, salmenta ez da null, baina EZ dago gogokoetan
    public void test3_EzDagoGogokoetan() {
        testDA.open();
        testDA.createRegistered(userEmail, "Test User");
        testDA.close();

        try {
            sut.open();
            boolean result = sut.isFavorite(userEmail, sale);
            sut.close();

            // Emaitza false izan behar da iterazioa amaitzen delako aurkitu gabe
            assertFalse(result);
        } finally {
            testDA.open();
            testDA.removeRegistered(userEmail);
            testDA.close();
        }
    }

    @Test
    // Kutxa Txuria: 4. Bidea - Erabiltzailea existitzen da, salmenta ez da null, eta GOGOKOETAN dago
    public void test4_GogokoetanDago() {
        testDA.open();
        testDA.createRegistered(userEmail, "Test User");
        testDA.addSaleToFavorites(userEmail, sale); // OHARRA: TestDataAccess-en inplementatu behar da
        testDA.close();

        try {
            sut.open();
            boolean result = sut.isFavorite(userEmail, sale);
            sut.close();

            // Emaitza true izan behar da barruko if-a betetzen delako
            assertTrue(result);
        } finally {
            testDA.open();
            testDA.removeRegistered(userEmail);
            testDA.close();
        }
    }
    
}
