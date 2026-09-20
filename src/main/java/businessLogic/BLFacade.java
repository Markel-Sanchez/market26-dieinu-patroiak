package businessLogic;

import java.io.File;
import java.util.Date;
import java.util.List;

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

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.awt.image.BufferedImage;
import java.awt.Image;

import gui.*;
/**
 * Interface that specifies the business logic.
 */
@WebService
public interface BLFacade  {
	  

	/**
	 * This method creates/adds a product to a seller
	 * 
	 * @param title of the product
	 * @param description of the product
	 * @param status 
	 * @param selling price
	 * @param category of a product
	 * @param publicationDate
	 * @return Sale
	 */
   @WebMethod
	public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail, File file) throws  FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException;
	
	
	/**
	 * This method retrieves the products that contain desc
	 * 
	 * @param desc the text to search
	 * @return collection of sales that contain desc 
	 */
	@WebMethod public List<Sale> getSales(String desc);
	
	/**
	 * 	 * This method retrieves the products that contain a desc text in a title and the publicationDate today or before
	 * 
	 * @param desc the text to search
	 * @param pubDate the date  of the publication date
	 * @return collection of sales that contain desc and published before pubDate
	 */
	@WebMethod public List<Sale> getPublishedSales(String desc, Date pubDate);

	
	/**
	 * This method calls the data access to initialize the database with some sellers and products.
	 * It is only invoked  when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	@WebMethod public void initializeBD();
	
	@WebMethod public Image downloadImage(String imageName);
	
	@WebMethod public User isLogged(String email, String pass);	
	
	@WebMethod public Registered register(String email, String log, String pass);
	
	@WebMethod public void addFavorite(String email, Sale sale);
	
	@WebMethod public boolean isFavorite(String email, Sale s);
	
	@WebMethod public void removeFavorite(String email, Sale s);
	
	@WebMethod public String buySale(Sale s, String sellerEmail);
	
	@WebMethod public void addMoney(String email, float amount);
	
	@WebMethod public void quitMoney(String email, float amount);
	
	@WebMethod public void putComplaint(String email, Sale s, String deskripzioa);	
	
	@WebMethod public Registered getUserByEmail(String email);
	
	@WebMethod public List<Complaint> getAdminComplaints();
	
	@WebMethod public void sendComplaintToAdmin(Sale s);
	
	@WebMethod public boolean isAdmin(String email);
	
	@WebMethod public void declineComplaint(Sale s);
	
	@WebMethod public void acceptComplaint(Sale s);
	
	@WebMethod public void addToCart(String email, Sale s);
	
	@WebMethod public void removeFromCart(String email, Sale s);
	
	@WebMethod public boolean buyCart(String buyerEmail, List<Sale> cart);
	
	@WebMethod public void createOrder(String description, String title, float priceMin, float priceMax, String email);
	
	@WebMethod public List<Order> getAllOrders();
	
	@WebMethod public void removeOrder(Order order);
	
	@WebMethod public List<Review> getAllReviews(); 
	
	@WebMethod public void createReview(Sale sale, String sellerEmail, String buyerEmail, int stars, String comment);
}