package testOperations;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import configuration.ConfigXML;
import domain.Sale;
import domain.Registered;
 
public class TestDataAccess {
	protected EntityManager db;
	protected EntityManagerFactory emf;

	ConfigXML c = ConfigXML.getInstance();

	public TestDataAccess() {
		System.out.println("TestDataAccess created");
	}

	public void open() {
		String fileName = c.getDbFilename();
		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:" + fileName);
			db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("javax.persistence.jdbc.user", c.getUser());
			properties.put("javax.persistence.jdbc.password", c.getPassword());

			emf = Persistence.createEntityManagerFactory(
					"objectdb://" + c.getDatabaseNode() + ":" + c.getDatabasePort() + "/" + fileName, properties);

			db = emf.createEntityManager();
		}
		System.out.println("TestDataAccess opened");
	}

	public void close() {
		db.close();
		System.out.println("TestDataAccess closed");
	}


	// METODOAK REGISTERED ETA ISFAVORITE TESTENTZAT

	/**
	 * Erabiltzaile bat sortzen du datu-basean. Testetarako pasahitza eta dirua lehenetsita daude.
	 */
	public Registered createRegistered(String email, String name) {
		System.out.println(">> TestDataAccess: createRegistered");
		Registered user = null;
		db.getTransaction().begin();
		try {
			// Registered klasearen eraikitzailea: (email, name, pass, moneyKop)
			user = new Registered(email, name, "12345", 0f);
			db.persist(user);
			db.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public boolean removeRegistered(String email) {
		System.out.println(">> TestDataAccess: removeRegistered");
		Registered u = db.find(Registered.class, email);
		if (u != null) {
			db.getTransaction().begin();
			db.remove(u);
			db.getTransaction().commit();
			return true;
		} else {
			return false;
		}
	}

	public boolean existRegistered(String email) {
		return db.find(Registered.class, email) != null;
	}

	public void addSaleToFavorites(String email, Sale sale) {
		System.out.println(">> TestDataAccess: addSaleToFavorites");
		db.getTransaction().begin();
		try {
			Registered u = db.find(Registered.class, email);
			if (u != null) {
				// Salmenta gordetzen dugu JPAk ID automatiko bat esleitu diezaion
				if (sale.getSaleNumber() == null) {
					db.persist(sale);
				}
				// Erabiltzailearen gogokoetara gehitzen dugu
				u.addFavorite(sale); 
				db.merge(u);
				db.getTransaction().commit();
			}
		} catch (Exception e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			e.printStackTrace();
		}
	}
 
	// METODO GEHIGARRIAK BUYSALE ETA BUYCART TESTENTZAT 

	/**
	 * Erabiltzaile bat sortzen du datu-basean zehaztutako hasierako diru-kopuruarekin.
	 */
	public Registered createRegistered(String email, String name, float moneyKop) {
		System.out.println(">> TestDataAccess: createRegistered with money");
		Registered user = null;
		db.getTransaction().begin();
		try {
			user = new Registered(email, name, "12345", moneyKop);
			db.persist(user);
			db.getTransaction().commit();
		} catch (Exception e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * Saltzaile bati salmenta bat gehitzen dio testetarako.
	 */
	public Sale addSaleToSeller(String sellerEmail, Sale sale) {
		System.out.println(">> TestDataAccess: addSaleToSeller");
		db.getTransaction().begin();
		try {
			Registered seller = db.find(Registered.class, sellerEmail);
			if (seller != null) {
				Sale newSale = seller.addSale(sale.getTitle(), sale.getDescription(), sale.getStatus(), sale.getPrice(), sale.getPublicationDate(), null, sale.isSold());
				db.persist(newSale);
				db.getTransaction().commit();
				return newSale;
			}
			db.getTransaction().commit();
		} catch (Exception e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Salmenta bat zuzenean ezabatzen du datu-reset/garbiketarako.
	 */
	public boolean removeSale(Sale sale) {
		System.out.println(">> TestDataAccess: removeSale");
		if (sale == null || sale.getSaleNumber() == null) return false;
		db.getTransaction().begin();
		try {
			Sale s = db.find(Sale.class, sale.getSaleNumber());
			if (s != null) {
				db.remove(s);
				db.getTransaction().commit();
				return true;
			}
			db.getTransaction().commit();
		} catch (Exception e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Salmenta bat zuzenean persistitzen du saltzaileari lotu gabe (orphan sale kasuetarako).
	 */
	public void persistSaleDirectly(Sale sale) {
		System.out.println(">> TestDataAccess: persistSaleDirectly");
		db.getTransaction().begin();
		try {
			db.persist(sale);
			db.getTransaction().commit();
		} catch (Exception e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			e.printStackTrace();
		}
	}
}