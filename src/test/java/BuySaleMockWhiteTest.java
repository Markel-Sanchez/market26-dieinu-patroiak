import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

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
    private String sellerEmail;
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
        sellerEmail = "ane@ehu.eus";
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
    public void test1_DenaZuzena() {
        Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);
        Mockito.when(buyerMock.getMoneyKop()).thenReturn(50.0f);
        Mockito.when(sellerMock.getEmail()).thenReturn(sellerEmail);
        Mockito.when(buyerMock.getEmail()).thenReturn(buyerEmail);

        sut.open();
        String result = sut.buySale(validSale, buyerEmail);
        sut.close();

        assertNull(result);
    }

    @Test
    public void test2_ErosleaEzDago() {
        Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(null);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);

        sut.open();
        String result = sut.buySale(validSale, buyerEmail);
        sut.close();

        assertNull(result);
    }

    @Test
    public void test3_SalmentaEzDago() {
        Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(null);

        sut.open();
        String result = sut.buySale(validSale, buyerEmail);
        sut.close();

        assertNotNull(result);
        assertTrue(result.contains("Error"));
    }

    @Test
    public void test4_AutokompraSaiakera() {
        Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);
        Mockito.when(buyerMock.getEmail()).thenReturn(buyerEmail);
        Mockito.when(sellerMock.getEmail()).thenReturn(buyerEmail); // Mismo email (autocompra)

        sut.open();
        String result = sut.buySale(validSale, buyerEmail);
        sut.close();

        assertEquals("Can't buy own sale", result);
    }

    @Test
    public void test5_DiruNahikorikEz() {
        Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);
        Mockito.when(buyerMock.getEmail()).thenReturn(buyerEmail);
        Mockito.when(sellerMock.getEmail()).thenReturn(sellerEmail);
        Mockito.when(buyerMock.getMoneyKop()).thenReturn(5.0f); // Menor que el precio (10.0)

        sut.open();
        String result = sut.buySale(validSale, buyerEmail);
        sut.close();

        assertEquals("Not enough money", result);
    }

    @Test
    public void test6_SalbuespenaEtaRollback() {
        Mockito.when(et.isActive()).thenReturn(false).thenReturn(true);
        Mockito.when(db.find(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException("Simulated Crash"));

        sut.open();
        String result = sut.buySale(validSale, buyerEmail);
        sut.close();

        assertNotNull(result);
        assertTrue(result.contains("Error"));
    }
}