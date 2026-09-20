package domain;

import java.io.Serializable;

import java.util.Date;

import javax.persistence.Entity;

import javax.persistence.GeneratedValue;

import javax.persistence.Id;

import javax.persistence.ManyToOne;

import javax.persistence.OneToOne;

import javax.xml.bind.annotation.XmlAccessType;

import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Review implements Serializable {

	@Id
	@GeneratedValue
	private Integer reviewId;
	private int stars; // 1 eta 5 tartean
	private String comment;
	private Date date;

	@ManyToOne
	private Registered reviewer; //Egiten duenak

	@ManyToOne
	private Registered reviewee; //Jasotzen duenak

	@OneToOne
	private Sale sale; //Sale-a

	public Review() {
		super();
	}

	public Review(Registered reviewer, Registered reviewee, Sale sale, int stars, String comment) {
		this.reviewer = reviewer;
		this.reviewee = reviewee;
		this.sale = sale;
		this.stars = stars;
		this.comment = comment;
		this.date = new Date();
	}

	public Integer getReviewId() {
		return reviewId;
	}

	public void setReviewId(Integer reviewId) {
		this.reviewId = reviewId;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Registered getReviewer() {
		return reviewer;
	}

	public void setReviewer(Registered reviewer) {
		this.reviewer = reviewer;
	}

	public Registered getReviewee() {
		return reviewee;
	}

	public void setReviewee(Registered reviewee) {
		this.reviewee = reviewee;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}
}