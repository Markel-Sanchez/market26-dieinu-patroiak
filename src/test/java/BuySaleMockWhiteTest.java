
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
 * Kutxa Txuriko testak Mockito erabiliz datu-basea simulatzeko (buySale).
 */
public class BuySaleMockWhiteTest {

    static DataAccess sut;

    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;

    private String buyerEmail;
    private Registered buyerMock;
    private Registered sellerMock;
    private Sale validSale;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(entityManagerFactory);

        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();
        sut = new DataAccess();

        buyerEmail = "jon@ehu.eus";
        buyerMock = Mockito.mock(Registered.class);
        sellerMock = Mockito.mock(Registered.class);

        validSale = new Sale("baloia", "deskribapena", 0, 10.0f, new Date(), null, null, false);
        validSale.setSaleNumber(1);
        validSale.setUser(sellerMock);
    }

    @After
    public void tearDown() {
    	persistenceMock.close();
    }

    @Test
    //Transakzioa jada aktibo dago (`if (!db.getTransaction().isActive())` falsua da)
    public void test1_TransakzioaAktiboDago() {
        Mockito.when(et.isActive()).thenReturn(true);
        Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);
        Mockito.when(buyerMock.getMoneyKop()).thenReturn(50.0f);
        Mockito.when(sellerMock.getEmail()).thenReturn("ane@ehu.eus");
        Mockito.when(buyerMock.getEmail()).thenReturn(buyerEmail);

        sut.open();
        String result = sut.buySale(validSale, buyerEmail);
        sut.close();

        assertNull(result);
        Mockito.verify(et, Mockito.never()).begin(); // Ez da begin() deitzen berriro jada aktibo zegoelako
    }

    @Test
    //Eroslea null da (if konposatua ez da betetzen)
    public void test2_ErosleaNullDa() {
        Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(null);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);

        sut.open();
        String result = sut.buySale(validSale, buyerEmail);
        sut.close();

        assertNull(result);
    }

    @Test
    //Autokompra gertatzen da (`buyer.getEmail().equals(seller.getEmail())`)
    public void test3_Autokompra() {
        Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);
        Mockito.when(buyerMock.getEmail()).thenReturn(buyerEmail);
        Mockito.when(sellerMock.getEmail()).thenReturn(buyerEmail);

        sut.open();
        String result = sut.buySale(validSale, buyerEmail);
        sut.close();

        assertEquals("Can't buy own sale", result);
    }

    @Test
    //Saldoa ez da nahikoa (`buyer.getMoneyKop() < price`)
    public void test4_SaldoaEzDaNahikoa() {
        Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);
        Mockito.when(buyerMock.getEmail()).thenReturn(buyerEmail);
        Mockito.when(sellerMock.getEmail()).thenReturn("ane@ehu.eus");
        Mockito.when(buyerMock.getMoneyKop()).thenReturn(5.0f); // Prezioa 10.0 da

        sut.open();
        String result = sut.buySale(validSale, buyerEmail);
        sut.close();

        assertEquals("Not enough money", result);
    }

    @Test
    //Gogokoen zerrenda null da (`buyer.getFavorites() != null` falsua da)
    public void test5_FavoritesNullDago() {
        Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);
        Mockito.when(buyerMock.getMoneyKop()).thenReturn(50.0f);
        Mockito.when(sellerMock.getEmail()).thenReturn("ane@ehu.eus");
        Mockito.when(buyerMock.getEmail()).thenReturn(buyerEmail);
        Mockito.when(buyerMock.getFavorites()).thenReturn(null); // Zerrenda null da

        sut.open();
        String result = sut.buySale(validSale, buyerEmail);
        sut.close();

        assertNull(result);
    }

    @Test
    //Salbuespena eta rollback kudeaketa (`catch (Exception e)`)
    public void test6_SalbuespenaEtaRollback() {
        Mockito.when(et.isActive()).thenReturn(false).thenReturn(true); // Hasieran ez, catch-ean bai
        Mockito.when(db.find(eq(Registered.class), any())).thenThrow(new RuntimeException("Simulated DB Crash"));

        sut.open();
        String result = sut.buySale(validSale, buyerEmail);
        sut.close();

        assertNotNull(result);
        assertTrue(result.contains("Error"));
        Mockito.verify(et, Mockito.times(1)).rollback(); // Ziurtatzen dugu rollback exekutatu dela
    }
}