import static org.junit.Assert.assertFalse;
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
 * Kutxa Txuriko testak Mockito erabiliz datu-basea simulatzeko (buyCart).
 */

public class BuyCartMockWhiteTest {
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
	private List<Sale> cart1;
	private List<Sale> cart2;

	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);
		persistenceMock = Mockito.mockStatic(Persistence.class);
		persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
				.thenReturn(entityManagerFactory);

		Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
		Mockito.doReturn(et).when(db).getTransaction();
		sut = new DataAccess();

		buyerEmail = "a@ehu.eus";
		buyerMock = new Registered(buyerEmail, "Jon Test", "password123", 0f);
		sellerMock = new Registered("seller@ehu.eus", "Saltzailea", "password123", 0f);

		validSale = new Sale("baloia", "deskribapena", 0, 10.0f, new Date(), null, null, false);
		validSale.setSaleNumber(1);
		validSale.setUser(sellerMock);

		// Saskia sale batekin
		cart1 = new ArrayList<Sale>();
		cart1.add(validSale);
		// Saskia hutsik
		cart2 = new ArrayList<Sale>();
	}

	@After
	public void tearDown() {
		persistenceMock.close();
	}

	@Test
	// 1. BIDEA: Transakzioa aktiboa dago (if (db.getTransaction().isActive()) -> TRUE itzultzen du)
	// Espero den irteera: false
	public void test1_ExceptionTransakzioaAktibo() {
		Mockito.when(et.isActive()).thenReturn(true);
		Mockito.when(db.find(eq(Registered.class), any())).thenThrow(new RuntimeException("Simulated DB Crash"));
		sut.open();
		boolean result = sut.buyCart(buyerEmail, null);
		sut.close();

		assertFalse(result);
	}

	@Test
	// 2. BIDEA: Transakzioa ez dago aktiboa (if (db.getTransaction().isActive()) -> FALSE itzultzen du)
	// Espero den irteera: false
	public void test2_ExceptionTransakzioaEzAktibo() {
		Mockito.when(et.isActive()).thenReturn(false); 
		Mockito.when(db.find(eq(Registered.class), any())).thenThrow(new RuntimeException("Simulated DB Crash"));

		sut.open();
		boolean result = sut.buyCart(buyerEmail, null);
		sut.close();

		assertFalse(result);
	}

	@Test
	// 3. BIDEA: BuyerEmail ez dago DBan
	// Espero den irteera: false
	public void test3_ErosleaEzDagoDB() {
		String buyerEmail = "ez@dago.com";
		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(null);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, cart1);
		sut.close();

		assertFalse(result);
	}

	@Test
	// 4. BIDEA: cart hutsik eta buyerEmail saldo negatiboa du
	// Espero den irteera: false
	public void test4_cartHutsikBuyerEmailSaldoNegatiboa() {
		buyerMock.setMoneyKop(-10.f);
		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, cart2);
		sut.close();

		assertFalse(result);
	}

	@Test
	// 5. BIDEA: buyerEmail DBan dago, baina ez du diru nahiko produktuak erosteko (buyerEmail.moneyKop= 0, S1.price = 10)
	// Espero den irteera: false
	public void test5_DiruNahikoEz() {
		buyerMock.setMoneyKop(0f);

		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, cart1);
		sut.close();

		assertFalse(result);
	}

	@Test
	// 6. BIDEA: buyerEmail DBan dago, baina saskia hutsik dago eta buyerEmail saldo positiboa du
	// Espero den irteera: true
	public void test6_SaskiaHutsik() {
		buyerMock.setMoneyKop(50f);

		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, cart2);
		sut.close();

		assertTrue(result);
	}

	@Test
	// 7. BIDEA: buyerEmail DBan dago, baina saskiko sale-a ez dago DBan
	// Espero den irteera: false
	public void test7_SaleEzDagoDB() {
		buyerMock.setMoneyKop(50f);

		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
		Mockito.when(db.find(Sale.class, validSale.getSaleNumber())).thenReturn(null);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, cart1);
		sut.close();

		assertFalse(result);
	}

	@Test
	// 8. BIDEA: buyerEmail DBan dago, S1 DBan dago, baina jada salduta dago (isSold = true).
	// Espero den irteera: false.
	public void test8_SaleSaldutaDago() {
		buyerMock.setMoneyKop(50f);
		validSale.setSold(true);

		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
		Mockito.when(db.find(Sale.class, validSale.getSaleNumber())).thenReturn(validSale);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, cart1);
		sut.close();

		assertFalse(result);
	}

	@Test
	// 9. BIDEA: buyerEmail DBan, S1 DBan (isSold = false) eta buyerEmail ez ditu sale fabortioak.
	// Espero den irteera: true.
	public void test9_SaleErosiFaboritoaEz() {
		buyerMock.setMoneyKop(50f);
		validSale.setSold(false);

		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
		Mockito.when(db.find(Sale.class, validSale.getSaleNumber())).thenReturn(validSale);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, cart1);
		sut.close();

		assertTrue(result);
	}

	@Test
	// 10. BIDEA: buyerEmail DBan, S1 DBan (isSold = false) eta buyerEmail sale-a faboritoetan du.
	// Espero den irteera: true
	public void test10_SaleErosiFaboritoaBai() {
		buyerMock.setMoneyKop(50f);
		validSale.setSold(false);
		buyerMock.addFavorite(validSale);

		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
		Mockito.when(db.find(Sale.class, validSale.getSaleNumber())).thenReturn(validSale);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, cart1);
		sut.close();

		assertTrue(result);
	}
}
