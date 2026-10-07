import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
 * Kutxa Beltzeko testak Mockito erabiliz datu-basea simulatzeko (IsFavorite).
 */
public class IsFavoriteMockBlackTest {

    static DataAccess sut;

    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;

    private String validEmail;
    private Registered registeredMock;
    private Sale validSale;

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

        validEmail = "testUser@ehu.eus";
        registeredMock = Mockito.mock(Registered.class);
        
        // Sale berria eraikitzaile eguneratuarekin
        validSale = new Sale("baloia", "deskribapena", 0, 10.5f, new Date(), null, null, false);
        validSale.setSaleNumber(1);
    }

    @After
    public void tearDown() {
        if (persistenceMock != null) {
            persistenceMock.close();
        }
    }

    @Test
    // Kutxa Beltza: Posta hutsa (null) - Baliokidetasun Klase Ez Egokia
    public void test1_PostaHutsa() {
        Mockito.when(db.find(Registered.class, null)).thenReturn(null);
        
        sut.open();
        boolean result = sut.isFavorite(null, validSale);
        sut.close();

        assertFalse(result);
    }

    @Test
    // Kutxa Beltza: Erabiltzailea ez dago DBan - Baliokidetasun Klase Ez Egokia
    public void test2_ErabiltzaileaEzDagoDBan() {
        String invalidEmail = "ezDaExistitzen@ehu.eus";
        Mockito.when(db.find(Registered.class, invalidEmail)).thenReturn(null);

        sut.open();
        boolean result = sut.isFavorite(invalidEmail, validSale);
        sut.close();

        assertFalse(result);
    }

    @Test
    // Kutxa Beltza: Sale objektua null da - Baliokidetasun Klase Ez Egokia
    public void test3_SaleNullDa() {
        Mockito.when(db.find(Registered.class, validEmail)).thenReturn(registeredMock);

        sut.open();
        boolean result = sut.isFavorite(validEmail, null);
        sut.close();

        assertFalse(result);
    }

    @Test
    // Kutxa Beltza: Sarrera egokiak, baina salmenta EZ dago gogokoetan - Klase Egokia
    public void test4_SarreraEgokiak_EzDagoGogokoetan() {
        List<Sale> favorites = new ArrayList<>(); // Lista hutsa
        Mockito.when(db.find(Registered.class, validEmail)).thenReturn(registeredMock);
        Mockito.when(registeredMock.getFavorites()).thenReturn(favorites);

        sut.open();
        boolean result = sut.isFavorite(validEmail, validSale);
        sut.close();

        assertFalse(result);
    }

    @Test
    // Kutxa Beltza: Sarrera egokiak eta salmenta GOGOKOETAN dago - Klase Egokia
    public void test5_SarreraEgokiak_GogokoetanDago() {
        List<Sale> favorites = new ArrayList<>();
        favorites.add(validSale); // Salmenta gogokoen listan dago
        Mockito.when(db.find(Registered.class, validEmail)).thenReturn(registeredMock);
        Mockito.when(registeredMock.getFavorites()).thenReturn(favorites);

        sut.open();
        boolean result = sut.isFavorite(validEmail, validSale);
        sut.close();

        assertTrue(result);
    }
    
}
