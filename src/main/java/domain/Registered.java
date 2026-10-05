package domain;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Registered extends User implements Serializable{

	private static final long serialVersionUID = 1L;
	private float moneyKop;
	@XmlIDREF
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<Sale> sales = new ArrayList<Sale>(); // Produktuen lista

	@XmlIDREF
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<Sale> favorite = new ArrayList<Sale>(); // Faboritoen lista

	@XmlIDREF
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<Sale> purchasedSales = new ArrayList<Sale>(); // Erositakoen lista

	@XmlIDREF
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<Movement> movementList = new ArrayList<Movement>(); // Mugimendu guztien lista

	@XmlIDREF
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<Complaint> receivedComplaintList = new ArrayList<Complaint>(); // Jasotako erreklamazioen lista

	@XmlIDREF
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<Complaint> sentComplaintList = new ArrayList<Complaint>(); // Egindako erreklamazioen lista

	@XmlIDREF
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<Order> orders = new ArrayList<Order>(); // Egindako eskaeren lista

	@XmlIDREF
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<Sale> cart = new ArrayList<Sale>(); // Karritoan sartutako sale-ak

	@XmlIDREF
	@OneToMany(mappedBy = "reviewee", cascade = CascadeType.ALL)
	private List<Review> reviewsReceived = new ArrayList<Review>(); // Jasotako review-en lista

	@XmlIDREF
	@OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL)
	private List<Review> reviewsWritten = new ArrayList<Review>(); // Egindako review-en lista

	public Registered() {
		super();
		this.sales = new ArrayList<Sale>();
		this.favorite = new ArrayList<Sale>();
		this.purchasedSales = new ArrayList<Sale>();
		this.movementList = new ArrayList<Movement>();
		this.receivedComplaintList = new ArrayList<Complaint>();
		this.sentComplaintList = new ArrayList<Complaint>();
		this.orders = new ArrayList<Order>();
		this.cart = new ArrayList<Sale>();
		this.reviewsReceived = new ArrayList<Review>();
		this.reviewsWritten = new ArrayList<Review>();
	}

	public Registered(String email, String name, String pass, float moneyKop) {
		super(email, name, pass);
		this.moneyKop = moneyKop;
		this.sales = new ArrayList<Sale>();
		this.favorite = new ArrayList<Sale>();
		this.purchasedSales = new ArrayList<Sale>();
		this.movementList = new ArrayList<Movement>();
		this.receivedComplaintList = new ArrayList<Complaint>();
		this.sentComplaintList = new ArrayList<Complaint>();
		this.orders = new ArrayList<Order>();
		this.cart = new ArrayList<Sale>();
	}

	public List<Sale> getSales() {
		if (this.sales == null) {
			this.sales = new ArrayList<Sale>();
		}
		return this.sales;
	}

	public List<Sale> getFavorites() {
		if (this.favorite == null) {
			this.favorite = new ArrayList<Sale>();
		}
		return favorite;
	}

	public List<Sale> getPurchasedSales() {
		if (this.purchasedSales == null)
			this.purchasedSales = new ArrayList<Sale>();
		return purchasedSales;
	}

	public List<Movement> getMovementList() {
		if (this.movementList == null)
			this.movementList = new ArrayList<Movement>();
		return movementList;
	}

	public List<Complaint> getReceivedComplaintList() {
		if(this.receivedComplaintList == null)
			this.receivedComplaintList = new ArrayList<Complaint>();
		return receivedComplaintList;
	}

	public List<Complaint> getSentComplaintList() {
		if(this.sentComplaintList == null)
			this.sentComplaintList = new ArrayList<Complaint>();
		return sentComplaintList;
	}

	public float getMoneyKop() {
		return moneyKop;
	}

	public void setMoneyKop(float moneyKop) {
		this.moneyKop = moneyKop;
	}

	public List<Order> getOrders() {
		if(this.orders == null)
			this.orders = new ArrayList<Order>();
		return orders;
	}

	public List<Sale> getCart() {
		if(this.cart == null)
			this.cart = new ArrayList<Sale>();
		return cart;
	}

	public String toString() {
		return getEmail() + ";" + getName() + ";" + getSales();
	}

	/**
	 * This method creates/adds a sale to a seller
	 * 
	 * @param title           of the sale
	 * @param description     of the sale
	 * @param status
	 * @param selling         price
	 * @param publicationDate
	 * @return Sale
	 */
	public Sale addSale(String title, String description, int status, float price, Date pubDate, File file,
			boolean sold) {

		if (this.sales == null) {
			this.sales = new ArrayList<Sale>();
		}
		Sale s = new Sale(title, description, status, price, pubDate, file, this, sold);
		this.sales.add(s);
		return s;
	}

	/**
	 * This method checks if the ride already exists for that driver
	 * 
	 * @param from the origin location
	 * @param to   the destination location
	 * @param date the date of the ride
	 * @return true if the ride exists and false in other case
	 */
	public boolean doesSaleExist(String title) {
		if (this.sales == null) {
			this.sales = new ArrayList<Sale>();
			return false;
		}
		for (int i = 0; i < this.sales.size(); i++) {
			Sale s = this.sales.get(i);
			if (s != null && s.getTitle() != null && s.getTitle().equals(title)) {
				return true;
			}
		}
		return false;
	}

	public void addFavorite(Sale sale) {
		if (this.favorite == null) {
			this.favorite = new ArrayList<Sale>();
		}
		if (!favorite.contains(sale)) {
			favorite.add(sale);
		}
	}

	public void addPurchasedSale(Sale sale) {
		this.getPurchasedSales().add(sale);
	}

	public boolean isOnCart(Sale s) {
		if (this.cart == null || s == null)
			return false;
		for (Sale item : this.cart) {
			if (item.getSaleNumber().equals(s.getSaleNumber())) {
				return true;
			}
		}
		return false;
	}

	public boolean isEmptyCart() {
		return this.cart.isEmpty();
	}

	public boolean isSameSeller(Sale s) {
		return this.cart.get(0).getUser().getEmail().equals(s.getUser().getEmail());
	}

	public void addToCart(Sale s) {
		this.cart.add(s);
	}

	public void addReceivedReview(Review review) {
		this.reviewsReceived.add(review);
	}

	public List<Review> getReviewsReceived() {
		return reviewsReceived;
	}

	public void addWrittenReview(Review review) {
		this.reviewsWritten.add(review);
	}

	public List<Review> getReviewsWritten() {
		return reviewsWritten;
	}
}