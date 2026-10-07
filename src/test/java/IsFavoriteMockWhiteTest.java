import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dataAccess.DataAccess;
import domain.Registered;
import domain.Sale;

/**
 * Kutxa Txuriko testak Mockito erabiliz datu-basea simulatzeko (IsFavorite).
 */
public class IsFavoriteMockWhiteTest {

    static DataAccess sut;

    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;

    private String userEmail;
    private Registered registeredMock;
    private Sale sale;

    /**
     * Test bakoitzaren aurretik Mock-ak hasieratzen dira.
     */
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(entityManagerFactory);

        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();
        sut = new DataAccess();

        userEmail = "testUser@ehu.eus";
        registeredMock = Mockito.mock(Registered.class);
        
        // Sale berria eraikitzaile eguneratuarekin
        sale = new Sale("futbol baloia", "oso polita", 0, 10.5f, new Date(), null, null, false);
        sale.setSaleNumber(1);
    }

    @After
    public void tearDown() {
        if (persistenceMock != null) {
            persistenceMock.close();
        }
    }

    @Test
    // Kutxa Txuria: 1. Bidea - u == null (Erabiltzailea ez da aurkitu DBan)
    public void test1_ErabiltzaileaEzDaExistitzen() {
        Mockito.when(db.find(Registered.class, userEmail)).thenReturn(null);

        sut.open();
        boolean result = sut.isFavorite(userEmail, sale);
        sut.close();

        assertFalse(result);
    }

    @Test
    // Kutxa Txuria: 2. Bidea - s == null (Salmenta null da)
    public void test2_SalmentaNullDa() {
        Mockito.when(db.find(Registered.class, userEmail)).thenReturn(registeredMock);

        sut.open();
        boolean result = sut.isFavorite(userEmail, null);
        sut.close();

        assertFalse(result);
    }

    @Test
    // Kutxa Txuria: 3. Bidea - EZ dago gogokoetan (for begizta osorik egiten da)
    public void test3_EzDagoGogokoetan() {
        List<Sale> favorites = new ArrayList<>();
        // Salmenta ezberdin bat sartuko dugu listan probatzeko
        Sale otherSale = new Sale("beste bat", "deskribapena", 0, 5f, new Date(), null, null, false);
        otherSale.setSaleNumber(99);
        favorites.add(otherSale);
        
        Mockito.when(db.find(Registered.class, userEmail)).thenReturn(registeredMock);
        Mockito.when(registeredMock.getFavorites()).thenReturn(favorites);

        sut.open();
        boolean result = sut.isFavorite(userEmail, sale);
        sut.close();

        assertFalse(result);
    }

    @Test
    // Kutxa Txuria: 4. Bidea - GOGOKOETAN dago (if baldintza betetzen da begizta barruan)
    public void test4_GogokoetanDago() {
        List<Sale> favorites = new ArrayList<>();
        favorites.add(sale); // Salmenta bera gehitzen dugu
        
        Mockito.when(db.find(Registered.class, userEmail)).thenReturn(registeredMock);
        Mockito.when(registeredMock.getFavorites()).thenReturn(favorites);

        sut.open();
        boolean result = sut.isFavorite(userEmail, sale);
        sut.close();

        assertTrue(result);
    }

    @Test
    // Kutxa Txuria: 5. Bidea - catch (Exception e) blokea exekutatzen da
    public void test5_SalbuespenaHarrapatu() {
        // DBko arazo bat simulatzen dugu find metodoan salbuespena jaurtiz
        Mockito.when(db.find(eq(Registered.class), any())).thenThrow(new IllegalArgumentException("DB Error"));

        sut.open();
        boolean result = sut.isFavorite(userEmail, sale);
        sut.close();

        // Catch blokeak false itzuli behar du
        assertFalse(result);
    }
    
}
