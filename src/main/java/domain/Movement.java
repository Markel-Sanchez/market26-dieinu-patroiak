package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Movement implements Serializable{
	
	@Id @GeneratedValue
    private Integer movementID;
    private String description;
    private float amount; 
    private Date movementDate;
    private Registered user;

    public Movement(String description, float amount, Registered user) { 
        this.description = description;
        this.amount = amount;
        this.user = user;
        this.movementDate = new Date();
    }
    
    public Movement() {
    	super();
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Date getMovementDate() {
		return movementDate;
	}

	public void setMovementDate(Date movementDate) {
		this.movementDate = movementDate;
	}

	public Registered getUser() {
		return user;
	}

	public void setUser(Registered user) {
		this.user = user;
	}

	public Integer getMovementID() {
		return movementID;
	}

	public void setMovementID(Integer movementID) {
		this.movementID = movementID;
	}
}