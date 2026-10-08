import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import dataAccess.DataAccess;
import domain.Registered;
import domain.Sale;
import testOperations.TestDataAccess;

/**
 * Kutxa Beltzeko testak DB-a erabiliz(buyCart).
 */

public class BuyCartBDBlackTest {
	static DataAccess sut = new DataAccess();
	static TestDataAccess testDA = new TestDataAccess();

	private String buyerEmail;
	private String sellerEmail;
	private Sale validSale;
	private List<Sale> cart1;
	private List<Sale> cart2;

	@Before
	public void defaultValues() {
		buyerEmail = "a@ehu.eus";
		sellerEmail = "ane@ehu.eus";
		validSale = new Sale("baloia", "deskribapena", 0, 10.0f, new Date(), null, null, false);

		// Saskia sale batekin
		cart1 = new ArrayList<Sale>();
		cart1.add(validSale);
		// Saskia hutsik
		cart2 = new ArrayList<Sale>();
	}

	@Test
	// 1. KASUA: Erosketa egokia
	// Espero den irteera: true
	public void test1_ErosketaEgokia() {
		validSale.setSold(false);
		testDA.open();
		testDA.createRegistered(buyerEmail, "Eroslea", 50f);
		testDA.createRegistered(sellerEmail, "Saltzailea", 0f);
		Sale dbSale = testDA.addSaleToSeller(sellerEmail, validSale);
		testDA.close();

		List<Sale> cart3 = new ArrayList<>();
		cart3.add(dbSale);

		try {
			sut.open();
			boolean result = sut.buyCart(buyerEmail, cart3);
			sut.close();

			assertTrue(result);
		} finally {
			testDA.open();
			testDA.removeRegistered(buyerEmail);
			testDA.removeSale(dbSale);
			testDA.removeRegistered(sellerEmail);
			testDA.close();
		}
	}

	@Test
	// 2. KASUA: Erosketa egiteko, diru nahikorik ez
	// Espero den irteera: false
	public void test2_DiruNahikoaEz() {
		testDA.open();
		testDA.createRegistered(buyerEmail, "Eroslea", 0f);
		testDA.createRegistered(sellerEmail, "Saltzailea", 0f);
		Sale dbSale = testDA.addSaleToSeller(sellerEmail, validSale);
		testDA.close();

		try {
			sut.open();
			boolean result = sut.buyCart(buyerEmail, cart1);
			sut.close();
			assertFalse(result);
		} finally {
			testDA.open();
			testDA.removeRegistered(buyerEmail);
			testDA.removeSale(dbSale);
			testDA.removeRegistered(sellerEmail);
			testDA.close();
		}

	}

	@Test
	// 3. KASUA: Saskiko sale-a jadanik salduta dago
	// Espero den irteera: false
	public void test3_SaleSaldutaDago() {
		validSale.setSold(true);
		testDA.open();
		testDA.createRegistered(buyerEmail, "Eroslea", 50f);
		testDA.createRegistered(sellerEmail, "Saltzailea", 0f);
		Sale dbSale = testDA.addSaleToSeller(sellerEmail, validSale);
		testDA.close();

		try {
			sut.open();
			boolean result = sut.buyCart(buyerEmail, cart1);
			sut.close();

			assertFalse(result);
		} finally {
			testDA.open();
			testDA.removeRegistered(buyerEmail);
			testDA.removeSale(dbSale);
			testDA.removeRegistered(sellerEmail);
			testDA.close();
		}
	}
	
	@Test
	// 4. KASUA: buyerEmail NULL izatea
	// Espero den irteera: false
	public void test4_BuyerEmailNull() {
		testDA.open();
		testDA.createRegistered(sellerEmail, "Saltzailea", 0f);
		Sale dbSale = testDA.addSaleToSeller(sellerEmail, validSale);
		testDA.close();
		try {
			sut.open();
			boolean result = sut.buyCart(null, cart1);
			sut.close();

			assertFalse(result);
		} finally {
			testDA.open();
			testDA.removeSale(dbSale);
			testDA.removeRegistered(sellerEmail);
			testDA.close();
		}
	}

	@Test
	// 5. KASUA: buyerEmail formato desegokia edukitzea
	// Espero den irteera: false
	public void test5_BuyerEmailFormatoDesdegokia() {
		testDA.open();
		testDA.createRegistered(sellerEmail, "Saltzailea", 0f);
		Sale dbSale = testDA.addSaleToSeller(sellerEmail, validSale);
		testDA.close();
		try {
			sut.open();
			boolean result = sut.buyCart("assd", cart1);
			sut.close();

			assertFalse(result);
		} finally {
			testDA.open();
			testDA.removeSale(dbSale);
			testDA.removeRegistered(sellerEmail);
			testDA.close();
		}
	}

	@Test
	// 6. KASUA: buyerEmail DBan ez egotea
	// Espero den irteera: false
	public void test6_BuyerEmailDBEz() {
		testDA.open();
		testDA.createRegistered(sellerEmail, "Saltzailea", 0f);
		Sale dbSale = testDA.addSaleToSeller(sellerEmail, validSale);
		testDA.close();
		try {
			sut.open();
			boolean result = sut.buyCart(buyerEmail, cart1);
			sut.close();
	
			assertFalse(result);
		} finally {
			testDA.open();
			testDA.removeSale(dbSale);
			testDA.removeRegistered(sellerEmail);
			testDA.close();
		}
	}
	

	@Test
	// 7. KASUA: cart NULL izatea
	// Espero den irteera: false
	public void test7_CartNull() {
		testDA.open();
		testDA.createRegistered(buyerEmail, "Eroslea", 50f);
		testDA.close();

		try {
			sut.open();
			boolean result = sut.buyCart(buyerEmail, null);
			sut.close();
			assertFalse(result);
		} finally {
			testDA.open();
			testDA.removeRegistered(buyerEmail);
			testDA.close();
		}
	}

	@Test
	// 8. KASUA: Saskia hutsik egotea
	// Espero den irteera: false
	public void test8_saskiaHutsik() {
		testDA.open();
		testDA.createRegistered(buyerEmail, "Eroslea", 50f);
		testDA.close();

		try {
			sut.open();
			boolean result = sut.buyCart(buyerEmail, cart2);
			sut.close();
			assertTrue(result);
		} finally {
			testDA.open();
			testDA.removeRegistered(buyerEmail);
			testDA.close();
		}
	}

	@Test
	// 9. KASUA: Saskiko sale-a ez dago DBan
	// Espero den irteera: false
	public void test9_SaleEzDagoDB() {
		testDA.open();
		testDA.createRegistered(buyerEmail, "Eroslea", 50f);
		testDA.close();

		try {
			sut.open();
			boolean result = sut.buyCart(buyerEmail, cart1);
			sut.close();
			assertFalse(result);
		} finally {
			testDA.open();
			testDA.removeRegistered(buyerEmail);
			testDA.close();
		}
	}
}
