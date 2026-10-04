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
import testOperations.TestDataAccess;

/**
 * Kutxa Beltzeko testak Mockito erabiliz datu-basea simulatzeko (buyCart).
 */

public class BuyCartMockBlackTest {
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
	// 1. KASUA: Erosketa egokia
	// Espero den irteera: true
	public void test1_ErosketaEgokia() {
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
	// 2. KASUA: Erosketa egiteko, diru nahikorik ez
	// Espero den irteera: false
	public void test2_DiruNahikoaEz() {
		buyerMock.setMoneyKop(0f);

		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, cart1);
		sut.close();

		assertFalse(result);
	}

	@Test
	// 3. KASUA: Saskiko sale-a jadanik salduta dago
	// Espero den irteera: false
	public void test3_SaleSaldutaDago() {
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
	// 4. KASUA: buyerEmail NULL izatea
	// Espero den irteera: false
	public void test4_BuyerEmailNull() {

		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(null);

		sut.open();
		boolean result = sut.buyCart(null, cart1);
		sut.close();

		assertFalse(result);
	}

	@Test
	// 5. KASUA: buyerEmail formato desegokia edukitzea
	// Espero den irteera: false
	public void test5_BuyerEmailFormatoDesdegokia() {

		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(null);

		sut.open();
		boolean result = sut.buyCart("assd", cart1);
		sut.close();

		assertFalse(result);
	}

	@Test
	// 6. KASUA: buyerEmail DBan ez egotea
	// Espero den irteera: false
	public void test6_BuyerEmailDBEz() {

		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(null);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, cart1);
		sut.close();

		assertFalse(result);
	}

	@Test
	// 7. KASUA: cart NULL izatea
	// Espero den irteera: false
	public void test7_CartNull() {

		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, null);
		sut.close();

		assertFalse(result);
	}

	@Test
	// 8. KASUA: Saskia hutsik egotea
	// Espero den irteera: false
	public void test8_saskiaHutsik() {
		buyerMock.setMoneyKop(50f);
		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, cart2);
		sut.close();

		assertTrue(result);
	}

	@Test
	// 9. KASUA: Saskiko sale-a ez dago DBan
	// Espero den irteera: false
	public void test9_SaleEzDagoDB() {
		buyerMock.setMoneyKop(50f);

		Mockito.when(db.find(Registered.class, buyerEmail)).thenReturn(buyerMock);
		Mockito.when(db.find(Sale.class, validSale.getSaleNumber())).thenReturn(null);

		sut.open();
		boolean result = sut.buyCart(buyerEmail, cart1);
		sut.close();

		assertFalse(result);
	}
}
