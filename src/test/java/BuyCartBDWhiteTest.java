import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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
 * Kutxa Txuriko testak DB-a erabiliz(buyCart).
 */

public class BuyCartBDWhiteTest {
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
	// 1. eta 2. BIDEA: Salbuespen kudeaketa
	// Espero den irteera: false
	public void test1_2_SalbuespenaEtaRollback() {
		testDA.open();
		testDA.createRegistered(buyerEmail, "Jon", 50.0f);
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
	// 3. BIDEA: BuyerEmail ez dago DBan
	// Espero den irteera: false
	public void test3_ErosleaEzDagoDB() {
		sut.open();
		boolean result = sut.buyCart("ezDago@email.com", cart1);
		sut.close();
		assertFalse(result);
	}

	@Test
	// 4. BIDEA: cart hutsik eta buyerEmail saldo negatiboa du
	// Espero den irteera: false
	public void test4_cartHutsikBuyerEmailSaldoNegatiboa() {
		testDA.open();
		testDA.createRegistered(buyerEmail, "Jon", -10f);
		testDA.close();

		try {
			sut.open();
			boolean result = sut.buyCart(buyerEmail, cart2);
			sut.close();
			assertFalse(result);
		} finally {
			testDA.open();
			testDA.removeRegistered(buyerEmail);
			testDA.close();
		}
	}

	@Test
	// 5. BIDEA: buyerEmail DBan dago, baina ez du diru nahiko produktuak erosteko
	// (buyerEmail.moneyKop= 0, S1.price = 10)
	// Espero den irteera: false
	public void test5_DiruNahikoEz() {
		testDA.open();
		testDA.createRegistered(buyerEmail, "Eroslea", 0f);
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

	@Test
	// 6. BIDEA: buyerEmail DBan dago, baina saskia hutsik dago eta buyerEmail saldo positiboa du
	// Espero den irteera: true
	public void test6_SaskiaHutsik() {
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
	// 7. BIDEA: buyerEmail DBan dago, baina saskiko sale-a ez dago DBan
	// Espero den irteera: false
	public void test7_SaleEzDagoDB() {
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

	@Test
	// 8. BIDEA: buyerEmail DBan dago, S1 DBan dago, baina jada salduta dago (isSold = true).
	// Espero den irteera: false.
	public void test8_SaleSaldutaDago() {
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
	// 9. BIDEA: buyerEmail DBan, S1 DBan (isSold = false) eta buyerEmail ez ditu sale fabortioak.
	// Espero den irteera: true.
	public void test9_SaleErosiFaboritoaEz() {
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
	// 10. BIDEA: buyerEmail DBan, S1 DBan (isSold = false) eta buyerEmail sale-a faboritoetan du.
	// Espero den irteera: true
	public void test10_SaleErosiFaboritoaBai() {
		validSale.setSold(false);
		testDA.open();
		testDA.createRegistered(buyerEmail, "Eroslea", 50f);
		testDA.createRegistered(sellerEmail, "Saltzailea", 0f);
		Sale dbSale = testDA.addSaleToSeller(sellerEmail, validSale);
		testDA.addSaleToFavorites(buyerEmail, dbSale);
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
}
