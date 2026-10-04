
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

/**
 * Kutxa Beltzeko testak Mockito erabiliz datu-basea simulatzeko (buySale).
 */
public class BuySaleMockBlackTest {

    static DataAccess sut;

    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;

    private String validBuyerEmail;
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

        validBuyerEmail = "jon@ehu.eus";
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
    //Sarrera egokiak, erosketa arrakastatsua
    public void test1_ErosketaEgokia() {
        Mockito.when(db.find(Registered.class, validBuyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);
        Mockito.when(buyerMock.getMoneyKop()).thenReturn(50.0f);
        Mockito.when(sellerMock.getEmail()).thenReturn("ane@ehu.eus");
        Mockito.when(buyerMock.getEmail()).thenReturn(validBuyerEmail);

        sut.open();
        String result = sut.buySale(validSale, validBuyerEmail);
        sut.close();

        assertNull(result);
    }

    @Test
    //BuyerEmail null da
    public void test2_BuyerEmailNull() {
        Mockito.when(db.find(Registered.class, null)).thenReturn(null);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);

        sut.open();
        String result = sut.buySale(validSale, null);
        sut.close();

        assertNull(result);
    }

    @Test
    //Sale objektua null da (lanzará error interno por s.getUser())
    public void test3_SaleNullDa() {
        Mockito.when(db.find(Registered.class, validBuyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, null)).thenReturn(null);

        sut.open();
        String result = sut.buySale(null, validBuyerEmail);
        sut.close();

        assertNotNull(result);
        assertTrue(result.contains("Error"));
    }

    @Test
    //Eroslea ez dago DBan
    public void test4_ErosleaEzDagoDBan() {
        String invalidEmail = "ezDago@ehu.eus";
        Mockito.when(db.find(Registered.class, invalidEmail)).thenReturn(null);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);

        sut.open();
        String result = sut.buySale(validSale, invalidEmail);
        sut.close();

        assertNull(result);
    }

    @Test
    //Salmenta ez dago DBan
    public void test5_SaleEzDagoDBan() {
        Mockito.when(db.find(Registered.class, validBuyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(null);

        sut.open();
        String result = sut.buySale(validSale, validBuyerEmail);
        sut.close();

        assertNotNull(result);
        assertTrue(result.contains("Error"));
    }

    @Test
    //Saltzailea null da
    public void test6_SaltzaileaNull() {
        validSale.setUser(null);
        Mockito.when(db.find(Registered.class, validBuyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);

        sut.open();
        String result = sut.buySale(validSale, validBuyerEmail);
        sut.close();

        assertNull(result);
    }

    @Test
    //Autokompra saiakera
    public void test7_Autokompra() {
        Mockito.when(db.find(Registered.class, validBuyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);
        Mockito.when(buyerMock.getEmail()).thenReturn(validBuyerEmail);
        Mockito.when(sellerMock.getEmail()).thenReturn(validBuyerEmail); // Mismo email

        sut.open();
        String result = sut.buySale(validSale, validBuyerEmail);
        sut.close();

        assertEquals("Can't buy own sale", result);
    }

    @Test
    //Diru nahikorik ez
    public void test8_DiruNahikorikEz() {
        Mockito.when(db.find(Registered.class, validBuyerEmail)).thenReturn(buyerMock);
        Mockito.when(db.find(Sale.class, 1)).thenReturn(validSale);
        Mockito.when(buyerMock.getEmail()).thenReturn(validBuyerEmail);
        Mockito.when(sellerMock.getEmail()).thenReturn("ane@ehu.eus");
        Mockito.when(buyerMock.getMoneyKop()).thenReturn(2.0f); // Precio es 10.0

        sut.open();
        String result = sut.buySale(validSale, validBuyerEmail);
        sut.close();

        assertEquals("Not enough money", result);
    }

    @Test
    //Salbuespena try blokean
    public void test9_ExceptionMock() {
        Mockito.when(db.find(Registered.class, validBuyerEmail)).thenThrow(new RuntimeException("Mock DB Error"));

        sut.open();
        String result = sut.buySale(validSale, validBuyerEmail);
        sut.close();

        assertNotNull(result);
        assertTrue(result.contains("Error"));
    }
}