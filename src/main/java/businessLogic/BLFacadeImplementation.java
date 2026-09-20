package businessLogic;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import dataAccess.DataAccess;
import domain.Complaint;
import domain.Movement;
import domain.Order;
import domain.Sale;
import domain.User;
import domain.Registered;
import domain.Review;
import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;

import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * It implements the business logic as a web service.
 */
@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation implements BLFacade {
	private static final int baseSize = 160;

	private static final String basePath = "src/main/resources/images/";
	DataAccess dbManager;

	public BLFacadeImplementation() {
		System.out.println("Creating BLFacadeImplementation instance");
		dbManager = new DataAccess();
	}

	public BLFacadeImplementation(DataAccess da) {
		System.out.println("Creating BLFacadeImplementation instance with DataAccess parameter");
		dbManager = da;
	}

	/**
	 * {@inheritDoc}
	 */
	@WebMethod
	public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail,
			File file) throws FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException {
		dbManager.open();
		Sale product = dbManager.createSale(title, description, status, price, pubDate, sellerEmail, file);
		dbManager.close();
		return product;
	};

	/**
	 * {@inheritDoc}
	 */
	@WebMethod
	public List<Sale> getSales(String desc) {
		dbManager.open();
		List<Sale> rides = dbManager.getSales(desc);
		dbManager.close();
		return rides;
	}

	/**
	 * {@inheritDoc}
	 */
	@WebMethod
	public List<Sale> getPublishedSales(String desc, Date pubDate) {
		dbManager.open();
		List<Sale> rides = dbManager.getPublishedSales(desc, pubDate);
		dbManager.close();
		return rides;
	}

	/**
	 * {@inheritDoc}
	 */
	@WebMethod
	public BufferedImage getFile(String fileName) {
		return dbManager.getFile(fileName);
	}

	public void close() {
		DataAccess dB4oManager = new DataAccess();
		dB4oManager.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@WebMethod
	public void initializeBD() {
		dbManager.open();
		dbManager.initializeDB();
		dbManager.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@WebMethod
	public Image downloadImage(String imageName) {
		File image = new File(basePath + imageName);
		try { //Erroreak sahiesteko
			return ImageIO.read(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@WebMethod
	public User isLogged(String mail, String pass) {
		dbManager.open();
		User b = dbManager.isLogged(mail, pass);
		dbManager.close();
		return b;
	}

	@WebMethod
	public Registered register(String mail, String log, String pass) {
		dbManager.open();
		Registered b = dbManager.register(mail, log, pass);
		dbManager.close();
		return b;
	}

	@WebMethod
	public void addFavorite(String email, Sale sale) {
		dbManager.addFavorite(email, sale);
	}

	@WebMethod
	public boolean isFavorite(String email, Sale s) {
		return dbManager.isFavorite(email, s);
	}

	@WebMethod
	public void removeFavorite(String email, Sale s) {
		dbManager.removeFavorite(email, s);
	}

	@WebMethod
	public String buySale(Sale s, String email) {
		dbManager.open();
		try { //Erroreak sahiesteko
			return dbManager.buySale(s, email);
		} finally {
			dbManager.close();
		}
	}

	@WebMethod
	public void addMoney(String email, float amount) {
		dbManager.open();
		dbManager.addMoney(email, amount);
		dbManager.close();
	}

	@WebMethod
	public void quitMoney(String email, float amount) {
		dbManager.open();
		dbManager.quitMoney(email, amount);
		dbManager.close();
	}

	@WebMethod
	public void putComplaint(String email, Sale s, String deskripzioa) {
		dbManager.open();
		dbManager.putComplaint(email, s, deskripzioa);
		dbManager.close();
	}

	@WebMethod
	public Registered getUserByEmail(String email) {
		dbManager.open();
		Registered regis = dbManager.getUserByEmail(email);
		dbManager.close();
		return regis;
	}

	@WebMethod
	public List<Complaint> getAdminComplaints() {
		dbManager.open();
		List<Complaint> comp = dbManager.getAdminComplaints();
		dbManager.close();
		return comp;
	}

	@WebMethod
	public void sendComplaintToAdmin(Sale s) {
		dbManager.open();
		dbManager.sendComplaintToAdmin(s);
		dbManager.close();
	}

	@WebMethod
	public boolean isAdmin(String email) {
		dbManager.open();
		boolean emaitza = dbManager.isAdmin(email);
		dbManager.close();
		return emaitza;
	}

	@WebMethod
	public void declineComplaint(Sale s) {
		dbManager.open();
		dbManager.declineComplaint(s);
		dbManager.close();
	}

	@WebMethod
	public void acceptComplaint(Sale s) {
		dbManager.open();
		dbManager.acceptComplaint(s);
		dbManager.close();
	}
	
	@WebMethod 
	public void addToCart(String email, Sale s) {
		dbManager.open();
		dbManager.addToCart(email, s);
		dbManager.close();
	}
	
	@WebMethod 
	public void removeFromCart(String email, Sale s) {
		dbManager.open();
		dbManager.removeFromCart(email, s);
		dbManager.close();
	}
	
	@WebMethod 
	public boolean buyCart(String buyerEmail, List<Sale> cart) {
		dbManager.open();
		boolean emaitza = dbManager.buyCart(buyerEmail, cart);
		dbManager.close();
		return emaitza;
	}
	
	@WebMethod
	public void createOrder(String description, String title, float priceMin, float priceMax, String email) {
		dbManager.open();
	    dbManager.createOrder(description, title, priceMin, priceMax, email);
	    dbManager.close();
	}
	
	@WebMethod 
	public List<Order> getAllOrders() {
		dbManager.open();
		List<Order> or = dbManager.getAllOrders();
		dbManager.close();
		return or;
	}
	
	@WebMethod
	public void removeOrder(Order order) {
		dbManager.open();
		dbManager.removeOrder(order);
		dbManager.close();
	}
	
	@WebMethod  
	public List<Review> getAllReviews() {
		dbManager.open();
		List<Review> or = dbManager.getAllReviews();
		dbManager.close();
		return or;
	}
	
	@WebMethod
	public void createReview(Sale sale, String sellerEmail, String buyerEmail, int stars, String comment) {
		dbManager.open();
	    dbManager.createReview(sale, sellerEmail, buyerEmail, stars, comment);
	    dbManager.close();
	}
}