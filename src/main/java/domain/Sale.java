package domain;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Sale implements Serializable {
	@XmlID
	@Id
	@XmlJavaTypeAdapter(IntegerAdapter.class)
	@GeneratedValue
	private Integer saleNumber;
	private String title;
	private String description;
	private int status;
	private float price;
	private Date pubDate;
	private String fileName;
	private boolean alreadyComplaint;
	@OneToOne(mappedBy = "sale", cascade = CascadeType.ALL)
	private Complaint complaint;
	@OneToOne(mappedBy = "sale") 
	private Review review; 
	private Registered user;
	private boolean sold; // jakiteko ea erosita dagoen

	public Sale() {
		super();
	}

	public Sale(String title, String description, int status, float price, Date pubDate, File file, Registered user, boolean sold) {
		super();
		this.title = title;
		this.description = description;
		this.status = status;
		this.price = price;
		this.pubDate = pubDate;
		this.sold = sold;
		if (file != null) {
			this.fileName = file.getName();
			try {
				BufferedImage img1 = ImageIO.read(file);

				String path = "src/main/resources/images/";
				File outputfile = new File(path + file.getName());

				ImageIO.write(img1, "png", outputfile); // ignore returned boolean

			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}

		this.user = user;

	}

	public boolean isAlreadyComplaint() {
		return alreadyComplaint;
	}

	public void setAlreadyComplaint(boolean alreadyComplaint) {
		this.alreadyComplaint = alreadyComplaint;
	}

	/**
	 * Get the number of the sale
	 * 
	 * @return the sale number
	 */
	public Integer getSaleNumber() {
		return saleNumber;
	}

	/**
	 * Set a number to a sale
	 * 
	 * @param sale Number to be set
	 */
	public void setSaleNumber(Integer saleNumber) {
		this.saleNumber = saleNumber;
	}

	/**
	 * Get the title of the sale
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title of the sale
	 * 
	 * @param title to be set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the description of the sale
	 * 
	 * @return the sale description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the sale
	 * 
	 * @param description to be set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the status of the sale
	 * 
	 * @return the sale status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set the status of the sale
	 * 
	 * @param status to be set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Get the price of the sale
	 * 
	 * @return the price description
	 */
	public float getPrice() {
		return price;
	}

	/**
	 * Set the price of the sale
	 * 
	 * @param price to be set
	 */
	public void setPrice(float price) {
		this.price = price;
	}

	/**
	 * Get the publication date of the sale
	 * 
	 * @return the publication date
	 */
	public Date getPublicationDate() {
		return pubDate;
	}

	/**
	 * Set the publication date of the sale
	 * 
	 * @param publication date to be set
	 */
	public void setPublicationDate(Date publicationDate) {
		this.pubDate = publicationDate;
	}

	/**
	 * Get the seller of a sale
	 * 
	 * @return the associated seller
	 */
	public Registered getUser() {
		return user;
	}

	/**
	 * Set the seller of a sale
	 * 
	 * @param seller to assign to the sale
	 */
	public void setUser(Registered user) {
		this.user = user;
	}

	/**
	 * Get the file of a sale
	 * 
	 * @return the associated file
	 */
	public String getFile() {
		return fileName;
	}

	public boolean isSold() {
		return sold;
	}

	public void setSold(boolean sold) {
		this.sold = sold;
	}

	public String toString() {
		return saleNumber + ";" + title + ";" + price;
	}

	public Complaint getComplaint() {
		return complaint;
	}

	public void setComplaint(Complaint complaint) {
		this.complaint = complaint;
	}
	
	public Review getReview() { 
		return review; 
		} 

	public void setReview(Review review) { 
		this.review = review; 
		} 
}